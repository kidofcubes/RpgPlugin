package io.github.KidOfCubes.Types;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgPlugin;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Stats.Sharpness;
import org.bukkit.Warning;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.jetbrains.annotations.NotNull;

import java.io.InvalidClassException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class StatRegisteredListener extends RegisteredListener
{
    //private final EventExecutor executor;
    //private RegisteredListener registeredListener;
    Stat stat;
    List<Class<? extends Event>> listenEvents;

    private MethodHandle method;
    public StatRegisteredListener(Stat stat,List<Class<? extends Event>> listenEvents) throws InvalidClassException {
        //Filling in bogus info because it's required
        //(EventExecutor.create(method, eventClass), plugin, method, eventClass)
        super(stat, null, EventPriority.NORMAL, RpgPlugin.plugin, false);
        this.listenEvents = listenEvents;
        this.stat = stat;
        logger.info("registed listener (REAL)");
    }

/*    @Override
    public Listener getListener()
    {
        return registeredListener.getListener();
    }

    @Override
    public Plugin getPlugin()
    {
        return registeredListener.getPlugin();
    }

    @Override
    public EventPriority getPriority()
    {
        return registeredListener.getPriority();
    }*/

    @Override
    public void callEvent(@NotNull Event event)  //DOUBLE-TRIPLE-(42 ms first time wtf) SPEED RN (this call event takes 0.0041 - 0.0024 ms)(calling the event) (orig takes 0.13 ms ????????)
    {

        //logger.info("the event aais a instance of "+event.getEventName());

        long startTime = System.nanoTime();
        for (int i = 0; i < listenEvents.size(); i++) {
            if(event.getClass().isInstance(listenEvents.get(i))) {
                //logger.info("IT WORKKED ON "+listenEvents[i].getName());
                RpgElement toCheck = stat.elementToStatCheck(event);
                if (toCheck != null) {
                    if (toCheck.hasStat(stat.getName())) {
                        logger.info("checking stat took " + ((System.nanoTime() - startTime) / 1000000.0));
                        //registeredListener.callEvent(event);
                        stat.trigger(event);
                    }
                }
                logger.info("doing stat took "+((System.nanoTime() - startTime)/1000000.0));
                return;
            }
        }

    }

/*    @Override
    public boolean isIgnoringCancelled()
    {
        return registeredListener.isIgnoringCancelled();
    }*/
}