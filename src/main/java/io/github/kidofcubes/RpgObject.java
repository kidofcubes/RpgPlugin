package io.github.kidofcubes;


import io.github.kidofcubes.events.RpgActivateStatEvent;
import io.github.kidofcubes.events.RpgEntityDamageByObjectEvent;
import io.github.kidofcubes.managers.RpgManager;
import io.github.kidofcubes.managers.StatManager;
import io.github.kidofcubes.types.DamageType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static io.github.kidofcubes.RpgPlugin.gson;


//dodgy code
public abstract interface RpgObject {

    //region gettersetters


    boolean isLoaded();

    void setUUID(UUID uuid);

    UUID getUUID();

    @NotNull
    public abstract String getName();


    int getLevel();

    void setLevel(int level);


    double getMana();
    void setMana(double mana);

    double getMaxMana();

    void setMaxMana(double maxMana);


    //endregion
    //region classes

    List<RpgClass> getRpgClasses();

    /**
     * Adds a RpgClass
     * @param rpgClass
     */
    void addRpgClass(RpgClass rpgClass);
    RpgClass getRpgClass(RpgClass rpgClass);
    RpgClass getRpgClass(String rpgClass);
    void removeRpgClass(String rpgClass);

    boolean hasRpgClass(String rpgClass);
    //endregion
    //region usingstuff

    void own(RpgObject rpgObject);
    void stopUsing(RpgObject rpgObject);
    boolean usedBy(RpgObject rpgObject);
    //endregion

    /**
     * Gets the parent of this object (inherits relations and will attribute things to parent)
     * @return The parent of this object
     */
    @Nullable
    public RpgObject getParent();


    public void setParent(RpgObject parent);


    //region stat stuff

    //todo fix class stat removing and stuff

    public void addStat(Stat stat, boolean force);

    public boolean hasStat(String stat);
    @Nullable
    public Stat getStat(String stat);

    /**
     * Removes a stat (if the stat is not found, won't do anything)
     * @param stat The stat name
     */
    public void removeStat(String stat);


    /**
     * Gets this object's used stats (for example, an RpgEntity's effective stats include stats of items in their inventory)
     * @return This object's used stats
     */
    public Map<Class<? extends Stat>, List<Stat>> getUsedStatsMap();

    /**
     * Gets this object's effective stats (for example, an RpgEntity's effective stats include stats of items in their inventory)
     * @return This object's effective stats
     */
    default public List<Stat> getUsedStats() {
        Map<Class<? extends Stat>, List<Stat>> effectiveStatsMap = getUsedStatsMap();
        List<Stat> returnStats = new ArrayList<>();
        for (List<Stat> list :
                effectiveStatsMap.values()) {
            returnStats.addAll(list);
        }
        return returnStats;
    }


    //endregion

//    /**
//     * Makes this object attack a RpgEntity victim with base damage
//     * (Makes a RpgEntityDamageByObjectEvent with amount physical base damage,
//     * @param amount The base damage of the attack
//     * @param victim The victim of the attack
//     */
//    public RpgEntityDamageByObjectEvent attack(double amount, RpgEntity victim) {
//        return attack(DamageType.PHYSICAL, amount,victim,null);
//    }
//    public RpgEntityDamageByObjectEvent attack(DamageType damageType, double amount, RpgEntity victim, List<Stat> extraStats){
//        if(victim!=null) {
//            return victim.damage(damageType, amount, this, extraStats);
//        }
//        return null;
//    }

    /**
     * Calls a RpgActivateStatEvent with this as the parent
     * @param statName The name of the stat to activate
     */
    default void activateStat(String statName) {
        getActivateStatEvent(List.of(statName)).callEvent();
    }

    default RpgActivateStatEvent getActivateStatEvent(List<String> statNames) {
        return new RpgActivateStatEvent(this, statNames);
    }

    //region saveloadingjson
    public String toJson();

    void loadFromJson(String json);
//        RpgObjectJsonContainer container = gson.fromJson(json, RpgObjectJsonContainer.class);
//        level = container.level;
//        name = container.name;
//        mana = container.mana;
//        maxMana = container.maxMana;
//        for (Map.Entry<String, Stat.StatContainer> entry : container.stats.entrySet()) {
//            try {
//                Class<? extends Stat> statClass = StatManager.getRegisteredStatByName(entry.getKey());
//                if(statClass!=null) {
//                    Stat stat = statClass.getDeclaredConstructor().newInstance();
//                    stat.setLevel(entry.getValue().level);
//                    stat.loadCustomData(entry.getValue().customData);
//                    addStat(stat);
//                }
//            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//        }
//        for (String entry : container.rpgClasses) {
//            try {
//                addRpgClass((RpgClass) Class.forName(entry).getDeclaredConstructor().newInstance());
//            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        if(container.parentUUID!=null) {
//            parentUUID = UUID.fromString(container.parentUUID);
//        }else{
//            parentUUID = null;
//        }

    //endregion

    /**
     * Deletes the object
     */
    public void remove();
    public void prepareForRemove();
    /**
     * Saves this object to the world
     */
    void save();
}
