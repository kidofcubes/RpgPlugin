package io.github.kidofcubes;

import io.github.kidofcubes.events.RpgActivateStatEvent;
import io.github.kidofcubes.managers.RpgManager;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TimedStat extends Stat{
    static final Map<TimedStat,Integer> objectsWithStat = new HashMap<>(); //key stat, value count

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
        objectsWithStat.put(this,0);
    }

    @Override
    public void onRemoveStat(RpgObject object) {
        super.onRemoveStat(object);
        objectsWithStat.remove(this);
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
                for (Map.Entry<TimedStat,Integer> entry: objectsWithStat.entrySet()) {
                    entry.setValue(entry.getValue()+1);
                    TimedStat statInstance = entry.getKey();
                    if(entry.getValue()>=statInstance.getInterval()){
                        entry.setValue(0);
                        //System.out.println("COUNT IS "+count+" AND INTERVAL IS "+statInstance.getInterval()+" ON "+statInstance.getName() +" WHOS PARENT IS "+statInstance.getParent().getName()+" AND USER IS "+statInstance.getUser().getName());
                        double manaCost = statInstance.getManaCost();
                        if(manaCost == 0){
                            statInstance.run(null);
                        }else{
                            if(manaSourceFromParent()){
                                if (statInstance.getParent().getMana() >= manaCost) {
                                    statInstance.getParent().setMana(statInstance.getParent().getMana() - manaCost);
                                    statInstance.run(null);
                                }
                            }else{
                                if (statInstance.getUser().getMana() >= manaCost) {
                                    statInstance.getUser().setMana(statInstance.getUser().getMana() - manaCost);
                                    statInstance.run(null);
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
