package io.github.kidofcubes.rpgplugin;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
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

    @Override
    public void setRpgType(NamespacedKey namespacedKey) {
        getHolder(itemStack).setSavedType(namespacedKey);
    }

    @Override
    public @NotNull NamespacedKey getRpgType() {
        return getHolder(itemStack).getSavedType();
    }

    @NotNull
    public static RpgObjectTag getHolder(ItemStack itemstack){
        if(itemstack instanceof CraftItemStack craftItemStack){
            if(craftItemStack.handle.getTag()==null){
                craftItemStack.handle.setTag(new CompoundTag());
            }
            if(!craftItemStack.handle.getTag().contains("PublicBukkitValues")){ //persistentDataContainer
                craftItemStack.handle.getTag().put("PublicBukkitValues",new CompoundTag());
            }
            CompoundTag tag = (CompoundTag) craftItemStack.handle.getTag().get("PublicBukkitValues");
//            System.out.println("WE ARE PUTTING IT IN PUBLIC BUKKIT VALUES");
            assert tag != null;
            if(!(tag.get(RpgObjectTag.RpgObjectTagKey.asString()) instanceof RpgObjectTag)){
//                System.out.println("IT WAS NOT A RPGOBJECTTAG");
                tag.put(RpgObjectTag.RpgObjectTagKey.asString(), RpgObjectTag.fromCompoundTag((CompoundTag) tag.get(RpgObjectTag.RpgObjectTagKey.asString())));
            }
//            System.out.println("THE TAGS NOW LOOK LIKE "+craftItemStack.handle.getTag().getAsString());
            return (RpgObjectTag) Objects.requireNonNull(tag.get(RpgObjectTag.RpgObjectTagKey.asString()));
        }else{ //assume its default itemstack
            try {
//                System.out.println("IT WAS NOT A CRAFTITEMSTACK");
                ItemMeta itemMeta = (ItemMeta) itemMetaField.get(itemstack);
                if(itemMeta==null){
                    itemMeta=itemstack.getItemMeta();
                    itemstack.setItemMeta(itemMeta);
                }
                Map<String, Tag> tag = ((CraftPersistentDataContainer)itemMeta.getPersistentDataContainer()).getRaw();
                if(!(tag.get(RpgObjectTag.RpgObjectTagKey.asString()) instanceof RpgObjectTag)){
                    tag.put(RpgObjectTag.RpgObjectTagKey.asString(), RpgObjectTag.fromCompoundTag((CompoundTag) tag.get(RpgObjectTag.RpgObjectTagKey.asString())));
                }
                return (RpgObjectTag) Objects.requireNonNull(tag.get(RpgObjectTag.RpgObjectTagKey.asString()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @NotNull
    public static RpgItem getInstance(ItemStack itemStack) throws ClassNotFoundException { //if livingentity has a type already, init that type instead, if not, init default
        if(itemStack.getType().isEmpty()){
            throw new RuntimeException("Cant attach a RpgItemStack on "+itemStack.getType());
        }
        RpgObjectTag holder = getHolder(itemStack);
        if(holder.getObject()==null){ //init object if not found
            NamespacedKey type = holder.getSavedType();
            if(RpgRegistry.containsTypeConstructor(RpgItem.class,type)){
                holder.setObject(RpgRegistry.getTypeConstructor(RpgItem.class,type).apply(itemStack));
            }else{ //didnt find type function then init default
                holder.setObject(RpgRegistry.getTypeConstructor(RpgItem.class,RpgObject.defaultTypeKey).apply(itemStack));
            }
        }
        return (RpgItem) holder.getObject();
    }

    public static void unloadInstance(ItemStack itemstack) {
        getHolder(itemstack).unload();
    }

    protected final ItemStack itemStack;
    private final UUID uuid;
    public RpgItemStack(ItemStack itemStack){
        if(itemStack.getType().isEmpty()){
            throw new RuntimeException("Cant init a RpgItemStack on "+itemStack.getType());
        }
        this.itemStack=itemStack;
        uuid=UUID.randomUUID();
    }

    protected int level=0;
    protected double mana=0;

    protected Map<String,RpgClass> rpgClasses = new HashMap<>();

    protected Map<NamespacedKey,Stat> stats = new HashMap<>();

    @Override
    public String getName() {
        return "RPG ItemStack";
    }
    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public UUID getRpgUUID() {
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

    protected Set<RpgObject> usedObjects = new HashSet<>();
    @Override
    public void use(RpgObject rpgObject) {
        if(!usedObjects.add(rpgObject)){
            return;
        }
        for(Stat stat : rpgObject.getStats()){
            stat.onStopUsingStat();
            stat.onUseStat(this);
        }
    }


    @Override
    public void stopUsing(RpgObject rpgObject) {
        if(!usedObjects.remove(rpgObject)){
            return;
        }
        for(Stat stat : rpgObject.getStats()){
            stat.onStopUsingStat();
            stat.onUseStat(rpgObject);
        }
    }

    @Override
    public @NotNull Map<NamespacedKey, List<Stat>> getUsedStatsMap() {
        Map<NamespacedKey,List<Stat>> map = new HashMap<>();
        for(RpgObject usedObject : usedObjects){
            for(Stat stat : usedObject.getStats()){
                map.putIfAbsent(stat.getIdentifier(),new ArrayList<>());
                map.get(stat.getIdentifier()).add(stat);
            }
        }
        for(Map.Entry<NamespacedKey,Stat> entry : stats.entrySet()){
            map.putIfAbsent(entry.getValue().getIdentifier(),new ArrayList<>());
            map.get(entry.getValue().getIdentifier()).add(entry.getValue());
        }
        return map;
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

    public ItemStack getItemStack(){
        return itemStack;
    }



    @Override
    public void addStat(NamespacedKey key, Stat stat) {
        stat.onAddStat(this);
        stat.onUseStat(this);
        stats.put(key,stat);
    }

    @Override
    public boolean hasStat(NamespacedKey key) {
        return stats.containsKey(key);
    }

    @Override
    public @NotNull Stat getStat(NamespacedKey key) {
        return stats.get(key);
    }

    @Override
    public List<Stat> getStats() {
        return stats.values().stream().toList();
    }

    @Override
    public void removeStat(NamespacedKey key) {
        Stat statInstance = stats.remove(key);
        if(statInstance!=null){
            statInstance.onRemoveStat();
            statInstance.onStopUsingStat();
        }
    }


    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = RpgItem.super.toJson();
        jsonObject.remove("type");
        if(getLevel()==0){
            jsonObject.remove("level");
        }
        if(getMana()==0){
            jsonObject.remove("mana");
        }
        return jsonObject;
    }

    @Override
    public RpgItem loadFromJson(@NotNull JsonObject jsonObject) {
        jsonObject.remove("type");
        return RpgItem.super.loadFromJson(jsonObject);
    }

    @Override
    public RpgItem getRpgInstance() {
        return this;
    }
}