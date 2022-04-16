package io.github.KidOfCubes.Stats;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import org.bukkit.event.Event;

public class BaseDamage extends Stat {
    public BaseDamage(int level, RpgElement statParent) {
        super(level, statParent);
    }

    @Override
    protected void run(RpgElement statParent, RpgElement caller, int level, Event event) {

    }
}
