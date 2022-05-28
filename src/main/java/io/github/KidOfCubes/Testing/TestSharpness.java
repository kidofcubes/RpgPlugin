package io.github.KidOfCubes.Testing;

import org.bukkit.event.Listener;

public class TestSharpness implements Listener {

    static String description = "Increaces damage by 1 heart per level";


    public int level = -1;



    /*@EventHandler
    protected void run(RpgEntityDamageEvent event) {
        logger.info("sharpnessTest activated with level "+level);
        event.setDamage(event.getDamage() + (level * 2f));
    }*/
}

