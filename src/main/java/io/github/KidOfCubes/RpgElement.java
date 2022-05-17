package io.github.KidOfCubes;



import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.Events.RpgEntityDamageByEntityEvent;
import io.github.KidOfCubes.Events.RpgEntityHealByEntityEvent;
import io.github.KidOfCubes.Events.RpgEntityHealthChangeEvent;
import org.bukkit.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static io.github.KidOfCubes.RpgPlugin.gson;

public class RpgElement {
    public String name;
    public int level;
    protected List<Stat> stats = new ArrayList<>();
    public float mana; //todo implement mana deez nuts
    public RpgEntity parent;


    public void addStat(Stat stat){
/*        if(!stats.containsKey(stat.getTriggerType())){
            stats.put(stat.getTriggerType(), new ArrayList<>());
        }*/
        stats.add(stat);


    }
    public List<Stat> getStats(){
        return stats;
/*        List<Stat> statList = new ArrayList<>();
        for (List<Stat> tempList : stats.values()) {
            statList.addAll(tempList);
        }
        return statList;*/
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
        RpgActivateStatEvent activateStatEvent = new RpgActivateStatEvent();
        for (Stat stat: getEffectiveStats()) {
            if(stat.getName().equalsIgnoreCase(name)){
                activateStatEvent.addTriggerStat(stat);
            }
        }
        activateStatEvent.callEvent(this);
    }

    public void eventActivateStats(Event event){
        RpgActivateStatEvent activateStatEvent = new RpgActivateStatEvent();
        if(event instanceof RpgEntityHealthChangeEvent rpgEntityHealthChangeEvent){
            activateStatEvent.setTarget(rpgEntityHealthChangeEvent.getEntity());
            //activateStatEvent.setParent(rpgEntityHealthChangeEvent.getEntity());
        }
        if(event instanceof RpgEntityDamageByEntityEvent rpgEntityDamageByEntityEvent){
            activateStatEvent.setCaster(rpgEntityDamageByEntityEvent.getAttacker());
        }
        if(event instanceof RpgEntityHealByEntityEvent rpgEntityHealByEntityEvent){
            activateStatEvent.setCaster(rpgEntityHealByEntityEvent.getHealer());
        }
        activateStatEvent.callEvent(this);
    }

    public String toJson(){
        RpgElementJsonContainer container = new RpgElementJsonContainer();
        container.name = name;
        container.level = level;
        List<Stat> allStats = getStats();
        container.stats = new HashMap<String,Integer>();
        for (int i = 0; i < allStats.size(); i++) {
            container.stats.put(allStats.get(i).getName(),allStats.get(i).level);
        }
        return gson.toJson(container);
    }
    public static RpgElement fromJson(String json){
        RpgElement rpgElement = new RpgElement();
        rpgElement.loadFromJson(json);
        return rpgElement;
    }
    void loadFromJson(String json){
        RpgElementJsonContainer container = gson.fromJson(json,RpgElementJsonContainer.class);
        level = container.level;
        name = container.name;
        for(Map.Entry<String,Integer> statProperties : container.stats.entrySet()){
            try {
                Class<? extends Stat> stat = Class.forName("io.github.KidOfCubes.Stats."+statProperties.getKey()).asSubclass(Stat.class);
                Stat realStat = (Stat)stat.getConstructors()[0].newInstance(statProperties.getValue());
                addStat(realStat);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    public static class RpgElementJsonContainer{
        public String name;
        public int level;
        public Map<String,Integer> stats;
    }
}
