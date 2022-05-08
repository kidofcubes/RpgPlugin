package io.github.KidOfCubes;

import io.github.KidOfCubes.Types.StatTriggerType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static io.github.KidOfCubes.RpgPlugin.gson;

public class RpgElement {
    public String name;
    public int level;
    protected Map<StatTriggerType, List<Stat>> stats = new HashMap<>();
    public float mana; //todo implement mana deez nuts
    public RpgEntity parent;


    public void addStat(Stat stat){
        if(!stats.containsKey(stat.getTriggerType())){
            stats.put(stat.getTriggerType(), new ArrayList<>());
        }
        stats.get(stat.getTriggerType()).add(stat);

        stats.get(stat.getTriggerType()).sort((stat1, stat2) -> stat2.getRunPriority() - stat1.getRunPriority());
    }
    public List<Stat> getStats(){
        List<Stat> statList = new ArrayList<>();
        for (List<Stat> tempList : stats.values()) {
            statList.addAll(tempList);
        }
        return statList;
    }
    public List<Stat> getEffectiveStats(){
        return getStats();
    }
    public void runStats(RpgElement caller){
        List<Stat> statsSorted = getEffectiveStats();
    }
    public boolean hasStat(String name){
        for (Stat stat :
                getEffectiveStats()) {
            if (stat.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    public Stat getStat(String name){
        for (Stat stat : getEffectiveStats()) {
            if(stat.getName().equalsIgnoreCase(name)){
                return stat;
            }
        }
        return null;
    }


    public void activateStat(String name){
        
    }

    public String toJson(){
        RpgElementJsonContainer container = new RpgElementJsonContainer();
        container.name = name;
        container.level = level;
        List<Stat> allStats = getStats();
        container.stats = new String[allStats.size()][];
        for (int i = 0; i < allStats.size(); i++) {
            container.stats[i] = new String[] {allStats.get(i).getName(),allStats.get(i).level+""};
        }
        return gson.toJson(container);
    }
    public static RpgElement fromJson(String json){
        RpgElement rpgElement = new RpgElement();
        RpgElementJsonContainer container = gson.fromJson(json,RpgElementJsonContainer.class);
        rpgElement.level = container.level;
        rpgElement.name = container.name;

        for (int i = 0; i < container.stats.length; i++) {
            try {
                Class<? extends Stat> stat = Class.forName("io.github.KidOfCubes.Stats."+container.stats[i][0]).asSubclass(Stat.class);
                Stat realStat = (Stat)stat.getConstructors()[0].newInstance(Integer.parseInt(container.stats[i][1]),rpgElement);
                rpgElement.addStat(realStat);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return rpgElement;
    }
    public static class RpgElementJsonContainer{
        public String name;
        public int level;
        public String[][] stats;
    }
}
