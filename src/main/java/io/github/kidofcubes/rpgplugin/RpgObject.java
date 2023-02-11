package io.github.kidofcubes.rpgplugin;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


//dodgy code
public interface RpgObject {

    public static final NamespacedKey defaultTypeKey = new NamespacedKey("rpg_plugin","default_type");


    public static final Gson gson = new Gson();

    void setRpgType(NamespacedKey namespacedKey);

    @NotNull
    NamespacedKey getRpgType();

    boolean isLoaded();

    UUID getUUID();

    String getName();


    int getLevel();

    void setLevel(int level);


    double getMana();
    void setMana(double mana);







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
    RpgObject getParent();


    void setParent(RpgObject parent);


    //region stat stuff



    void addStat(NamespacedKey key, Stat stat);
    default void addStat(NamespacedKey key){
        addStat(key,RpgRegistry.initStat(key));
    }
    default void addStat(Stat stat){
        addStat(stat.getIdentifier(),stat);
    }

    boolean hasStat(NamespacedKey key);

    /**
     * Gets the stat instance under {@code key}
     * @param key The key of the to be retrieved stat
     * @return
     */
    @NotNull
    Stat getStat(NamespacedKey key);


    /**
     * Returns non-modifiable list of stats
     * @return
     */
    @NotNull
    List<Stat> getStats();

    /**
     * Removes a stat (if the stat is not found, won't do anything)
     * @param key The stat key
     */
    void removeStat(NamespacedKey key);


    /**
     * Gets this object's used stats (non-modifiable) (for example, an RpgEntity's effective stats include stats of items in their inventory)
     * @return This object's used stats
     */
    @NotNull
    Map<NamespacedKey, List<Stat>> getUsedStats();

    /**
     * Gets this object's effective stats (for example, an RpgEntity's effective stats include stats of items in their inventory)
     * @return This object's effective stats
     */
    @NotNull
    default List<Stat> getUsedStatsList() {
        Map<NamespacedKey, List<Stat>> effectiveStatsMap = getUsedStats();
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
//    RpgEntityDamageByObjectEvent attack(double amount, RpgEntity victim) {
//        return attack(DamageType.PHYSICAL, amount,victim,null);
//    }
//    RpgEntityDamageByObjectEvent attack(DamageType damageType, double amount, RpgEntity victim, List<Stat> extraStats){
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


    default JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        if(getRpgType()!=defaultTypeKey) {
            jsonObject.addProperty("type", getRpgType().asString());
        }
        jsonObject.addProperty("level",getLevel());
        jsonObject.addProperty("mana",getMana());
        if(getStats().size()!=0) {
            Map<String, Stat.StatContainer> map = new HashMap<>();
            for (Stat stat : getStats()) {
                map.put(stat.getClass().getName(), stat.asContainer());
            }

            jsonObject.add("stats", gson.toJsonTree(map));
        }
        return (jsonObject);
    }

    default RpgObject loadFromJson(String json) {
        if(json.equals("")){
            return this;
        }
        return loadFromJson(gson.fromJson(json,JsonObject.class));
    }
    default RpgObject loadFromJson(@NotNull JsonObject jsonObject) {
        if(jsonObject.has("type")){
            setRpgType(NamespacedKey.fromString(jsonObject.get("type").getAsString()));
        }
        if(jsonObject.has("level")){
            setLevel(jsonObject.get("level").getAsInt());
        }
        if(jsonObject.has("mana")){
            setMana(jsonObject.get("mana").getAsDouble());
        }
        if(jsonObject.has("stats")){
            Map<String,JsonElement> map = jsonObject.get("stats").getAsJsonObject().asMap();
            for (Map.Entry<String,JsonElement> entry : map.entrySet()) {
                NamespacedKey key = NamespacedKey.fromString(entry.getKey());
                if(RpgRegistry.isRegisteredStat(key)) {
                    assert key != null;
                    Stat stat = RpgRegistry.initStat(key);
                    stat.loadCustomData((entry.getValue().getAsJsonObject()).getAsJsonObject("customData"));
                    addStat(key, stat);
                }
            }
        }

        return this;
    }
    //endregion
    default RpgObject self(){
        return this;
    }
}
