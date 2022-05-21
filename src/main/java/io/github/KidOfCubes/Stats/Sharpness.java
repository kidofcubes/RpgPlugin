package io.github.KidOfCubes.Stats;

import static io.github.KidOfCubes.RpgPlugin.*;

import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.Events.RpgEntityDamageByEntityEvent;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.Event;

public class Sharpness extends Stat {

    static String description = "Increaces damage by 1 heart per level";
    static StatType statType = StatType.stat;

    public Sharpness(int level) {
        super(level);
    }

    @Override
    public boolean activateConditions(RpgActivateStatEvent event) {
        return event.getTriggerEvent() instanceof RpgEntityDamageByEntityEvent;
    }

    @Override
    protected void run(RpgActivateStatEvent event) {
        logger.info("sharpness activated");
        if (event.getTriggerEvent() instanceof RpgEntityDamageByEntityEvent damageEvent) {
            damageEvent.setDamage(damageEvent.getDamage() + (level * 2f));
        }
    }
}
