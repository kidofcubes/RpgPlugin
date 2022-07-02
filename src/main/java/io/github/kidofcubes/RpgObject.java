package io.github.kidofcubes;


import io.github.kidofcubes.events.RpgActivateStatEvent;
import io.github.kidofcubes.events.RpgEntityDamageByObjectEvent;
import io.github.kidofcubes.managers.RpgManager;
import io.github.kidofcubes.managers.StatManager;
import io.github.kidofcubes.types.DamageType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.StreamSupport;

import static io.github.kidofcubes.RpgPlugin.gson;

public abstract class RpgObject {
    //probably shouldnt use strings i think maybe? dunno
    private final List<RpgClass> rpgClasses = new ArrayList<>();
    private final Map<String, Stat> statMap = new HashMap<>();
    private final Map<String, List<Stat>> effectiveStats = new HashMap<>();

    //stats work by checking if a object is using a objects

    String name;
    int level = 0;

    private double maxMana = 100;
    private double mana = 100;
    UUID parentUUID;
    boolean temporary = false;

    private boolean usedByParent = false;

    private RpgObject parent;
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
        System.out.println("newmana:"+mana+" maxmana:"+getMaxMana()+" min of mana and max mana: "+Math.min(mana,getMaxMana()));
        this.mana = Math.max(Math.min(mana,getMaxMana()),0);
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
    public RpgObject getParent() {
        if (parent != null) {
            return parent;
        } else {
            if (parentUUID != null) {
                RpgObject rpgObjectParent = RpgManager.getRpgObject(parentUUID);
                if (rpgObjectParent != null) {
                    parent = rpgObjectParent;
                    return parent;
                }
            }
            return null;
        }
    }

    public void setParent(RpgObject parent) {
        this.parent = parent;
        parentUUID = parent.getUUID();
    }

    public void setUsedByParent(boolean usedByParent){
        this.usedByParent = usedByParent;
    }

    //region stat stuff

    private final List<RpgObject> usedObjects = new ArrayList<>();
    public List<RpgObject> getUsedObjects(){
        return usedObjects;
    }
    public void addUsedObject(RpgObject rpgObject){
        System.out.println("ADDED USED OBJECT 1");
        if(rpgObject!=null) {
            System.out.println(getName()+"ADDED USED OBJECT 11"+rpgObject.getName());
            if(rpgObject.usingObject(this)) return;
            rpgObject.setParent(this);
            rpgObject.setUsedByParent(true);
            usedObjects.add(rpgObject);
            System.out.println("ADDED USED OBJECT 2");
            for (Stat stat : rpgObject.getEffectiveStats()) {
                System.out.println(getName()+" ADDING EFFECTIVE STATT "+stat.getName());
                addEffectiveStat(stat);
            }
        }
    }
    public void removeUsedObject(RpgObject rpgObject){
        if(rpgObject!=null) {
            if(!this.usingObject(rpgObject)) return;
            System.out.println("REMOVED USED OBJECT "+rpgObject.getName());
            usedObjects.remove(rpgObject);
            rpgObject.setUsedByParent(false);
            for (Stat stat : rpgObject.getEffectiveStats()) {
                removeEffectiveStat(stat);
            }
        }
    }

    public boolean usingObject(RpgObject rpgObject){
        if(rpgObject!=null) {
            if (getUUID().equals(rpgObject.getUUID())) return true;
            for (RpgObject usedObject : getUsedObjects()) {
                if (usedObject != null) {
                    if (usedObject.equals(rpgObject)) return true;
                    if (rpgObject.usingObject(rpgObject)) return true;
                }
            }
        }
        return false;
    }

    public void addEffectiveStat(Stat stat){
        System.out.println(getName()+" IS GETTING A NEW STAT "+stat.getName());
        if(effectiveStats.putIfAbsent(stat.getName(),new ArrayList<>(List.of(stat)))!=null){
            effectiveStats.get(stat.getName()).add(stat);
        }
        System.out.println(getName()+" NOW WITH "+effectiveStats.get(stat.getName()).size() + "of "+stat.getName());
        if(getParent()!=null&&usedByParent){
            getParent().addEffectiveStat(stat);
        }
    }
    public void removeEffectiveStat(Stat stat){
        if(effectiveStats.containsKey(stat.getName())) effectiveStats.get(stat.getName()).remove(stat);

        if(getParent()!=null&&usedByParent){
            getParent().removeEffectiveStat(stat);
        }
    }


    //todo fix class stat removing and stuff

    /**
     * Adds a stat to the statMap (will overwrite earlier version if exists)
     * @param stat The stat
     */
    public void addStat(Stat stat){
        stat.onAddStat(this);
        statMap.put(stat.getName(),stat);
        addEffectiveStat(stat);
    }
    public void addStats(List<Stat> addStats, boolean force){
        for (Stat stat :
                addStats) {
            addStat(stat,force);
        }
    }
    public void addStat(Stat stat, boolean force){
        if(!force) {
            Stat origStat = statMap.getOrDefault(stat.getName(),null);
            if(origStat!=null){
                origStat.join(stat);
            }else{
                addStat(stat);
            }
        }else{
            addStat(stat);
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
            removeEffectiveStat(removedStat);
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
/*        for (Map.Entry<String, List<Stat>> entry : effectiveStats.entrySet()) {
            for (Stat stat : entry.getValue()) {
                System.out.println("YOU HAVEE A STAT "+stat.getName()+" LVL "+stat.getLevel()+" FROM "+getName());
            }
        }*/
        return effectiveStats;
    }

    /**
     * Gets this object's effective stats (for example, an RpgEntity's effective stats include stats of items in their inventory)
     * @return This object's effective stats
     */
    public List<Stat> getEffectiveStats() {
        Map<String, List<Stat>> effectiveStatsMap = getEffectiveStatsMap();
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
