package io.github.kidofcubes.rpgplugin;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class RPGImpl implements RPG{

    @Override
    public TagWrapper getTag() {
        return base;
    }

    protected final TagWrapper base;
    public RPGImpl(TagWrapper base){
        this.base=base;
    }


    protected TagWrapper dataTag(){
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
    public int getLevel(){ return dataTag().getInt(LVL_KEY); }
    public void setLevel(int level){ dataTag().putInt(LVL_KEY,level); }

    /**
     * Defaults to 0.
     * @return
     */
    public double getMana(){ return dataTag().getDouble(MANA_KEY); }
    public void setMana(double mana){ dataTag().putDouble(MANA_KEY,mana); }


    protected TagWrapper statsTag(){
        if(!base.contains("stats")) base.put("stats", new TagWrapper());
        return base.getCompound("stats");
    }
    public void addStat(Stat stat){
        if(!RpgRegistry.isRegisteredStat(stat.getIdentifier())) throw new IllegalArgumentException("Stat "+stat.getIdentifier()+" is not registered!");
        statsTag().put(stat.getIdentifier().asString(),stat.getData());
    }
    public void addStat(NamespacedKey key){
        if(!RpgRegistry.isRegisteredStat(key)) throw new IllegalArgumentException("Stat "+key+" is not registered!");
        statsTag().put(key.asString(),new TagWrapper());
    }
    public boolean hasStat(NamespacedKey key){
        if(key==null) return false;
        return RpgRegistry.isRegisteredStat(key) && statsTag().contains(key.asString());
    }

    /**
     * Returns the stat if found and registered, else throws error
     * @param key
     * @return
     */
    @NotNull
    public Stat getStat(NamespacedKey key){
        if(!hasStat(key)) throw new RuntimeException("Stat "+key+" was not found and/or registered");
        return RpgRegistry.initStat(key,this,(Objects.requireNonNull(statsTag().getCompound(key.asString()))));
    }

    /**
     * Removes a stat (if the stat is not found, won't do anything)
     * @param key The stat key
     */
    public void removeStat(NamespacedKey key){
        statsTag().remove(key.asString());
    }

    public Map<NamespacedKey, Stat> getStats() {
        Map<NamespacedKey, Stat> stats = new HashMap<>();
        statsTag().getAllKeys().forEach((key)->{
            NamespacedKey namespacedKey = NamespacedKey.fromString(key);
            TagWrapper tag = statsTag().getCompound(key);
            if(namespacedKey!=null&&tag!=null){
                stats.put(namespacedKey,RpgRegistry.initStat(namespacedKey,this,tag));
            }
        });
        return stats;
    }

    public void addUsedStats(NamespacedKey key, Map<RPG,Stat> map){
        if(hasStat(key)) map.put(this,getStat(key).setUser(this));
    }


}
