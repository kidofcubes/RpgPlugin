package io.github.KidOfCubes.Managers;

import io.github.KidOfCubes.Events.RpgEntityDamageEvent;
import io.github.KidOfCubes.Events.RpgEntityHealEvent;
import io.github.KidOfCubes.Events.RpgEntityHealthChangeEvent;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgPlugin;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Stats.Sharpness;
import io.github.KidOfCubes.Types.StatHandler;
import io.github.KidOfCubes.Types.StatRegisteredListener;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class StatManager implements Listener {

    private static final List<Class<? extends Stat>> registeredStats = new ArrayList<>();

    public static void register(Stat stat, List<Class<? extends Event>> listenEvents){
        if(!registeredStats.contains(stat.getClass())) {
            for (Class<? extends Event> listenEvent : listenEvents) {
                //if(listenEvent.getClass().asSubclass(Event.class))
                try {
                    logger.info("SIGNING UP A "+stat.getName()+" FOR "+listenEvent.getSimpleName());
                    Method method = getRegistrationClass(listenEvent).getDeclaredMethod("getHandlerList");
                    method.setAccessible(true);
                    HandlerList handlerList = ((HandlerList) method.invoke(null));
                    handlerList.register(new StatRegisteredListener(stat,listenEvents));
                    logger.info("there are "+handlerList.getRegisteredListeners().length+" listeners listening for stuf");
                    for (RegisteredListener listener: handlerList.getRegisteredListeners()) {
                        logger.info("theres a listener "+listener.getClass().getName());
                    }


                } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InvalidClassException e) {
                    e.printStackTrace();
                }
            }


            registeredStats.add(stat.getClass());
            for (RegisteredListener listener: (RpgEntityHealthChangeEvent.getHandlerList()).getRegisteredListeners()) {
                logger.info("theres a listener HEAL EVENT "+listener.getClass().getName());
            }
        }
    }
    public static void unregister(Stat stat){

    }
    public static List<Class<? extends Stat>> getRegisteredStats(){
        return Collections.unmodifiableList(registeredStats);
    }

    @NotNull
    private static Class<? extends Event> getRegistrationClass(@NotNull Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlerList method required!");
            }
        }
    }

}
