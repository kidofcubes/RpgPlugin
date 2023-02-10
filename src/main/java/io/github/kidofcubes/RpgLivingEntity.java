package io.github.kidofcubes;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


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
//        System.out.println("GOT THE INSTANCE OF "+livingEntity.getName());
        RpgObjectTag holder = getHolder(livingEntity);
        if(holder.getObject()==null){ //init object if not found
            NamespacedKey type = holder.getSavedType();
            if(RpgRegistry.containsTypeConstructor(RpgEntity.class,type)){
                holder.setObject(RpgRegistry.getTypeConstructor(RpgEntity.class,type).apply(livingEntity));
            }else{ //didnt find type function then init default
                holder.setObject(RpgRegistry.getTypeConstructor(RpgEntity.class,RpgObject.defaultTypeKey).apply(livingEntity));
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

    private int level=0;
    private double mana=0;

    Map<String,RpgClass> rpgClasses = new HashMap<>();

    Map<String,Stat> stats = new HashMap<>();

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
    public RpgEntity self() {
        return this;
    }
}
