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
        if(event==null) {
            for (int i = objectsWithStat.size() - 1; i > -1; i--) {
                RpgObject toCheck = objectsWithStat.get(i);
                if(toCheck instanceof RpgEntity rpgEntity) if(!rpgEntity.exists()){
                    objectsWithStat.remove(i);
                    continue;
                }
                if (toCheck != null) {
                    Stat statInstance = toCheck.getEffectiveStatsMap().getOrDefault(this.getClass().getName(), null);
                    if (statInstance != null) {
                        if (toCheck.getMana() >= getManaCost() || getManaCost() == 0) {
                            toCheck.setMana(toCheck.getMana() - getManaCost());
                            statInstance.run(null);
                        }
                    }
                }
            }
        }else{
            super.trigger(event);
        }
    }
}
