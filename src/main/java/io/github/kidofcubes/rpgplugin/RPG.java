package io.github.kidofcubes.rpgplugin;


import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import java.util.List;
import java.util.Map;




/**
 * Immutable Wrapper for CompoundTag of rpg things
 */
public interface RPG {
    public CompoundTag getTag();
    public static final NamespacedKey RPG_TAG_KEY = new NamespacedKey("rpg_plugin", "rpg");
    static final NamespacedKey DEFAULT_TYPE_KEY = new NamespacedKey("rpg_plugin","default_type");
    static final String TYPE_KEY = "type";
    static final String LVL_KEY = "lvl";
    static final String MANA_KEY = "mana";
    static final String USED_KEY = "used";

    public void setRpgType(NamespacedKey namespacedKey);

    public NamespacedKey getRpgType();

    public String getName();

    /**
     * Defaults to 0.
     * @return
     */
    public int getLevel();
    public void setLevel(int level);

    /**
     * Defaults to 0.
     * @return
     */
    public double getMana();
    public void setMana(double mana);
    public void addStat(Stat stat);
    public boolean hasStat(NamespacedKey key);


    /**
     * Returns the stat if found and registered, else throws error
     * @param key
     * @return
     */
    public Stat getStat(NamespacedKey key);

    /**
     * Removes a stat (if the stat is not found, won't do anything)
     * @param key The stat key
     */
    public void removeStat(NamespacedKey key);
    public Map<NamespacedKey, Stat> getStats();
    public void addUsedStats(NamespacedKey key, Map<RPG,Stat> map);

    /**
     * Convenience method for using addUsedStats
     * @param key
     * @return
     */
    default Map<RPG,Stat> getUsedStats(NamespacedKey key){
        Map<RPG,Stat> map = new HashMap<>();
        addUsedStats(key,map);
        return map;
    }

    default RPG getRpgInstance(){
        return this;
    }



}
