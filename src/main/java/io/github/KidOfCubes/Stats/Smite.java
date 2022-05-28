package io.github.KidOfCubes.Stats;

import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.RpgObject;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.DamageType;
import org.bukkit.event.Event;

public class Smite extends Stat {
    @Override
    public RpgObject elementToStatCheck(Event event) {
        return ((RpgActivateStatEvent)event).getParent();
    }

    @Override
    public void run(Event event) {
        RpgActivateStatEvent activateStatEvent = ((RpgActivateStatEvent)event);
        if(activateStatEvent.getTarget() instanceof RpgEntity entity){
            entity.livingEntity.getWorld().strikeLightningEffect(entity.livingEntity.getLocation());
            entity.damage(DamageType.Thunder,10);
        }

    }
}
