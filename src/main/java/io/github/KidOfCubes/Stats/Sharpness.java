package io.github.KidOfCubes.Stats;

import static io.github.KidOfCubes.RpgPlugin.*;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatTriggerType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Sharpness extends Stat {

    public Sharpness(int level, RpgElement statParent) {
        super("Sharpness",
                level,
                StatTriggerType.onAttack,
                statParent,
                true);
    }

    @Override
    protected void run(RpgElement statActivator, int level, Event event) {
        logger.info("sharpness activated");
        if(event!=null) {
            if (event instanceof EntityDamageByEntityEvent damageEvent) {
                damageEvent.setDamage(damageEvent.getDamage() + (level * 5f));
            }
        }
    }
}
