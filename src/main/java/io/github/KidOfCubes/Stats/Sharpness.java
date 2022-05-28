package io.github.KidOfCubes.Stats;

import io.github.KidOfCubes.Events.RpgEntityDamageByObjectEvent;
import io.github.KidOfCubes.Events.RpgEntityDamageEvent;
import io.github.KidOfCubes.RpgObject;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.DamageType;
import org.bukkit.event.Event;

public class Sharpness extends Stat {

    static String description = "Increaces damage by 1 heart per level";



    @Override
    public RpgObject elementToStatCheck(Event event) {
        return ((RpgEntityDamageByObjectEvent)event).getCause();
    }



    @Override
    //@StatHandler(listenEvent = RpgEntityDamageEvent.class)
    public void run(Event event) {
        ((RpgEntityDamageEvent)event).addDamage(DamageType.Physical,100);
/*        if(event instanceof RpgEntityDamageEvent damageEvent) {
            logger.info("sharpness activated with level "*//*+level*//*);
            damageEvent.setDamage(damageEvent.getDamage() + (*//*level * *//*200f));
        }*/
    }
}
