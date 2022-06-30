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
    //probably shouldnt use strings i think maybe? dunno
    private final List<RpgClass> rpgClasses = new ArrayList<>();
    private final Map<String, Stat> statMap = new HashMap<>();

    //not saved, generated at runtime
    private final Map<RpgObject,Boolean> children = new HashMap<>(); //boolean is for use stats
    //true means use stats from it
    //false means dont use stats from it
    String name;
    int level = 0;

    private double maxMana = 100;
    private double mana = 100;
    UUID parentUUID;
    boolean temporary = false;

    private RpgEntity parent;
    private UUID uuid;

    //region gettersetters

    /**
     * Check if this object is temporary (deleted on server restart, doesn't get saved)
     * @return If this object is temporary
     */
    public boolean isTemporary() {
        return temporary;
    }

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

    public abstract String getName();

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        this.mana = Math.max(Math.min(mana,maxMana),0);
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
    }


    //endregion
    //region classes

    public List<RpgClass> getRpgClasses(){
        return rpgClasses;
    }

    /**
     * Adds a RpgClass
     * @param rpgClass
     */
    public void addRpgClass(RpgClass rpgClass){
            removeRpgClass(rpgClass);
            rpgClasses.add(rpgClass);
        addStats(rpgClass.classStats(),false);

    }
    public RpgClass getRpgClass(RpgClass rpgClass){
        return getRpgClass(rpgClass.getFullName());
    }
    public RpgClass getRpgClass(String rpgClass){
        for (RpgClass check : rpgClasses) {
            if (check.getFullName().equalsIgnoreCase(rpgClass)) {
                return check;
            }
        }
        return null;
    }
    public void removeRpgClass(RpgClass rpgClass){
        removeRpgClass(rpgClass.getFullName());
    }
    public void removeRpgClass(String rpgClass){
        for (int i = 0; i < rpgClasses.size(); i++) {
            if(rpgClasses.get(i).getFullName().equalsIgnoreCase(rpgClass)){
                for (Stat stat : rpgClasses.get(i).classStats()) {
                    removeStat(stat,false);
                }
                rpgClasses.remove(i);
                break;
            }
        }
    }

    public boolean hasRpgClass(RpgClass rpgClass){
        for (RpgClass check : rpgClasses) {
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

    public void addChild(RpgObject object, boolean used){
        children.put(object,used);
    }
    public void removeChild(RpgObject object){
        children.remove(object);

    }


    //region stat stuff

    //todo fix class stat removing and stuff

    /**
     * Adds a stat to the statMap (will increase level if stat of that type already exists)
     * @param stat The stat
     */
    public void addStat(Stat stat){
        addStat(stat,false);
    }
    public void addStats(List<Stat> addStats, boolean force){
        for (Stat stat :
                addStats) {
            addStat(stat,force);
        }
    }
    public void addStat(Stat stat, boolean force){
        if(!force) {
            Stat origStat = statMap.putIfAbsent(stat.getName(),stat);
            if(origStat!=null){
                origStat.join(stat);
            }else{
                stat.onAddStat(this);
            }
        }else{
            statMap.put(stat.getName(),stat);
            stat.onAddStat(this);
        }
    }

    public boolean hasStat(String stat){
        return statMap.containsKey(stat);
    }
    @Nullable
    public Stat getStat(String stat){
        return statMap.getOrDefault(stat,null);
    }

    /**
     * Removes a stat (if the stat is not found, won't do anything)
     * @param stat The stat name
     */
    public void removeStat(String stat) {
        Stat removedStat = statMap.remove(stat);
        if(removedStat!=null){
            removedStat.onRemoveStat(this);
        }
    }
    public void removeStat(Stat stat,boolean force){
        if(!force){
            Stat previous = getStat(stat.getName());
            if(previous!=null) {
                previous.remove(stat);
                if (previous.getLevel() == 0) {
                    removeStat(stat.getName());
                }
            }
        }else{
            removeStat(stat.getName());
        }
    }
    public void removeStats(List<Stat> statList, boolean force){
        for (Stat stat : statList) {
            removeStat(stat,force);
        }
    }

    /**
     * Gets the stat map
     * @return The stat map
     */
    public Map<String, Stat> getStatsMap() {
        return statMap;
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
    public Map<String, List<Stat>> getEffectiveStatsMap() {
        Map<String,List<Stat>> effectiveStats = new HashMap<>();
        //clone the stat
        for (Map.Entry<String,Stat> pair: getStatsMap().entrySet()) {
            effectiveStats.put(pair.getKey(),List.of(pair.getValue().newInstance()));
        }
        for (Map.Entry<RpgObject,Boolean> pair: children.entrySet()) {
            //for (Stat stat : pair.getKey().getEffectiveStats()) { //will loop indefinitely if self is a child somewhere down the line
            for (Stat stat : pair.getKey().getStats()) { //will not loop indefinitely if self is a child
                List<Stat> origStats = effectiveStats.getOrDefault(stat.getName(),null);

                if(origStats!=null){
                    if(origStats.get(0).mergeable()){
                        origStats.get(0).join(stat);
                    }else{
                        origStats.add(stat);
                    }
                }else{
                    effectiveStats.put(stat.getName(),List.of(stat));
                }
            }
        }
        return effectiveStats;
    }

    /**
     * Gets this object's effective stats (for example, an RpgEntity's effective stats include stats of items in their inventory)
     * @return This object's effective stats
     */
    public List<Stat> getEffectiveStats() {
        Map<String, List<Stat>> effectiveStatsMap = new HashMap<>();
        List<Stat> returnStats = new ArrayList<>();
        for (List<Stat> list :
                effectiveStatsMap.values()) {
            returnStats.addAll(list);
        }
        return returnStats;
    }


    //endregion

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
        if(victim!=null) {
            return victim.damage(damageType, amount, this, extraStats);
        }
        return null;
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

    //region saveloadingjson
    public String toJson() {
        return gson.toJson(toContainer());
    }

    public RpgObjectJsonContainer toContainer(){
        RpgObjectJsonContainer container = new RpgObjectJsonContainer();
        container.name = name;
        container.level = level;
        container.mana = getMana();
        container.maxMana = getMaxMana();

        for (Map.Entry<String, Stat> entry : getStatsMap().entrySet()) {
            container.stats.put(entry.getKey(),entry.getValue().asContainer());
        }
        if(parentUUID!=null) {
            container.parentUUID = parentUUID.toString();
        }
        for (RpgClass rpgClass :
                rpgClasses) {
            container.rpgClasses.add(rpgClass.getFullName());
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
    //endregion

    /**
     * Removes the object
     */
    public void remove(){
/*        for (Stat stat :
                statMap.values()) {
            stat.onRemoveStat(this);
        }*/
        statMap.clear();
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
