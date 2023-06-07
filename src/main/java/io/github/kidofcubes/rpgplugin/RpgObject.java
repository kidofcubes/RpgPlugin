//package io.github.kidofcubes.rpgplugin;
//
//
//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import net.minecraft.nbt.CompoundTag;
//import org.bukkit.NamespacedKey;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.*;
//
//
////dodgy code
//public interface RpgObject {
//    //todo always loaded boolean thing
//
//    NamespacedKey defaultTypeKey = new NamespacedKey("rpg_plugin","default_type");
//    String typeKey = ("type");
//
//
//    void setRpgType(NamespacedKey namespacedKey);
//
//    @NotNull
//    NamespacedKey getRpgType();
//
//    default String getName(){ return "RPG_OBJECT";};
//
//    default boolean isValid(){
//        return true;
//    }
//
//    boolean alwaysLoaded();
//
//    int getLevel();
//
//    void setLevel(int level);
//
//
//    double getMana();
//    void setMana(double mana);
//
//
//
//
//
//
//
//    //region classes
//
//    List<RpgClass> getRpgClasses();
//
//    /**
//     * Adds a RpgClass
//     * @param rpgClass
//     */
//    void addRpgClass(RpgClass rpgClass);
//    RpgClass getRpgClass(String rpgClass);
//    void removeRpgClass(String rpgClass);
//
//    boolean hasRpgClass(String rpgClass);
//    //endregion
//    //region usingstuff
//
//    void use(RpgObject rpgObject);
//    void stopUsing(RpgObject rpgObject);
//    boolean usedBy(RpgObject rpgObject);
//    //endregion
//
//    /**
//     * Gets the parent of this object (inherits relations and will attribute things to parent)
//     * @return The parent of this object
//     */
//    @Nullable
//    RpgObject getParent();
//
//
//    void setParent(RpgObject parent);
//
//
//    //region stat stuff
//
//
//
//    void addStat(NamespacedKey key, Stat stat);
//    default void addStat(NamespacedKey key){
//        addStat(key,RpgRegistry.initStat(key));
//    }
//    default void addStat(Stat stat){
//        addStat(stat.getIdentifier(),stat);
//    }
//
//    boolean hasStat(NamespacedKey key);
//
//    /**
//     * Gets the stat instance under {@code key}
//     * @param key The key of the to be retrieved stat
//     * @return
//     */
//    @NotNull
//    Stat getStat(NamespacedKey key);
//
//
//    /**
//     * Returns non-modifiable list of stats
//     * @return
//     */
//    @NotNull
//    List<Stat> getStats();
//
//    /**
//     * Removes a stat (if the stat is not found, won't do anything)
//     * @param key The stat key
//     */
//    void removeStat(NamespacedKey key);
//
//
//    /**
//     * Gets this object's used stats
//     * non-persistent
//     * @return This object's used stats
//     */
//    @NotNull
//    Map<NamespacedKey, List<Stat>> getUsedStatsMap();
//
//    /**
//     * Gets this object's effective stats
//     * non-persistent
//     * @return This object's effective stats
//     */
//    default @NotNull List<Stat> getUsedStats() {
//        Map<NamespacedKey, List<Stat>> effectiveStatsMap = getUsedStatsMap();
//        List<Stat> returnStats = new ArrayList<>();
//        for (Collection<Stat> statz :
//                effectiveStatsMap.values()) {
//            returnStats.addAll(statz);
//        }
//        return returnStats;
//    }
//
//
//    //endregion
//
////    /**
////     * Makes this object attack a RpgEntity victim with base damage
////     * (Makes a RpgEntityDamageByObjectEvent with amount physical base damage,
////     * @param amount The base damage of the attack
////     * @param victim The victim of the attack
////     */
////    RpgEntityDamageByObjectEvent attack(double amount, RpgEntity victim) {
////        return attack(DamageType.PHYSICAL, amount,victim,null);
////    }
////    RpgEntityDamageByObjectEvent attack(DamageType damageType, double amount, RpgEntity victim, List<Stat> extraStats){
////        if(victim!=null) {
////            return victim.damage(damageType, amount, this, extraStats);
////        }
////        return null;
////    }
//
////    /**
////     * Calls a RpgActivateStatEvent with this as the parent
////     * @param statName The name of the stat to activate
////     */
////    default void activateStat(String statName) {
////        getActivateStatEvent(List.of(statName)).callEvent();
////    }
////
////    default RpgActivateStatEvent getActivateStatEvent(List<String> statNames) {
////        return new RpgActivateStatEvent(this, statNames);
////    }
//
//    //region saveloadingjson
//
//    //todo save as CompoundTag instead of json string
//
//    default CompoundTag toTag(){
//        CompoundTag compoundTag = new CompoundTag();
//        if(getRpgType()!=defaultTypeKey) {
//            compoundTag.putString("type", getRpgType().asString());
//        }
//        compoundTag.putInt("level",getLevel());
//        compoundTag.putDouble("mana",getMana());
//        if(getStats().size()!=0) {
////            Map<String, Stat.StatContainer> map = new HashMap<>();
//            CompoundTag stats = new CompoundTag();
//            for (Stat stat : getStats()) {
//                stats.put(stat.getIdentifier().asString(), stat.asTag());
//            }
//
//            compoundTag.put("stats", stats);
//        }
////        System.out.println("TO JSONed something "+this.getClass()+" into "+compoundTag.toString());
////
////        try {
////            throw new NullPointerException();
////        }catch (NullPointerException exception){
////            exception.printStackTrace();
////        }
//        return (compoundTag);
//    }
//
//    default RpgObject loadTag(CompoundTag compoundTag) {
//        if(compoundTag.contains("type")){
//            setRpgType(NamespacedKey.fromString(compoundTag.getString("type")));
//        }
//        if(compoundTag.contains("level")){
//            setLevel(compoundTag.getInt("level"));
//        }
//        if(compoundTag.contains("mana")){
//            setMana(compoundTag.getDouble("mana"));
//        }
//        if(compoundTag.contains("stats")){
//            CompoundTag stats = compoundTag.getCompound("stats");
//            for (String keyy : stats.getAllKeys()) {
//                NamespacedKey key = NamespacedKey.fromString(keyy);
//                if(RpgRegistry.isRegisteredStat(key)) {
//                    assert key != null;
//                    Stat stat = RpgRegistry.initStat(key);
//                    stat.loadTag(stats.getCompound(keyy));
//                    addStat(key, stat);
//                }else{
//                    System.out.println("KEY "+key.asString()+" WAS NOT IN REGISTERY, IGNORINGG");
//                }
//            }
//        }
//        return this;
//    }
//    //endregion
//    default RpgObject getRpgInstance(){
//        return this;
//    }
//}
