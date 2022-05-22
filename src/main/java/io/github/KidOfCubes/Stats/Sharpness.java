package io.github.KidOfCubes.Stats;

import static io.github.KidOfCubes.RpgPlugin.*;

import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.Events.RpgEntityDamageEvent;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatHandler;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;

public class Sharpness extends Stat {

    static String description = "Increaces damage by 1 heart per level";


    public Sharpness(int level) {
        super(level);
    }



    @Override
    public RpgElement elementToStatCheck(Event event) {
        return ((RpgEntityDamageEvent)event).getCause();
    }


    @Override
    @EventHandler
    @StatHandler(listenEvent = RpgEntityDamageEvent.class)
    public void run(Event event) { //non static is faster
        if(event instanceof RpgEntityDamageEvent damageEvent) {
            logger.info("sharpness activated with level "/*+level*/);
            damageEvent.setDamage(damageEvent.getDamage() + (/*level * */2f));
        }
    }
}
