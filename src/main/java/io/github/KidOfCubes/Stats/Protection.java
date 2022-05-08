package io.github.KidOfCubes.Stats;

import io.github.KidOfCubes.Events.RpgEntityDamageEvent;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatTriggerType;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class Protection extends Stat {

    public Protection(int level, RpgElement parent) {
        super(level,parent);
        description = "Decreaces raw damage taken by 1.5 hearts per level";
        triggerType = StatTriggerType.onDamage;
        statType = StatType.stat;
    }

    @Override
    protected void run(RpgElement statParent, RpgElement statActivator, int level, Event event) {
        logger.info("protection activated with level "+level);
        if(event!=null) {
            if (event instanceof RpgEntityDamageEvent damageEvent) {
                damageEvent.setDamage(damageEvent.getDamage() - (level * 1.5f));
            }
        }
    }
}