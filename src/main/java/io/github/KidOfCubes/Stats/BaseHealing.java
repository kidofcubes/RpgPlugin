/*
package io.github.KidOfCubes.Stats;

import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.Events.RpgEntityHealByElementEvent;
import io.github.KidOfCubes.Events.RpgEntityHealEvent;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.Event;

public class BaseHealing extends Stat {
    public static String description = "BaseHealing";
    public static StatType statType = StatType.hidden;
    public static int runPriority = Integer.MIN_VALUE;

    public BaseHealing(int level) {
        super(level);
    }

    @Override
    public boolean activateConditions(RpgActivateStatEvent event) {
        return event.getTriggerStats().contains(this);
    }

    @Override
    protected void run(RpgActivateStatEvent event) {
        if(event.getTriggerEvent() instanceof RpgEntityHealByElementEvent healByEntityEvent){
            healByEntityEvent.setChange(healByEntityEvent.getChange()+level);
        }
    }
}
*/
