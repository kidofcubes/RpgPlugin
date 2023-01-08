package io.github.kidofcubes;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BaseRpgEntity extends CraftLivingEntity implements RpgEntity {

    public BaseRpgEntity(CraftServer server, LivingEntity entity) {
        super(server, entity);
        System.out.println("MADE A RPG ENTITY WITH TYPE "+entity.getType().toString());
    }
    private int level=0;
    private double mana=0;

    Map<String,RpgClass> rpgClasses = new HashMap<>();

//    private List<Stat,>

    @Override
    public String getName() {
        return "RPGENTITIY";
    }

    @Override
    public void setHealth(double health) {
        super.setHealth(health);
        System.out.println("RPG ENTITY HEALTH CHANGED");
    }

    @Override
    public void storeBukkitValues(CompoundTag c) {
        super.storeBukkitValues(c);
        c.putString("rpgentity",RpgPlugin.gson.toJson(toJson()));
        System.out.println("WE WRITE");
    }

    @Override
    public void readBukkitValues(CompoundTag c) {
        super.readBukkitValues(c);
        System.out.println("WE READ");
        System.out.println("does tag have rpgentity: "+(!c.getString("rpgentity").equals("")));
        if(!c.getString("rpgentity").equals("")){
            loadFromJson(RpgPlugin.gson.fromJson(c.getString("rpgentity"),JsonObject.class));
        }
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public UUID getUUID() {
        return getUniqueId();
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

    }

    @Override
    public boolean hasStat(String stat) {
        return false;
    }

    @Nullable
    @Override
    public Stat getStat(String stat) {
        return null;
    }

    @Override
    public List<Stat> getStats() {
        return List.of();
    }

    @Override
    public void removeStat(String stat) {

    }

    @Override
    public Map<Class<? extends Stat>, List<Stat>> getUsedStatsMap() {
        return null;
    }
}
