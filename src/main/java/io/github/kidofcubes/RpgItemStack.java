package io.github.kidofcubes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.kidofcubes.managers.StatManager;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.StringTag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private static CraftPersistentDataContainer getPersistentDataContainer(ItemStack itemstack){
        try {
            System.out.println("GETTING THE PERSISTENT DATA CONTAINER OF "+itemstack.hashCode());
            ItemMeta itemMeta = ((ItemMeta) itemMetaField.get(itemstack));
            if(itemMeta==null){
                System.out.println("WAS NO PREVIOUS ITEMMETA FOR "+itemstack.hashCode());
                itemMeta=itemstack.getItemMeta();
                System.out.println("THE NEW META WE MADE IS "+itemMeta);
                if(itemMeta==null){
                    throw new IllegalStateException("Cannot attach RpgItem to  " + itemstack.getType());
                }
                itemMetaField.set(itemstack,itemMeta);
                System.out.println("WE GOT THE META AGAIN AND ITS "+((ItemMeta) itemMetaField.get(itemstack)));
                itemstack.setItemMeta(itemstack.getItemMeta());
                System.out.println("WE GOT THE META AGAIN AGAIN AND ITS "+((ItemMeta) itemMetaField.get(itemstack)));
                ItemMeta magick = itemstack.getItemMeta();
                magick.setDisplayName("among us");
                itemstack.setItemMeta(magick);
                System.out.println("WE GOT THE META AGAIN AGAIN AND ITS "+((ItemMeta) itemMetaField.get(itemstack)));
            }else{
                System.out.println("REAL META OF "+itemstack.hashCode()+" IS "+itemMeta);
            }
            CraftPersistentDataContainer container = (CraftPersistentDataContainer) itemMeta.getPersistentDataContainer();
            System.out.println("THE PERSISTENT DATA CONTAINER WE ARE GOING TO RETURN CODE IS "+container);
            return container;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    //group code into smth to do with craftpersistentdatacontainers

    public static void setRpgItemInstance(ItemStack itemstack, RpgItem rpgItem){
        CraftPersistentDataContainer dataContainer = getPersistentDataContainer(itemstack);
        System.out.println("OK WE ARE ABOUT TO START "+dataContainer);
        dataContainer.getRaw().put(RpgObject.metadataKey.asString(), new RpgObjectTag(rpgItem));
        System.out.println("ADDED THE TAG WTF "+dataContainer);
        ((RpgObjectHolder) dataContainer).setObject(rpgItem);
        System.out.println("VERIFY THE OBJECT OF DATACONTAINER "+((RpgObjectHolder) dataContainer)+ " IS: "+((RpgObjectHolder) dataContainer).getObject());
        System.out.println("SET THE OBJECT OF "+dataContainer + " ON "+itemstack.hashCode());
    }

    public static void setRpgItemType(ItemStack itemstack, NamespacedKey identifier) throws ClassNotFoundException{
        CraftPersistentDataContainer persistentDataContainer = getPersistentDataContainer(itemstack);
        StringTag originalType = ((StringTag)persistentDataContainer.getRaw().get(RpgObject.typeStorageKey.asString()));
        if(originalType!=null){
            if(originalType.getAsString().equals(identifier.asString())){
                return;
            }
        }
        if(identifier==null&&originalType==null){
            return;
        }
        if(RpgRegistry.containsItemType(identifier)){
            persistentDataContainer.getRaw().put(RpgObject.typeStorageKey.asString(),StringTag.valueOf(identifier.asString()));

            if(((RpgObjectHolder) persistentDataContainer).getObject()!=null){ //if its already loaded, reload it
                setRpgItemInstance(itemstack, RpgRegistry.getItemType(identifier).apply(itemstack).loadFromJson(((RpgObjectHolder) persistentDataContainer).getObject().toJson()));
            }
        }else{
            throw new ClassNotFoundException("Couldn't load RpgItem type "+identifier.asString()+" from registry");
        }
    }

    public static RpgItem getRpgItemInstance(ItemStack itemstack) throws ClassNotFoundException { //if itemstack has a type already, init that type instead, if not, init default
        System.out.println("GETTING ITEM INSTANCE OF "+itemstack.displayName());
        CraftPersistentDataContainer persistentDataContainer = getPersistentDataContainer(itemstack);
        if(((RpgObjectHolder)persistentDataContainer).getObject()==null){ //init object if not found
            System.out.println("DIDNT FIND PREVIOUS OBJECT");
            if(persistentDataContainer.getRaw().containsKey(RpgObject.typeStorageKey.toString())){ //has a special type
                System.out.println("HAS SPECIAL TYPE");
                NamespacedKey type=NamespacedKey.fromString(persistentDataContainer.getRaw().get(RpgObject.typeStorageKey.toString()).getAsString());
                if(RpgRegistry.containsItemType(type)){
                    if(persistentDataContainer.getRaw().containsKey(RpgObject.metadataKey.toString())) {
                        setRpgItemInstance(itemstack, RpgRegistry.getItemType(type).apply(itemstack).loadFromJson(
                                new String(((ByteArrayTag)((persistentDataContainer).getRaw().get(RpgObject.metadataKey.toString()))).getAsByteArray(), StandardCharsets.UTF_8)));
                    }else{
                        setRpgItemInstance(itemstack,RpgRegistry.getItemType(type).apply(itemstack));
                    }
                }else{
                    throw new ClassNotFoundException("Couldn't load RpgItem type "+type+" from registry");
                }
            }else{
                System.out.println("NO SPECIAL TYPE");
                if(persistentDataContainer.getRaw().containsKey(RpgObject.metadataKey.toString())) {
                    System.out.println("HAS PREVIOUS DATA");
                    setRpgItemInstance(itemstack, new RpgItemStack(itemstack).loadFromJson(
                            new String(((ByteArrayTag)((persistentDataContainer).getRaw().get(RpgObject.metadataKey.toString()))).getAsByteArray(), StandardCharsets.UTF_8)));
                }else{
                    System.out.println("NO PREVIOUS DATA, NEW ITEMSTACK GO");
                    setRpgItemInstance(itemstack,new RpgItemStack(itemstack));
                }
            }
        }
        System.out.println("OUR DATA CONTAINER IS STILL "+persistentDataContainer);
        System.out.println("RETURNING RPGITEM "+((RpgObjectHolder)persistentDataContainer).getObject().toJson());
        return (RpgItem) ((RpgObjectHolder)persistentDataContainer).getObject(); //it probably should be a rpgitem unless someone(probably me) broke something
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

}