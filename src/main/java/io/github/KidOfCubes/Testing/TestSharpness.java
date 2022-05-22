package io.github.KidOfCubes.Testing;

import io.github.KidOfCubes.Events.RpgEntityDamageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import org.bukkit.event.Event;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class TestSharpness implements Listener {

    static String description = "Increaces damage by 1 heart per level";


    public int level = -1;



    /*@EventHandler
    protected void run(RpgEntityDamageEvent event) {
        logger.info("sharpnessTest activated with level "+level);
        event.setDamage(event.getDamage() + (level * 2f));
    }*/
}

