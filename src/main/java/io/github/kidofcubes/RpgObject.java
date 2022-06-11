package io.github.kidofcubes;


import io.github.kidofcubes.events.RpgActivateStatEvent;
import io.github.kidofcubes.events.RpgEntityDamageByObjectEvent;
import io.github.kidofcubes.managers.RpgManager;
import io.github.kidofcubes.managers.StatManager;
import io.github.kidofcubes.types.DamageType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static io.github.kidofcubes.RpgPlugin.gson;
import static io.github.kidofcubes.RpgPlugin.plugin;

public abstract class RpgObject {
    //probably shouldnt use strings i think
    private final List<RpgClass> rpgClasses = new ArrayList<>();
    private final Map<String, Stat> stats = new HashMap<>();
    private final Map<String, Stat> classStats = new HashMap<>();
    String name;
    int level;

    private float mana; //todo implement mana
    UUID parentUUID;
    boolean temporary = false;
    private RpgEntity parent;
    private UUID uuid;

    /**
     * Check if this object is temporary (deleted on server restart, doesn't get saved)
     * @return If this object is temporary
     */
    public boolean isTemporary() {
        return temporary;
    }

    //region gettersetters

    /**
     * Gets the UUID of this object
     * @return The UUID of this object
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Sets the UUID of this object
     * @param uuid The new UUID
     */
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }



    public float getMana() {
        return mana;
    }

    public void setMana(float mana) {
        this.mana = mana;
    }


    /**
     * Clears all previous RpgClasses, and adds that class
     * @param rpgClass
     */
    public void setRpgClass(RpgClass rpgClass){
        rpgClasses.clear();
        addRpgClass(rpgClass);
    }
    public void addRpgClass(RpgClass rpgClass){
        rpgClasses.add(rpgClass);
        for (Stat stat : rpgClass.classStats()) {
            classStats.put(stat.getName(),stat);
        }
    }
    public void removeRpgClass(RpgClass rpgClass){
        if(rpgClasses.remove(rpgClass)){
            for (Stat stat : rpgClass.classStats()) {
                classStats.remove(stat.getName());
            }
        }
    }

    public boolean hasRpgClass(RpgClass rpgClass){
        return rpgClasses.contains(rpgClass);
    }

    //endregion

    /**
     * Gets the parent of this object (inherits relations and will attribute things to parent)
     * @return The parent of this object
     */
    @Nullable
    public RpgEntity getParent() {
        if (parent != null) {
            return parent;
        } else {
            if (parentUUID != null) {
                RpgEntity rpgEntityParent = RpgManager.getRpgEntity(parentUUID);
                if (rpgEntityParent != null) {
                    parent = rpgEntityParent;
                    return parent;
                }
            }
            return null;
        }
    }


    public void setParent(RpgEntity parent) {
        this.parent = parent;
        parentUUID = parent.getUUID();
    }

    /**
     * Adds a stat to the statMap (replaces stat with new level if already exists)
     * @param stat The stat
     */
    public void addStat(Stat stat) {
        stats.put(stat.getName(), stat);
    }

    /**
     * Removes a stat (if the stat is not found, won't do anything)
     * @param stat The stat name
     */
    public void removeStat(String stat) {
        stats.remove(stat);
    }

    /**
     * Gets the stat map
     * @return The stat map
     */
    public Map<String, Stat> getStatsMap() {
        return stats;
    }
    /**
     * Gets the stat map
     * @return The stat map
     */
    public List<Stat> getStats() {
        return getStatsMap().values().stream().toList();
    }

    /**
     * Gets this object's effective stats (for example, an RpgEntity's effective stats include stats of items in their inventory)
     * @return This object's effective stats
     */
    public Map<String, Stat> getEffectiveStatsMap() {
        return getStatsMap();
    }

    /**
     * Gets this object's effective stats (for example, an RpgEntity's effective stats include stats of items in their inventory)
     * @return This object's effective stats
     */
    public List<Stat> getEffectiveStats() {
        return getEffectiveStatsMap().values().stream().toList();
    }


    public boolean hasStat(String name) {
        return getEffectiveStatsMap().containsKey(name);
    }

    /**
     * Makes this object attack a RpgEntity victim with base damage
     * (Makes a RpgEntityDamageByObjectEvent with amount physical base damage,
     * @param amount The base damage of the attack
     * @param victim The victim of the attack
     */
    public void attack(double amount, RpgEntity victim) {
        attack(amount,victim,null);
    }
    public void attack(double amount, RpgEntity victim, List<Stat> extraStats){
        RpgEntityDamageByObjectEvent event = new RpgEntityDamageByObjectEvent(victim, DamageType.Physical, amount, this,extraStats);
        event.callEvent();
        victim.livingEntity.damage(event.getTotalDamage());
    }

    /**
     * Calls a RpgActivateStatEvent with this as the parent
     * @param statName The name of the stat to activate
     */
    public void activateStat(String statName) {
        getActivateStatEvent(List.of(statName)).callEvent();
    }

    public RpgActivateStatEvent getActivateStatEvent(List<String> statNames) {
        return new RpgActivateStatEvent(this, statNames);
    }

    public String toJson() {
        RpgObjectJsonContainer container = new RpgObjectJsonContainer();
        container.name = name;
        container.level = level;

        for (Map.Entry<String, Stat> entry : getStatsMap().entrySet()) {
            container.stats.put(entry.getKey(),entry.getValue().asContainer());
        }
        if(parentUUID!=null) {
            container.parentUUID = parentUUID.toString();
        }
        container.rpgClasses.addAll(rpgClasses);
        return gson.toJson(container);
    }

    protected void loadFromJson(String json) {
        RpgObjectJsonContainer container = gson.fromJson(json, RpgObjectJsonContainer.class);
        level = container.level;
        name = container.name;
        for (Map.Entry<String, Stat.StatContainer> entry : container.stats.entrySet()) {
            try {
                Stat stat = StatManager.getRegisteredStatByName(entry.getKey()).getDeclaredConstructor().newInstance();
                stat.setLevel(entry.getValue().level);
                stat.loadCustomData(entry.getValue().customData);
                addStat(stat);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        for (RpgClass entry : container.rpgClasses) {
            addRpgClass(entry);
        }
        if(container.parentUUID!=null) {
            parentUUID = UUID.fromString(container.parentUUID);
        }else{
            parentUUID = null;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof RpgObject otherRpgObject) {
            return getUUID().equals(otherRpgObject.getUUID());
        } else {
            return false;
        }
    }

    /**
     * Saves this object to the world
     */
    public abstract void save();

    public static class RpgObjectJsonContainer {
        public String name;
        public int level;
        public String parentUUID;
        public Map<String, Stat.StatContainer> stats = new HashMap<>();
        public List<RpgClass> rpgClasses = new ArrayList<>();
    }
}
