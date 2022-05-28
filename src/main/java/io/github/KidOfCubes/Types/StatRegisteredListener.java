package io.github.KidOfCubes.Types;

import io.github.KidOfCubes.RpgObject;
import io.github.KidOfCubes.RpgPlugin;
import io.github.KidOfCubes.Stat;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.jetbrains.annotations.NotNull;

import java.io.InvalidClassException;
import java.util.*;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class StatRegisteredListener extends RegisteredListener
{

    Stat stat;
    List<Class<? extends Event>> listenEvents;

    public StatRegisteredListener(Stat stat,List<Class<? extends Event>> listenEvents) throws InvalidClassException {
        super(stat, null, EventPriority.NORMAL, RpgPlugin.plugin, false);
        this.listenEvents = listenEvents;
        this.stat = stat;
        logger.info("registed listener (REAL)");
    }


    @Override
    public void callEvent(@NotNull Event event)  //DOUBLE-TRIPLE-(42 ms first time wtf) SPEED RN (this call event takes 0.0041 - 0.0024 ms)(calling the event) (orig takes 0.13 ms ????????)
    {
        long startTime = System.nanoTime();
        for (int i = 0; i < listenEvents.size(); i++) {
            if(listenEvents.get(i).isAssignableFrom(event.getClass())) {
                RpgObject toCheck = stat.elementToStatCheck(event);
                if (toCheck != null) {
                    if (toCheck.hasStat(stat.getName())) {
                        stat.trigger(event);
                    }
                }
                return;
            }
        }

    }

}