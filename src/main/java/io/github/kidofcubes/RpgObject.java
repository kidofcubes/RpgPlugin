package io.github.kidofcubes;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.kidofcubes.managers.StatManager;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


//dodgy code
public interface RpgObject {
    NamespacedKey metadataKey = new NamespacedKey("rpg_plugin", "metadata");
    Gson gson = new Gson();

    //region gettersetters


    boolean isLoaded();

    UUID getUUID();

    String getName();


    int getLevel();

    void setLevel(int level);


    double getMana();
    void setMana(double mana);


    //endregion
    //region classes

    List<RpgClass> getRpgClasses();

    /**
     * Adds a RpgClass
     * @param rpgClass
     */
    void addRpgClass(RpgClass rpgClass);
    RpgClass getRpgClass(String rpgClass);
    void removeRpgClass(String rpgClass);

    boolean hasRpgClass(String rpgClass);
    //endregion
    //region usingstuff

    void use(RpgObject rpgObject);
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
     * Returns non-modifiable list of stats
     * @return
     */
    @NotNull
    public List<Stat> getStats();

    /**
     * Removes a stat (if the stat is not found, won't do anything)
     * @param stat The stat name
     */
    public void removeStat(String stat);


    /**
     * Gets this object's used stats (non-modifiable) (for example, an RpgEntity's effective stats include stats of items in their inventory)
     * @return This object's used stats
     */
    @NotNull
    public Map<Class<? extends Stat>, List<Stat>> getUsedStats();

    /**
     * Gets this object's effective stats (for example, an RpgEntity's effective stats include stats of items in their inventory)
     * @return This object's effective stats
     */
    @NotNull
    default public List<Stat> getUsedStatsList() {
        Map<Class<? extends Stat>, List<Stat>> effectiveStatsMap = getUsedStats();
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

//    /**
//     * Calls a RpgActivateStatEvent with this as the parent
//     * @param statName The name of the stat to activate
//     */
//    default void activateStat(String statName) {
//        getActivateStatEvent(List.of(statName)).callEvent();
//    }
//
//    default RpgActivateStatEvent getActivateStatEvent(List<String> statNames) {
//        return new RpgActivateStatEvent(this, statNames);
//    }

    //region saveloadingjson


    default String toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("level",getLevel());
        jsonObject.addProperty("mana",getMana());
        Map<String,Stat.StatContainer> map = new HashMap<>();
        for (Stat stat : getStats()) {
            map.put(stat.getClass().getName(), stat.asContainer());
        }

        jsonObject.add("stats",gson.toJsonTree(map));
        return gson.toJson(jsonObject);
    }

    default RpgObject loadFromJson(String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        setLevel(jsonObject.get("level").getAsInt());
        setMana(jsonObject.get("mana").getAsInt());
        Map<String,JsonElement> map = jsonObject.get("stats").getAsJsonObject().asMap();
        for (Map.Entry<String,JsonElement> entry : map.entrySet()) {
            try {
                Class<? extends Stat> statClass = StatManager.getRegisteredStatByName(entry.getKey());
                if(statClass!=null) {
                    Stat stat = statClass.getDeclaredConstructor().newInstance();
                    (entry.getValue().getAsJsonObject()).get("level").getAsInt();
                    stat.loadCustomData((entry.getValue().getAsJsonObject()).getAsJsonObject("customData"));
                    addStat(stat,true);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
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
}
