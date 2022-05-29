package io.github.KidOfCubes;



import io.github.KidOfCubes.Events.*;
import io.github.KidOfCubes.Managers.RpgManager;
import io.github.KidOfCubes.Types.DamageType;

import java.util.*;

import static io.github.KidOfCubes.RpgPlugin.gson;

public abstract class RpgObject {
    String name;
    int level;
    private final Map<String,Integer> stats = new HashMap<>();
    float mana; //todo implement mana



    private RpgEntity parent;



    UUID parentUUID;
    boolean temporary = false;


    private UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid){
        this.uuid = uuid;
    }


    public RpgEntity getParent() {
        if(parent!=null){
            return parent;
        }else{
            if(parentUUID!=null){
                RpgEntity rpgEntityParent = RpgManager.getRpgEntity(parentUUID);
                if(rpgEntityParent!=null){
                    parent = rpgEntityParent;
                    return parent;
                }
            }
            return null;
        }
    }

    public void setParent(RpgEntity parent) {
        this.parent = parent;
        parentUUID = parent.getUUID();
    }



    public void addStat(String stat,int level){
        stats.put(stat,level);
    }
    public void removeStat(String stat){
        stats.remove(stat);
    }
    public Map<String,Integer> getStats(){
        return stats;
/*        List<Stat> statList = new ArrayList<>();
        for (List<Stat> tempList : stats.values()) {
            statList.addAll(tempList);
        }
        return statList;*/
    }
    public Map<String,Integer> getEffectiveStats(){
        return getStats();
    }
    public boolean hasStat(String name){
        return getEffectiveStats().containsKey(name);
    }

    public void attack(double amount, RpgEntity victim){
        RpgEntityDamageEvent event = new RpgEntityDamageByObjectEvent(victim, DamageType.Physical, amount,this);
        event.callEvent();
        victim.livingEntity.damage(event.getTotalDamage());
    }

    public void activateStat(String name){
        getActivateStatEvent(name).callEvent();
    }
    public RpgActivateStatEvent getActivateStatEvent(String name){
        return new RpgActivateStatEvent().parent(this).addTriggerStat(name);
    }

    public String toJson(){
        RpgElementJsonContainer container = new RpgElementJsonContainer();
        container.name = name;
        container.level = level;
        Map<String,Integer> allStats = getStats();
        container.stats = new HashMap<String,Integer>();
        container.stats.putAll(allStats);
        return gson.toJson(container);
    }
    void loadFromJson(String json){
        RpgElementJsonContainer container = gson.fromJson(json,RpgElementJsonContainer.class);
        level = container.level;
        name = container.name;
        stats.putAll(container.stats);
    }
    public static class RpgElementJsonContainer{
        public String name;
        public int level;
        public UUID parent;
        public Map<String,Integer> stats;
    }

    @Override
    public boolean equals(Object other){
        if(other==null){
            return false;
        }
        if(other instanceof RpgObject otherRpgObject){
            return getUUID().equals(otherRpgObject.getUUID());
        }else{
            return false;
        }
    }

    public abstract void save();
}
