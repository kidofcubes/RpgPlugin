package io.github.kidofcubes;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatSet {
    Map<String, Stat> statMap = new HashMap<>();
    private final RpgObject parent;
    public StatSet(RpgObject parent){
        this.parent = parent;
    }

    public boolean hasStat(String stat){
        return statMap.containsKey(stat);
    }
    @Nullable
    public Stat getStat(String stat){
        return statMap.getOrDefault(stat,null);
    }

    public void addStats(List<Stat> addStats, boolean force){
        for (Stat stat :
                addStats) {
            addStat(stat,force);
        }
    }
    public void addStats(StatSet addStats, boolean force){
        for (Stat stat :
                addStats.statMap.values()) {
            addStat(stat,force);
        }
    }
    public StatSet addStat(Stat stat){
        return addStat(stat,false);
    }
    public StatSet addStat(Stat stat, boolean force){
        if(!force) {
            Stat previous = getStat(stat.getName());
            if (previous != null) {
                if (previous.getLevel() < stat.getLevel()) {
                    previous.setLevel(stat.getLevel());
                }
            }else{
                return addStat(stat,true);
            }
        }else{
            statMap.put(stat.getName(),stat);
            stat.onAddStat(parent);
        }
        return this;
    }


    @Nullable
    public void removeStat(String stat){
        Stat removed = statMap.remove(stat);
        if(removed!=null) {
            removed.onRemoveStat(parent);
        }
    }
    @Nullable
    public void removeStat(Stat stat,boolean force){
        if(!force){
            Stat previous = getStat(stat.getName());
            if (previous != null) {
                if (previous.getLevel() > stat.getLevel()) {
                    //previous.setLevel(previous.getLevel()-stat.getLevel());
                }else{
                    removeStat(stat,true);
                }
            }
        }else{
            statMap.remove(stat.getName());
            stat.onRemoveStat(parent);
        }
    }
    public void removeStats(List<Stat> statList, boolean force){
        for (Stat stat : statList) {
            removeStat(stat,force);
        }
    }


}
