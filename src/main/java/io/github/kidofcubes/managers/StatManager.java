package io.github.kidofcubes.managers;

import io.github.kidofcubes.Stat;
import io.github.kidofcubes.types.StatRegisteredListener;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.jetbrains.annotations.NotNull;

import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatManager implements Listener {

    private static final List<Class<? extends Stat>> registeredStats = new ArrayList<>();

    public static void register(Stat stat, List<Class<? extends Event>> listenEvents) {
        if (!registeredStats.contains(stat.getClass())) {
            for (Class<? extends Event> listenEvent : listenEvents) {
                try {
                    Method method = getRegistrationClass(listenEvent).getDeclaredMethod("getHandlerList");
                    method.setAccessible(true);
                    HandlerList handlerList = ((HandlerList) method.invoke(null));
                    handlerList.register(new StatRegisteredListener(stat, listenEvents));


                } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InvalidClassException e) {
                    e.printStackTrace();
                }
            }


            registeredStats.add(stat.getClass());
        }
    }

    //todo unimplemented (real)
    public static void unregister(Stat stat) {

    }

    public static List<Class<? extends Stat>> getRegisteredStats() {
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