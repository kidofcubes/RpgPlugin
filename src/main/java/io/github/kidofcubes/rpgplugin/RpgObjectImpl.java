//package io.github.kidofcubes.rpgplugin;
//
//import org.bukkit.NamespacedKey;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.*;
//
//public abstract class RpgObjectImpl implements RpgObject{
//
//    protected int level=0;
//    protected double mana=0;
//    protected Map<String,RpgClass> rpgClasses = new HashMap<>();
//    protected Map<NamespacedKey,Stat> stats = new HashMap<>();
//    protected Set<RpgObject> usedObjects = new HashSet<>();
//    @Nullable
//    protected RpgObject parent = null;
//    private NamespacedKey type = RpgObject.defaultTypeKey;
//
//    @Override
//    public int getLevel() {
//        return level;
//    }
//    @Override
//    public void setLevel(int level) {
//        this.level=level;
//    }
//
//    @Override
//    public double getMana() {
//        return mana;
//    }
//
//    @Override
//    public void setMana(double mana) {
//        this.mana=mana;
//    }
//
//    @Override
//    public List<RpgClass> getRpgClasses() {
//        return rpgClasses.values().stream().toList();
//    }
//    @Override
//    public void addRpgClass(RpgClass rpgClass) {
//        rpgClasses.put(rpgClass.getFullName(),rpgClass);
//    }
//
//    @Override
//    public RpgClass getRpgClass(String rpgClass) {
//        return rpgClasses.get(rpgClass);
//    }
//
//    @Override
//    public void removeRpgClass(String rpgClass) {
//        rpgClasses.remove(rpgClass);
//    }
//
//    @Override
//    public boolean hasRpgClass(String rpgClass) {
//        return rpgClasses.containsKey(rpgClass);
//    }
//
//    @Override
//    public void setRpgType(@NotNull NamespacedKey namespacedKey) {
//        type=namespacedKey;
//    }
//
//    @Override
//    public @NotNull NamespacedKey getRpgType() {
//        return type;
//    }
//
//    @Override
//    public void use(RpgObject rpgObject) {
//        if(!usedObjects.add(rpgObject)){
//            return;
//        }
//        for(Stat stat : rpgObject.getStats()){
//            stat.onStopUsingStat();
//            stat.onUseStat(this);
//        }
//        rpgObject.setParent(this);
//    }
//
//    @Override
//    public void stopUsing(RpgObject rpgObject) {
//        if(!usedObjects.remove(rpgObject)){
//            return;
//        }
//        for(Stat stat : rpgObject.getStats()){
//            stat.onStopUsingStat();
//            stat.onUseStat(rpgObject);
//        }
//        rpgObject.setParent(null);
//    }
//
//
//    @Nullable
//    @Override
//    public RpgObject getParent() {
//        return parent;
//    }
//
//    @Override
//    public void setParent(@Nullable RpgObject parent) {
//        this.parent=parent;
//    }
//
//    @Override
//    public @NotNull Map<NamespacedKey, List<Stat>> getUsedStatsMap() {
//        Map<NamespacedKey,List<Stat>> map = new HashMap<>();
//        for(RpgObject usedObject : usedObjects){
//            for(Stat stat : usedObject.getStats()){
//                map.putIfAbsent(stat.getIdentifier(),new ArrayList<>());
//                map.get(stat.getIdentifier()).add(stat);
//            }
//        }
//        for(Map.Entry<NamespacedKey,Stat> entry : stats.entrySet()){
//            map.putIfAbsent(entry.getValue().getIdentifier(),new ArrayList<>());
//            map.get(entry.getValue().getIdentifier()).add(entry.getValue());
//        }
//        return map;
//    }
//
//    @Override
//    public boolean usedBy(RpgObject rpgObject) {
//        return false;
//    }
//
//    @Override
//    public void addStat(NamespacedKey key, Stat stat) {
//        removeStat(key);
//        stat.onAddStat(this);
//        stat.onUseStat(getParent()!=null ? getParent():this);
//        stats.put(key,stat);
//    }
//
//    @Override
//    public boolean hasStat(NamespacedKey key) {
//        return stats.containsKey(key);
//    }
//
//    @Override
//    public @NotNull Stat getStat(NamespacedKey key) {
//        return Objects.requireNonNull(stats.get(key));
//    }
//
//    @Override
//    public List<Stat> getStats() {
//        return stats.values().stream().toList();
//    }
//
//    @Override
//    public void removeStat(NamespacedKey key) {
//        Stat statInstance = stats.remove(key);
//        if(statInstance!=null){
//            statInstance.onStopUsingStat();
//            statInstance.onRemoveStat();
//        }
//    }
//
//    @Override
//    public boolean alwaysLoaded() {
//        return false;
//    }
//}
