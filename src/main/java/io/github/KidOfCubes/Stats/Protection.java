/*
package io.github.KidOfCubes.Stats;

import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.Events.RpgEntityDamageEvent;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class Protection extends Stat {

    public static String description = "Decreaces raw damage taken by 1.5 hearts per level";
    public static StatType statType = StatType.stat;

    public Protection(int level) {
        super(level);
    }

    @Override
    public boolean activateConditions(RpgActivateStatEvent event) {
        return event.getTriggerEvent() instanceof EntityDamageEvent;
    }

    @Override
    protected void run(RpgActivateStatEvent event) {
        logger.info("protection activated with level "+level);
        if (event.getTriggerEvent() instanceof RpgEntityDamageEvent damageEvent) {
            damageEvent.setDamage(damageEvent.getDamage() - (level * 1.5f));
        }
    }
}*/
