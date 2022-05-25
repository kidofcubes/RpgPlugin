package io.github.KidOfCubes;



import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.Events.RpgEntityDamageEvent;
import io.github.KidOfCubes.Events.RpgEntityHealByElementEvent;
import io.github.KidOfCubes.Events.RpgEntityHealthChangeEvent;
import org.bukkit.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static io.github.KidOfCubes.RpgPlugin.gson;
import static io.github.KidOfCubes.RpgPlugin.logger;

public class RpgElement {
    public String name;
    public int level;
    protected List<String> stats = new ArrayList<>();
    public float mana; //todo implement mana
    public RpgEntity parent;


    public void addStat(Stat stat){
/*        if(!stats.containsKey(stat.getTriggerType())){
            stats.put(stat.getTriggerType(), new ArrayList<>());
        }*/
        stats.add(stat.getName());


    }

    public void addStat(String stat){
/*        if(!stats.containsKey(stat.getTriggerType())){
            stats.put(stat.getTriggerType(), new ArrayList<>());
        }*/
        stats.add(stat);


    }
    public List<String> getStats(){
        return stats;
/*        List<Stat> statList = new ArrayList<>();
        for (List<Stat> tempList : stats.values()) {
            statList.addAll(tempList);
        }
        return statList;*/
    }
    public List<String> getEffectiveStats(){
        return getStats();
    }
    public void runStats(RpgElement caller){
        List<String> statsSorted = getEffectiveStats();
    }
    public boolean hasStat(String name){
        return getEffectiveStats().contains(name);
/*        if(stats.contains(name)){

        }
        for (Stat stat :
                getEffectiveStats()) {
            if (stat.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;*/
    }
/*    public Stat getStat(String name){
        for (Stat stat : getEffectiveStats()) {
            if(stat.getName().equalsIgnoreCase(name)){
                return stat;
            }
        }
        return null;
    }*/


    public void attack(double amount, RpgEntity victim){
        RpgEntityDamageEvent event = new RpgEntityDamageEvent(victim,amount,this);
        event.callEvent();
        victim.livingEntity.damage(event.getDamage());
    }

    public void activateStat(String name){ //todo fix
/*        RpgActivateStatEvent activateStatEvent = new RpgActivateStatEvent().caster(this);
        for (Stat stat: getEffectiveStats()) {
            if(stat.getName().equalsIgnoreCase(name)){
                logger.info("I HAVE A STAT "+ stat.getName() + " OF LEVEL "+stat.level);
                activateStatEvent.addTriggerStat(stat);
            }
        }
        activateStatEvent.callEvent(this);*/
    }

/*    public void activateStat(Event event){
        RpgActivateStatEvent activateStatEvent = new RpgActivateStatEvent();
        if(event instanceof RpgEntityHealthChangeEvent rpgEntityHealthChangeEvent){
            activateStatEvent.setTarget(rpgEntityHealthChangeEvent.getEntity());
            //activateStatEvent.setParent(rpgEntityHealthChangeEvent.getEntity());
        }
        if(event instanceof RpgEntityDamageByEntityEvent rpgEntityDamageByEntityEvent){
            activateStatEvent.setCaster(rpgEntityDamageByEntityEvent.getAttacker());
        }
        if(event instanceof RpgEntityHealByElementEvent rpgEntityHealByEntityEvent){
            activateStatEvent.setCaster(rpgEntityHealByEntityEvent.getHealer());
        }
        activateStatEvent.setTriggerEvent(event);
        activateStatEvent.callEvent(this);
    }*/

    public String toJson(){
        RpgElementJsonContainer container = new RpgElementJsonContainer();
        container.name = name;
        container.level = level;
        List<String> allStats = getStats();
        container.stats = new HashMap<String,Integer>();
        for (int i = 0; i < allStats.size(); i++) {
            container.stats.put(allStats.get(i),/*allStats.get(i).level*/-1);
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
            addStat(statProperties.getKey());
        }
    }
    public static class RpgElementJsonContainer{
        public String name;
        public int level;
        public Map<String,Integer> stats;
    }

    @Override
    public boolean equals(Object other){ //use uuid system later not work gud
        if(other==null){
            return false;
        }
        if(other instanceof RpgElement otherRpgElement){
            if(!otherRpgElement.name.equalsIgnoreCase(name)){
                return false;
            }
            if(otherRpgElement.level != level){
                return false;
            }
            if(otherRpgElement.mana != mana){
                return false;
            }
            List<String> otherEffectiveStats = otherRpgElement.getEffectiveStats();
            List<String> myEffectiveStats = getEffectiveStats();
            if(otherEffectiveStats.size()!=myEffectiveStats.size()){
                return false;
            }
            for (int i = 0; i < otherEffectiveStats.size(); i++) {
                if(!otherEffectiveStats.get(i).equals(myEffectiveStats.get(i))){
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }
}
