package io.github.KidOfCubes.Stats;

import io.github.KidOfCubes.Events.RpgEntityHealEvent;
import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class HealingTest extends Stat {

    @Override
    public RpgElement elementToStatCheck(Event event) {
        return ((RpgEntityHealEvent)event).getEntity();
    }

    @Override
    public void run(Event event) {
        logger.info("HEAL TEST");
        ((RpgEntityHealEvent)event).setAmount(((RpgEntityHealEvent) event).getAmount()+5);
    }

}
