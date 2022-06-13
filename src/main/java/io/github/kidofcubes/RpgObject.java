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

public abstract class RpgObject {
    //probably shouldnt use strings i think
    private final List<RpgClass> rpgClasses = new ArrayList<>();
    private final StatSet stats = new StatSet(this);
    String name;
    int level = 0;

    private double maxMana = 100;
    private double mana = 100;
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



    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        this.mana = mana;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
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
        stats.addStats(rpgClass.classStats(),false);

    }
    public void removeRpgClass(String rpgClass){
        for (int i = 0; i < rpgClasses.size(); i++) {
            if(rpgClasses.get(i).getFullName().equalsIgnoreCase(rpgClass)){
                stats.removeStats(rpgClasses.get(i).classStats(),false);
                rpgClasses.remove(i);
                break;
            }
        }
    }

    public boolean hasRpgClass(RpgClass rpgClass){
        for (RpgClass check: rpgClasses) {
            if(check.getFullName().equalsIgnoreCase(rpgClass.getFullName())){
                return true;
            }
        }
        return false;
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
        stats.addStat(stat,true);
    }

    /**
     * Removes a stat (if the stat is not found, won't do anything)
     * @param stat The stat name
     */
    public void removeStat(String stat) {
        stats.removeStat(stat);
    }

    public StatSet getStats() {
        return stats;
    }

    /**
     * Gets this object's effective stats (for example, an RpgEntity's effective stats include stats of items in their inventory)
     * @return This object's effective stats
     */
    public StatSet getEffectiveStats() {
        StatSet effectiveStats = new StatSet(this);
        effectiveStats.addStats(getStats(),false);
        return effectiveStats;
    }


    public boolean hasStat(String name) {
        return getEffectiveStats().hasStat(name);
    }

    /**
     * Makes this object attack a RpgEntity victim with base damage
     * (Makes a RpgEntityDamageByObjectEvent with amount physical base damage,
     * @param amount The base damage of the attack
     * @param victim The victim of the attack
     */
    public RpgEntityDamageByObjectEvent attack(double amount, RpgEntity victim) {
        return attack(DamageType.PHYSICAL, amount,victim,null);
    }
    public RpgEntityDamageByObjectEvent attack(DamageType damageType, double amount, RpgEntity victim, List<Stat> extraStats){
        return victim.damage(damageType,amount,this,extraStats);
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
        return gson.toJson(toContainer());
    }

    public RpgObjectJsonContainer toContainer(){
        RpgObjectJsonContainer container = new RpgObjectJsonContainer();
        container.name = name;
        container.level = level;
        container.mana = getMana();
        container.maxMana = getMaxMana();

        for (Map.Entry<String, Stat> entry : getStats().statMap.entrySet()) {
            container.stats.put(entry.getKey(),entry.getValue().asContainer());
        }
        if(parentUUID!=null) {
            container.parentUUID = parentUUID.toString();
        }
        for (RpgClass temp: rpgClasses) {
            container.rpgClasses.add(temp.getFullName());
        }
        return container;
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
        for (String entry : container.rpgClasses) {
            try {
                addRpgClass((RpgClass) Class.forName(entry).getDeclaredConstructor().newInstance());
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(container.parentUUID!=null) {
            parentUUID = UUID.fromString(container.parentUUID);
        }else{
            parentUUID = null;
        }
    }

    public void remove(){
        for (Stat stat :
                stats.statMap.values()) {
            stat.onRemoveStat(this);
        }
        RpgManager.removeRpgObject(getUUID());
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
        public double maxMana;
        public double mana;
        public String parentUUID;
        public Map<String, Stat.StatContainer> stats = new HashMap<>();
        public List<String> rpgClasses = new ArrayList<>();
    }
}
