package io.github.KidOfCubes.Stats;

import static io.github.KidOfCubes.RpgPlugin.*;

import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.Events.RpgEntityDamageByElementEvent;
import io.github.KidOfCubes.Events.RpgEntityDamageEvent;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.DamageType;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;

public class Sharpness extends Stat {

    static String description = "Increaces damage by 1 heart per level";



    @Override
    public RpgElement elementToStatCheck(Event event) {
        return ((RpgEntityDamageByElementEvent)event).getCause();
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
