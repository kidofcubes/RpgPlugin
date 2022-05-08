package io.github.KidOfCubes.Stats;

import io.github.KidOfCubes.Events.RpgEntityHealByEntityEvent;
import io.github.KidOfCubes.Events.RpgEntityHealEvent;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatTriggerType;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.Event;

public class BaseHealing extends Stat {
    public static String description = "BaseHealing";
    public static StatTriggerType triggerType = StatTriggerType.onHeal;
    public static StatType statType = StatType.hidden;
    public static int runPriority = Integer.MIN_VALUE;
    public BaseHealing(int level, RpgElement statParent) {
        super(level, statParent);
    }

    @Override
    protected void run(RpgElement statParent, RpgElement caller, int level, Event event) {
        if(event instanceof RpgEntityHealByEntityEvent healByEntityEvent){
            healByEntityEvent.setChange(healByEntityEvent.getChange()+level);
        }
    }
}
