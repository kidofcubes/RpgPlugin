package io.github.kidofcubes;

import org.bukkit.event.Event;

import java.util.*;

public abstract class TimedStat extends Stat{
    static final Map<TimedStat,Integer> statInstances = new HashMap<>(); //key stat, value count

    /**
     * Override this if you want to support events too
     * @param event
     * @return
     */
    @Override
    public RpgObject checkObject(Event event) {
        return null;
    }

    @Override
    public void onAddStat(RpgObject object) {
        super.onAddStat(object);
        System.out.println("ADDED A STAT ONTO "+object.getName());
        statInstances.put(this,0);
    }

    @Override
    public void onRemoveStat(RpgObject object) {
        super.onRemoveStat(object);
        statInstances.remove(this);
    }

    @Override
    public void onUseStat(RpgObject object) {
        super.onUseStat(object);
        //statInstances.put(getParent(),this);
    }

    @Override
    public void onStopUsingStat(RpgObject object) {
        super.onStopUsingStat(object);
        //objectsWithStat.remove(getParent());
        //statInstances.remove(getParent(),this);
    }

    public int getInterval(){
        return 1;
    }

    /**
     * Runs checks for event, and runs stat if passes
     * Checks are:
     * stat on check object
     * level!=0
     * check object mana enough
     *
     * @param event
     */
    @Override
    public void trigger(Event event) {
        if(event==null) { //check if timed event
            //if(count>=this.getInterval()){
            //    count=0;
            HashSet<Map.Entry<TimedStat,Integer>> entries = new HashSet<>(statInstances.entrySet());
            for (Map.Entry<TimedStat,Integer> entry: entries) {
                entry.setValue(entry.getValue()+1);
                TimedStat statInstance = entry.getKey();
                if(entry.getValue()>=statInstance.getInterval()){
                    entry.setValue(0);
                    //System.out.println("COUNT IS "+count+" AND INTERVAL IS "+statInstance.getInterval()+" ON "+statInstance.getName() +" WHOS PARENT IS "+statInstance.getParent().getName()+" AND USER IS "+statInstance.getUser().getName());
                    double manaCost = statInstance.getManaCost();
                    if(manaCost == 0){
                        statInstance.activateStat(null);
                    }else{
                        if(manaSourceFromParent()){
                            if (statInstance.getParent().getMana() >= manaCost) {
                                statInstance.getParent().setMana(statInstance.getParent().getMana() - manaCost);
                                statInstance.activateStat(null);
                            }
                        }else{
                            if (statInstance.getUser().getMana() >= manaCost) {
                                statInstance.getUser().setMana(statInstance.getUser().getMana() - manaCost);
                                statInstance.activateStat(null);
                            }
                        }
                    }
                }

            }
            //}
        }else{
            super.trigger(event);
        }
    }
}
