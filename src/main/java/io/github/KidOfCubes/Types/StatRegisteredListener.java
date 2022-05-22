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
    private RegisteredListener registeredListener;
    Stat stat;
    Class<? extends Event> type;
    private MethodHandle method;
    public StatRegisteredListener(RegisteredListener registeredListener, Stat stat) throws InvalidClassException {
        //Filling in bogus info because it's required
        super(null, null, null, null, false);
        this.stat = stat;
        this.registeredListener = registeredListener;

        //code copied from java plugin loader
        Method[] publicMethods = stat.getClass().getMethods();
        Method[] privateMethods = stat.getClass().getDeclaredMethods();


        Set<Method> methods = new HashSet<Method>(publicMethods.length + privateMethods.length, 1.0f);
        methods.addAll(Arrays.asList(publicMethods));
        methods.addAll(Arrays.asList(privateMethods));
        for (final Method method : methods) {
            final StatHandler eh = method.getAnnotation(StatHandler.class);
            if (eh == null) continue;
            // Do not register bridge or synthetic methods to avoid event duplication
            // Fixes SPIGOT-893
            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }
            final Class<?> checkClass;
            if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                logger.severe(RpgPlugin.plugin.getDescription().getFullName() + " attempted to register an invalid StatHandler method signature \"" + method.toGenericString() + "\" in " + stat.getClass());
                continue;
            }
            type = checkClass.asSubclass(Event.class);
            method.setAccessible(true);

            //this.method = MethodHandles.Lookup;
            break;
        }
        MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
        MethodType mt = MethodType.methodType(Void.class, Event.class);

        try {
            method = publicLookup.findStatic(stat.getClass(), "run", mt);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if(this.method==null){
            throw new InvalidClassException("invalid stat");
        }
        logger.info("registed listener");
    }

    @Override
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
    }

    @Override
    public void callEvent(@NotNull Event event) throws EventException //DOUBLE-TRIPLE-(42 ms first time wtf) SPEED RN (this call event takes 0.0041 - 0.0024 ms)(calling the event) (orig takes 0.13 ms ????????)
    {

        RpgElement toCheck = stat.elementToStatCheck(event);
        long startTime = System.nanoTime();
        if (toCheck!=null&&toCheck.hasStat(stat.getName())){
            //logger.info("checking stat took "+((System.nanoTime() - startTime)/1000000.0));
            //registeredListener.callEvent(event);
            stat.trigger(event);
            try {
                logger.info("invoking the method");
                method.invokeExact(stat, type.cast(event));
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
        logger.info("doing stat took "+((System.nanoTime() - startTime)/1000000.0));

    }

    @Override
    public boolean isIgnoringCancelled()
    {
        return registeredListener.isIgnoringCancelled();
    }
}