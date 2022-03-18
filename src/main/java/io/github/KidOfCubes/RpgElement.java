package io.github.KidOfCubes;

import io.github.KidOfCubes.Types.StatTriggerType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpgElement {
    public String name;
    public int level;
    public Map<StatTriggerType, List<Stat>> stats = new HashMap<>();

    public void addStat(Stat stat){
        if(!stats.containsKey(stat.triggerType)){
            stats.put(stat.triggerType,new ArrayList<Stat>());
        }
        stats.get(stat.triggerType).add(stat);
    }


}
