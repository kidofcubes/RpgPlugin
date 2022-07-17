package io.github.kidofcubes;

import io.github.kidofcubes.ActivateStats;
import io.github.kidofcubes.RpgPlugin;
import io.github.kidofcubes.Stat;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.io.InvalidClassException;
import java.util.List;

public class StatRegisteredListener extends RegisteredListener {

    Stat stat;
    List<Class<? extends Event>> listenEvents;

    public StatRegisteredListener(Stat stat, List<Class<? extends Event>> listenEvents) throws InvalidClassException {
        super(stat, null, stat.priority(), RpgPlugin.plugin, false);
        this.listenEvents = listenEvents;
        this.stat = stat;
    }


    @Override
    public void callEvent(@NotNull Event event)  //whats performance
    {
        if(event instanceof ActivateStats activateStats){
            Stat statToRun = activateStats.getActivationStats().getOrDefault(stat.getClass(),null);
            if(statToRun!=null){
                statToRun.trigger(event);
            }
        }
        for (Class<? extends Event> listenEvent : listenEvents) {
            if (listenEvent.isAssignableFrom(event.getClass())) {
                stat.trigger(event);
                return;
            }
        }

    }

}