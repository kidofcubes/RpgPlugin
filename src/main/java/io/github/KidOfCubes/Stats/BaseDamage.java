package io.github.KidOfCubes.Stats;

import io.github.KidOfCubes.Events.RpgEntityDamageByEntityEvent;
import io.github.KidOfCubes.Events.RpgEntityHealByEntityEvent;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatTriggerType;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.Event;

public class BaseDamage extends Stat {
    public static String description = "BaseDamage";
    public static StatTriggerType triggerType = StatTriggerType.onAttack;
    public static StatType statType = StatType.hidden;
    public BaseDamage(int level, RpgElement statParent) {
        super(level, statParent);
    }
    @Override
    protected void run(RpgElement statParent, RpgElement caller, int level, Event event) {
        if(event instanceof RpgEntityDamageByEntityEvent damageByEntityEvent){
            damageByEntityEvent.setDamage(damageByEntityEvent.getChange()+level);
        }
    }
}
