package io.github.kidofcubes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.kidofcubes.managers.StatManager;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RpgItemStack implements RpgItem{

    private static final Field itemMetaField;

    static {
        try {
            itemMetaField = ItemStack.class.getDeclaredField("meta");
            itemMetaField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static String getType(ItemStack itemStack){
        StringTag stringTag = (StringTag) ((CraftPersistentDataContainer)itemStack.getItemMeta().getPersistentDataContainer()).getRaw().getOrDefault(RpgObject.typeStorageKey.asString(),null);
        if(stringTag!=null){
            return stringTag.getAsString();
        }else{
            return RpgObject.defaultTypeKey.asString();
        }
    }

    private static Object getTagHolder(ItemStack itemStack){
        if(itemStack instanceof CraftItemStack craftItemStack){
            if(craftItemStack.handle.getTag()==null){
                craftItemStack.handle.setTag(new CompoundTag());
            }
            return craftItemStack.handle.getTag();
        }else{ //assume its default itemstack
            try {
                ItemMeta itemMeta = (ItemMeta) itemMetaField.get(itemStack);
                if(itemMeta==null){
                    itemMeta=itemStack.getItemMeta();
                    itemStack.setItemMeta(itemMeta);
                }
                return ((CraftPersistentDataContainer)itemMeta.getPersistentDataContainer());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @NotNull
    private static RpgObjectHolder getHolder(ItemStack itemstack){
        Object tagHolder = getTagHolder(itemstack);
        if(tagHolder instanceof CompoundTag compoundTag){
            if(!compoundTag.contains(RpgObject.metadataKey.asString())){
                compoundTag.put(RpgObject.metadataKey.asString(),new RpgObjectTag());
            }
            if(!(compoundTag.get(RpgObject.metadataKey.toString()) instanceof RpgObjectTag)){
                compoundTag.put(RpgObject.metadataKey.toString(),new RpgObjectTag((ByteArrayTag) Objects.requireNonNull(compoundTag.get(RpgObject.metadataKey.toString()))));
            }
            return (RpgObjectTag) Objects.requireNonNull(compoundTag.get(RpgObject.metadataKey.asString()));
        }else if(tagHolder instanceof CraftPersistentDataContainer persistentDataContainer){
            ByteArrayTag tag = (ByteArrayTag) persistentDataContainer.getRaw().getOrDefault(RpgObject.metadataKey.asString(),null);
            if(tag==null){
                tag=new RpgObjectTag();
                persistentDataContainer.getRaw().put(RpgObject.metadataKey.asString(),tag);
            }

            if(!(tag instanceof RpgObjectTag)){
                tag = new RpgObjectTag(tag);
                persistentDataContainer.getRaw().put(RpgObject.metadataKey.toString(),tag);
            }
            return (RpgObjectHolder) tag;
        }
        throw new RuntimeException("this should never happen");
    }

    //group code into smth to do with craftpersistentdatacontainers

    public static void setRpgItemInstance(ItemStack itemstack, RpgItem rpgItem){
        RpgObjectHolder dataContainer = getHolder(itemstack);
        System.out.println("OK WE ARE ABOUT TO START "+dataContainer);
        dataContainer.setObject(rpgItem);
        System.out.println("ADDED THE TAG WTF "+dataContainer);
        System.out.println("VERIFY THE OBJECT OF DATACONTAINER "+((RpgObjectHolder) dataContainer)+ " IS: "+((RpgObjectHolder) dataContainer).getObject());
        System.out.println("SET THE OBJECT OF "+dataContainer + " ON "+itemstack.hashCode());
    }

    public static void setRpgItemType(ItemStack itemstack, @Nullable NamespacedKey identifier) throws ClassNotFoundException{
        if(identifier==null){
            identifier=defaultTypeKey;
        }
        String originalType = getType(itemstack);
        if(originalType.equals(identifier.asString())){
            return;
        }

        RpgObjectHolder holder = getHolder(itemstack);
        Object tagHolder = getTagHolder(itemstack);
        RpgItem newInstance = RpgRegistry.getItemType(identifier).apply(itemstack);

        if(holder.getJsonData()!=null){
            newInstance.loadFromJson(holder.getJsonData());
        }
        if(tagHolder instanceof CompoundTag compoundTag){
            compoundTag.put(RpgObject.typeStorageKey.asString(),StringTag.valueOf(identifier.asString()));
        }else if(tagHolder instanceof CraftPersistentDataContainer persistentDataContainer){
            persistentDataContainer.getRaw().put(RpgObject.typeStorageKey.asString(),StringTag.valueOf(identifier.asString()));
        }
    }

    public static RpgItem getRpgItemInstance(ItemStack itemstack) throws ClassNotFoundException { //if itemstack has a type already, init that type instead, if not, init default
        System.out.println("GETTING ITEM INSTANCE OF "+itemstack.displayName());
        RpgObjectHolder holder = getHolder(itemstack);
        String typeString=getType(itemstack);
        if((holder).getObject()==null){ //init object if not found
            System.out.println("DIDNT FIND PREVIOUS OBJECT");
            if(typeString!=null){ //has a special type
                System.out.println("HAS SPECIAL TYPE");
                NamespacedKey type=NamespacedKey.fromString(typeString);
                if(RpgRegistry.containsItemType(type)){
                    if(holder.getJsonData()!=null) {
                        setRpgItemInstance(itemstack, RpgRegistry.getItemType(type).apply(itemstack).loadFromJson(holder.getJsonData()));
                    }else{
                        setRpgItemInstance(itemstack,RpgRegistry.getItemType(type).apply(itemstack));
                    }
                }else{
                    throw new ClassNotFoundException("Couldn't load RpgItem type "+type+" from registry");
                }
            }else{
                System.out.println("NO SPECIAL TYPE");
                if(holder.getJsonData()!=null) {
                    System.out.println("HAS PREVIOUS DATA");
                    setRpgItemInstance(itemstack, new RpgItemStack(itemstack).loadFromJson(holder.getJsonData()));
                }else{
                    System.out.println("NO PREVIOUS DATA, NEW ITEMSTACK GO");
                    setRpgItemInstance(itemstack,new RpgItemStack(itemstack));
                }
            }
        }
        System.out.println("OUR DATA CONTAINER IS STILL "+holder);
        System.out.println("RETURNING RPGITEM "+holder.getObject().toJson());
        return (RpgItem) holder.getObject(); //it probably should be a rpgitem unless someone(probably me) broke something
    }

    private final ItemStack itemStack;
    private final UUID uuid;
    public RpgItemStack(ItemStack itemStack){
        this.itemStack=itemStack;
        uuid=UUID.randomUUID();
    }

    private int level=0;
    private double mana=0;

    Map<String,RpgClass> rpgClasses = new HashMap<>();

    Map<String,Stat> stats = new HashMap<>();

    @Override
    public String getName() {
        return "RPGOBJECT";
    }
    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level=level;
    }

    @Override
    public double getMana() {
        return mana;
    }

    @Override
    public void setMana(double mana) {
        this.mana=mana;
    }

    @Override
    public List<RpgClass> getRpgClasses() {
        return rpgClasses.values().stream().toList();
    }

    @Override
    public void addRpgClass(RpgClass rpgClass) {
        rpgClasses.put(rpgClass.getFullName(),rpgClass);
    }

    @Override
    public RpgClass getRpgClass(String rpgClass) {
        return rpgClasses.get(rpgClass);
    }

    @Override
    public void removeRpgClass(String rpgClass) {
        rpgClasses.remove(rpgClass);
    }

    @Override
    public boolean hasRpgClass(String rpgClass) {
        return rpgClasses.containsKey(rpgClass);
    }

    @Override
    public void use(RpgObject rpgObject) {

    }

    @Override
    public void stopUsing(RpgObject rpgObject) {

    }

    @Override
    public boolean usedBy(RpgObject rpgObject) {
        return false;
    }

    @Nullable
    @Override
    public RpgObject getParent() {
        return null;
    }

    @Override
    public void setParent(RpgObject parent) {

    }



    @Override
    public void addStat(Stat stat, boolean force) {
        stat.onAddStat(this);
        stat.onUseStat(this);
        stats.put(stat.getName(),stat);
    }

    @Override
    public boolean hasStat(String stat) {
        return stats.containsKey(stat);
    }

    @Override
    public @NotNull Stat getStat(String stat) {
        return stats.get(stat);
    }

    @Override
    public List<Stat> getStats() {
        return stats.values().stream().toList();
    }

    @Override
    public void removeStat(String stat) {
        System.out.println("REMOVING ITEM STAT");
        Stat statInstance = stats.remove(stat);
        if(statInstance!=null){
            statInstance.onRemoveStat();
            statInstance.onStopUsingStat();
        }
    }

    @Override
    public @NotNull Map<Class<? extends Stat>, List<Stat>> getUsedStats() {
        return Map.of();
    }

    @Override
    public RpgItem self() {
        return this;
    }
}