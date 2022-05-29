package io.github.kidofcubes.types;

import io.github.kidofcubes.events.RpgActivateStatEvent;
import io.github.kidofcubes.RpgObject;
import io.github.kidofcubes.RpgPlugin;
import io.github.kidofcubes.Stat;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.io.InvalidClassException;
import java.util.List;

import static io.github.kidofcubes.RpgPlugin.logger;

public class StatRegisteredListener extends RegisteredListener {

    Stat stat;
    List<Class<? extends Event>> listenEvents;

    public StatRegisteredListener(Stat stat, List<Class<? extends Event>> listenEvents) throws InvalidClassException {
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
            if (listenEvents.get(i).isAssignableFrom(event.getClass())) {
                RpgObject toCheck = stat.elementToStatCheck(event);
                if (toCheck != null) {
                    int level = toCheck.getEffectiveStats().getOrDefault(stat.getName(), 0);
                    if (level != 0) {

                        if (event instanceof RpgActivateStatEvent rpgActivateStatEvent) {
                            logger.info("GOT A ACTIVATE EVENT ON " + stat.getName() + " " + rpgActivateStatEvent.getTriggerStats().size());
                            if (rpgActivateStatEvent.getTriggerStats().contains(stat.getName()))
                                stat.trigger(event, level);
                        } else {
                            stat.trigger(event, level);
                        }
                    }
                }
                return;
            }
        }

    }

}