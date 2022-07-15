package io.github.kidofcubes;


import io.github.kidofcubes.events.RpgActivateStatEvent;
import io.github.kidofcubes.events.RpgEntityDamageByObjectEvent;
import io.github.kidofcubes.managers.RpgManager;
import io.github.kidofcubes.managers.StatManager;
import io.github.kidofcubes.types.DamageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static io.github.kidofcubes.RpgPlugin.gson;


//dodgy code
public abstract class RpgObject {
    //probably shouldnt use strings i think maybe? dunno
    private final Map<RpgClass, List<Stat>> rpgClasses = new HashMap<>();
    private final Map<String, Stat> statMap = new HashMap<>(); //key stat name
    private final Map<Class<? extends Stat>, List<Stat>> effectiveStats = new HashMap<>(); //key stat name

    //stats work by checking if a object is using a objects

    String name;

    int level = 0;

    private double maxMana = 20;
    private double mana = 20;
    UUID parentUUID;
    public boolean temporary = false;


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

    @NotNull
    public abstract String getName();


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public double getMana() {
        return mana;
    }
    public void setMana(double mana) {
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
        return new ArrayList<RpgClass>(rpgClasses.keySet());
    }

    /**
     * Adds a RpgClass
     * @param rpgClass
     */
    public void addRpgClass(RpgClass rpgClass){
        removeRpgClass(rpgClass);
        List<Stat> listOfStats = rpgClass.classStats();
        rpgClasses.put(rpgClass,listOfStats);
        for (Stat stat : listOfStats) {
            stat.onAddStat(this);
            addEffectiveStat(stat);
        }


    }
    public RpgClass getRpgClass(RpgClass rpgClass){
        return getRpgClass(rpgClass.getFullName());
    }
    public RpgClass getRpgClass(String rpgClass){
        for (RpgClass check : getRpgClasses()) {
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
        RpgClass toRemove = null;
        for (Map.Entry<RpgClass, List<Stat>> pair : rpgClasses.entrySet()) {
            if(pair.getKey().getFullName().equalsIgnoreCase(rpgClass)){
                for (Stat stat : pair.getValue()) {
                    stat.onRemoveStat(this);
                    removeEffectiveStat(stat);
                }
                toRemove = pair.getKey();
                break;
            }
        }
        if(toRemove!=null){
            rpgClasses.remove(toRemove);
        }
    }

    public boolean hasRpgClass(RpgClass rpgClass){
        for (RpgClass check : getRpgClasses()) {
            if(check.getFullName().equalsIgnoreCase(rpgClass.getFullName())){
                return true;
            }
        }
        return false;
    }
    //endregion
    //region usingstuff

    private RpgObject user;
    private final List<RpgObject> usedObjects = new ArrayList<>(); //for updating

    public void use(RpgObject rpgObject){
        if(!rpgObject.usedBy(this)&&!this.usedBy(rpgObject)){
            usedObjects.add(rpgObject);
            rpgObject.setUser(this);
            for (Stat stat : rpgObject.getEffectiveStats()) {

                stat.onStopUsingStat(rpgObject);
                addEffectiveStat(stat);
            }
        }
    }

    protected void setUser(RpgObject user){
        this.user=user;
    }
    public void stopUsing(RpgObject rpgObject){
        if (rpgObject.user == this) {
            usedObjects.remove(rpgObject);
            rpgObject.setUser(null);
            for (Stat stat : rpgObject.getEffectiveStats()) {
                removeEffectiveStat(stat);
                stat.onUseStat(rpgObject);
            }
        }
    }

    public boolean usedBy(RpgObject rpgObject){
        if (getUUID().equals(rpgObject.getUUID())||rpgObject==this) return true;//check if object is me
        if(user!=null){
            if(user==rpgObject) return true;
            return user.usedBy(rpgObject);
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
                if(rpgObjectParent==this){
                    parentUUID = null;
                    return null;
                }
                if (rpgObjectParent != null) {
                    setParent(rpgObjectParent);
                    return parent;
                }
            }
            return null;
        }
    }


    public void setParent(RpgObject parent) {
        if(parent==this){
            throw new IllegalArgumentException("Can't set parent of "+getName()+" to itself");
        }
        this.parent = parent;
        parentUUID = parent.getUUID();
    }


    //region stat stuff

    public void addEffectiveStat(Stat stat){
        if(effectiveStats.putIfAbsent(stat.getClass(),new ArrayList<>(List.of(stat)))!=null){
            effectiveStats.get(stat.getClass()).add(stat);
        }

        if(user!=null){
            user.addEffectiveStat(stat);
        }else{
            stat.onUseStat(this);
        }
    }
    public void removeEffectiveStat(Stat stat){
        if(effectiveStats.containsKey(stat.getClass())) effectiveStats.get(stat.getClass()).remove(stat);

        if(user!=null){
            user.removeEffectiveStat(stat);

        }else{
            stat.onStopUsingStat(this);
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
    public Map<Class<? extends Stat>, List<Stat>> getEffectiveStatsMap() {
        return effectiveStats;
    }

    /**
     * Gets this object's effective stats (for example, an RpgEntity's effective stats include stats of items in their inventory)
     * @return This object's effective stats
     */
    public List<Stat> getEffectiveStats() {
        Map<Class<? extends Stat>, List<Stat>> effectiveStatsMap = getEffectiveStatsMap();
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
                getRpgClasses()) {
            container.rpgClasses.add(rpgClass.getFullName());
        }
        return container;
    }

    protected void loadFromJson(String json) {
        RpgObjectJsonContainer container = gson.fromJson(json, RpgObjectJsonContainer.class);
        level = container.level;
        name = container.name;
        mana = container.mana;
        maxMana = container.maxMana;
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
        prepareForRemove();
    }
    public void prepareForRemove(){
        /*        for (Stat stat :
                statMap.values()) {
            stat.onRemoveStat(this);
        }*/
        RpgManager.removeRpgObject(getUUID());
//        for (Stat stat :
//                getStats()) {
//            stat.onRemoveStat(this);
//        }
        Map<String, Stat> tempStats = new HashMap<>(getStatsMap());
        List<Stat> statsList = getStats();
        if(statsList.size()>0) {
            for (int i = getStats().size() - 1; i > -1; i--) {
                removeStat(statsList.get(i).getName());
            }
        }
        if(user!=null){
            user.stopUsing(this);
        }
        if(usedObjects.size()>0) {
            for (int i = usedObjects.size() - 1; i > -1; i--) {
                stopUsing(usedObjects.get(i));
            }
        }
        statMap.putAll(tempStats);
//        List<Stat> effectiveStatsList = getEffectiveStats();
//        if(effectiveStatsList.size()>0) {
//            for (int i = getEffectiveStats().size() - 1; i > -1; i--) {
//                removeEffectiveStat(effectiveStatsList.get(i));
//            }
//        }
//        statMap.clear();
//        effectiveStats.clear();
//        usedObjects.clear();
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
