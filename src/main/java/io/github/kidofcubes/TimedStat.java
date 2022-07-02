package io.github.kidofcubes;

import io.github.kidofcubes.events.RpgActivateStatEvent;
import io.github.kidofcubes.managers.RpgManager;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public abstract class TimedStat extends Stat{
    static final List<RpgObject> objectsWithStat = new ArrayList<>();

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
        objectsWithStat.add(object);
    }

    @Override
    public void onRemoveStat(RpgObject object) {
        super.onRemoveStat(object);
        objectsWithStat.remove(object);
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
    int count = 0;
    @Override
    public void trigger(Event event) {
        if(event==null) {
            count++;
            if(count>=getInterval()){
                count=0;
                for (int i = objectsWithStat.size() - 1; i > -1; i--) {
                    RpgObject toCheck = objectsWithStat.get(i);
                    if(toCheck instanceof RpgEntity rpgEntity) if(!rpgEntity.exists()){
                        objectsWithStat.remove(i);
                        continue;
                    }
                    if (toCheck != null) {
                        List<Stat> statInstances = toCheck.getEffectiveStatsMap().getOrDefault(this.getClass().getName(), null);
                        if (statInstances != null) {
                            for (Stat statInstance: statInstances) {
                                double manaCost = statInstance.getManaCost();
                                if (manaCost == 0 || toCheck.getMana() >= manaCost) {
                                    toCheck.setMana(toCheck.getMana() - manaCost);
                                    statInstance.run(null);
                                }
                            }
                        }
                    }
                }
            }
        }else{
            super.trigger(event);
        }
    }
}
