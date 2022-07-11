package io.github.kidofcubes.types;

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


    @Override //i dunno the speed
    public void callEvent(@NotNull Event event)  //DOUBLE-TRIPLE-(42 ms first time wtf) SPEED RN (this call event takes 0.0041 - 0.0024 ms)(calling the event) (orig takes 0.13 ms ????????)
    {
        if(event instanceof ActivateStats activateStats){
            Stat statToRun = activateStats.getActivationStats().getOrDefault(stat.getClass(),null);
            if(statToRun!=null){
                System.out.println("MY ACTIVATION STAT WAS "+statToRun.getName()+" TO RUN FOR "+event.getEventName());
                statToRun.run(event);
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