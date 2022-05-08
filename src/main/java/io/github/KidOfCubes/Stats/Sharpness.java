package io.github.KidOfCubes.Stats;

import static io.github.KidOfCubes.RpgPlugin.*;

import io.github.KidOfCubes.Events.RpgEntityDamageByEntityEvent;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatTriggerType;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.Event;

public class Sharpness extends Stat {

    public Sharpness(int level, RpgElement statParent) {
        super(level, statParent);
        description = "Increaces damage by 1 heart per level";
        triggerType = StatTriggerType.onAttack;
        statType = StatType.stat;
    }

    @Override
    protected void run(RpgElement statParent, RpgElement statActivator, int level, Event event) {
        logger.info("sharpness activated");
        if(event!=null) {
            if (event instanceof RpgEntityDamageByEntityEvent damageEvent) {
                damageEvent.setDamage(damageEvent.getDamage() + (level * 2f));
            }
        }
    }
}
