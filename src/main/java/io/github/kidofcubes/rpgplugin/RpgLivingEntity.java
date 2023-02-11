package io.github.kidofcubes.rpgplugin;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class RpgLivingEntity implements RpgEntity {
    @Override
    public void setRpgType(NamespacedKey namespacedKey) {
        getHolder(livingEntity).setSavedType(namespacedKey);
    }

    @Override
    public @NotNull NamespacedKey getRpgType() {
        return getHolder(livingEntity).getSavedType();
    }

    @NotNull
    public static RpgObjectTag getHolder(LivingEntity livingEntity){

        CraftPersistentDataContainer persistentDataContainer = (CraftPersistentDataContainer) livingEntity.getPersistentDataContainer();
        if(!persistentDataContainer.getRaw().containsKey(RpgObjectTag.RpgObjectTagKey.asString())){
            persistentDataContainer.getRaw().put(RpgObjectTag.RpgObjectTagKey.asString(),new CompoundTag());
        }
        if (!(persistentDataContainer.getRaw().get(RpgObjectTag.RpgObjectTagKey.toString()) instanceof RpgObjectTag)) {
            persistentDataContainer.getRaw().put(RpgObjectTag.RpgObjectTagKey.toString(), new RpgObjectTag((CompoundTag) persistentDataContainer.getRaw().get(RpgObjectTag.RpgObjectTagKey.toString())));
        }
        return (RpgObjectTag) persistentDataContainer.getRaw().get(RpgObjectTag.RpgObjectTagKey.toString());
    }

    @NotNull
    public static RpgEntity getInstance(LivingEntity livingEntity) { //if livingentity has a type already, init that type instead, if not, init default
        RpgObjectTag holder = getHolder(livingEntity);
        if(holder.getObject()==null){ //init object if not found
            NamespacedKey type = holder.getSavedType();
            if(RpgRegistry.containsTypeConstructor(RpgEntity.class,type)){
                holder.setObject(RpgRegistry.getTypeConstructor(RpgEntity.class,type).apply(livingEntity));
            }else{ //didnt find type function then init default
                holder.setObject(RpgRegistry.getTypeConstructor(RpgEntity.class, defaultTypeKey).apply(livingEntity));
            }
        }
        return (RpgEntity) holder.getObject();
    }

    public static void unloadInstance(LivingEntity livingEntity) {
        getHolder(livingEntity).unload();
    }

    protected final LivingEntity livingEntity;

    public RpgLivingEntity(LivingEntity livingEntity){
        this.livingEntity=livingEntity;
    }

    private int level=1;
    private double mana=0;

    Map<String,RpgClass> rpgClasses = new HashMap<>();

    Map<NamespacedKey,Stat> stats = new HashMap<>();

    @Override
    public String getName() {
        return "RpgLivingEntity";
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public UUID getUUID() {
        return livingEntity.getUniqueId();
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
    public @NotNull Map<NamespacedKey, List<Stat>> getUsedStats() {
        return Map.of();
    }

    @Override
    public RpgEntity self() {
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = RpgEntity.super.toJson();
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
    public RpgEntity loadFromJson(@NotNull JsonObject jsonObject) {
        jsonObject.remove("type");
        return RpgEntity.super.loadFromJson(jsonObject);
    }
}
