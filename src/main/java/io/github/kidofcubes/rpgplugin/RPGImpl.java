package io.github.kidofcubes.rpgplugin;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RPGImpl implements RPG{

    @Override
    public CompoundTag getTag() {
        return base;
    }

    private final CompoundTag base;
    public RPGImpl(CompoundTag base){
        this.base=base;
    }


    private CompoundTag data(){
        if(!base.contains("data")) base.put("data", new CompoundTag());
        return base.getCompound("data");
    }


    public void setRpgType(NamespacedKey namespacedKey){
        base.putString(TYPE_KEY,namespacedKey.asString());
    }

    public NamespacedKey getRpgType(){
        return base.getString(TYPE_KEY).equals("") ? DEFAULT_TYPE_KEY : NamespacedKey.fromString(base.getString(TYPE_KEY));
    }

    public String getName(){ return "RPG_OBJECT";};

    /**
     * Defaults to 0.
     * @return
     */
    public int getLevel(){ return data().getInt(LVL_KEY); }
    public void setLevel(int level){ data().putInt(LVL_KEY,level); }

    /**
     * Defaults to 0.
     * @return
     */
    public double getMana(){ return data().getDouble(MANA_KEY); }
    public void setMana(double mana){ data().putDouble(MANA_KEY,mana); }


    private CompoundTag stats(){
        if(!base.contains("stats")) base.put("stats", new CompoundTag());
        return base.getCompound("stats");
    }
    public void addStat(NamespacedKey key, CompoundTag stat){
        stats().put(key.asString(),stat);
    }
    public void addStat(Stat stat){
        stats().put(stat.getIdentifier().asString(),stat.getData().getTag());
    }
    public boolean hasStat(NamespacedKey key){
        return stats().contains(key.asString());
    }

    /**
     * Returns the stat if found, else returns new empty CompoundTag
     * @param key
     * @return
     */
    public CompoundTag getStat(NamespacedKey key){
        return stats().getCompound(key.asString());
    }

    /**
     * Removes a stat (if the stat is not found, won't do anything)
     * @param key The stat key
     */
    public void removeStat(NamespacedKey key){
        stats().remove(key.asString());
    }

    public List<RPG> usedThings(){ //override with your own
        return List.of(this);
    }

    public Map<NamespacedKey, CompoundTag> getStats() {
        Map<NamespacedKey, CompoundTag> stats = new HashMap<>();
        stats().tags.forEach((key, tag)->{
            NamespacedKey namespacedKey = NamespacedKey.fromString(key);
            if(namespacedKey!=null&&tag instanceof CompoundTag compoundTag){
                stats.put(namespacedKey,compoundTag);
            }
        });
        return stats;
    }

    public void addUsedStats(NamespacedKey key, Map<RPG,CompoundTag> map){
        if(stats().contains(key.asString())) map.put(this,getStat(key));
        usedThings().forEach(rpg -> {
            if(rpg!=this) addUsedStats(key,map);
        });
    }

    public void use(RPG rpg){
        for(Map.Entry<NamespacedKey,CompoundTag> entry : rpg.getStats().entrySet()){
            if(RpgRegistry.isRegisteredStat(entry.getKey())){
                Stat stat = RpgRegistry.initStat(entry.getKey(),rpg,new StatInst(entry.getValue()));
                stat.onStopUsingStat();
                stat.onUseStat(this);
            }
        }
    }
    public void stopUsing(RPG rpg){
        for(Map.Entry<NamespacedKey,CompoundTag> entry : rpg.getStats().entrySet()){
            if(RpgRegistry.isRegisteredStat(entry.getKey())){
                Stat stat = RpgRegistry.initStat(entry.getKey(),rpg,new StatInst(entry.getValue()));
                stat.onStopUsingStat();
            }
        }
    }

}
