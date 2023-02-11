package io.github.kidofcubes.rpgplugin;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class RpgRegistry { //why?
    private final static Map<Class<? extends RpgObject>,Map<NamespacedKey, Function<?,? extends RpgObject>>> typeConstructors = new HashMap<>();

    public static boolean containsTypeConstructor(Class<? extends RpgObject> clazz, NamespacedKey type){
        if(type==null){
            return false;
        }
        if(!typeConstructors.containsKey(clazz)){
            return false;
        }
        return typeConstructors.get(clazz).containsKey(type);
    }

    @SuppressWarnings("unchecked")
    public static <T,Z extends RpgObject> Function<T, Z> getTypeConstructor(Class<? extends RpgObject> clazz, NamespacedKey type){
        return (Function<T, Z>) Objects.requireNonNull(typeConstructors.get(clazz).get(type), "Specified type doesn't exist in map");
    }
    public static <T,Z extends RpgObject> void registerTypeConstructor(Class<? extends RpgObject> clazz, NamespacedKey type, Function<T,Z> constructor){
        typeConstructors.putIfAbsent(clazz,new HashMap<>());
        typeConstructors.get(clazz).put(type,constructor);
    }



    private static final Map<NamespacedKey,Supplier<? extends Stat>> registeredStats = new HashMap<>();
    private static final Map<NamespacedKey,Map<Class<? extends Event>, RegisteredStatListener>> registeredStatEvents = new HashMap<>();

    //do this l8r
//    private static final List<TimedStat> timedStats = new ArrayList<>();
//    public static void init(){
//        BukkitScheduler scheduler = Bukkit.getScheduler();
//        scheduler.runTaskTimer(GlobalVariables.plugin, () -> {
//            if(timedStats.size()>0){ //static is static for all timedstats eee
//                timedStats.get(0).trigger(null);
//            }
//        }, 0, 1);
//
//
//    }


    /**
     * Registers a stat to listen for events
     * @param listenEvents A list of events it will listen for
     */
    public static <T extends Stat> void register(T stat, Supplier<T> supplier, List<Class<? extends Event>> listenEvents) {
        register(stat.getIdentifier(),stat,supplier,listenEvents);
    }
    /**
     * Registers a stat to listen for events
     * @param listenEvents A list of events it will listen for
     */
    private static <T extends Stat> void register(NamespacedKey key, T stat, Supplier<T> supplier, List<Class<? extends Event>> listenEvents) {
        if (!registeredStats.containsKey(key)) {
//            if(stat instanceof TimedStat timedStat){
//                timedStats.add(timedStat);
//                timedStats.sort(Comparator.comparing(Stat::priority));
//            }

            Map<Class<? extends Event>, RegisteredStatListener> listeners = new HashMap<>();
            Set<Class<? extends Event>> events = new HashSet<>(listenEvents);

            for (Class<? extends Event> event : listenEvents) {
                try {
                    Method method = getRegistrationClass(event).getDeclaredMethod("getHandlerList");
                    method.setAccessible(true);
                    HandlerList handlerList = ((HandlerList) method.invoke(null));
                    RegisteredStatListener listener = new RegisteredStatListener(stat,events);
                    handlerList.register(listener);
                    listeners.put(event,listener);


                } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException |
                         InvalidClassException e) {
                    e.printStackTrace();
                }
            }



            registeredStats.put(key, supplier);
            registeredStatEvents.put(key, listeners);
        }
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

    public static boolean isRegisteredStat(@Nullable NamespacedKey key){
        return registeredStats.containsKey(key);
    }

    @NotNull
    public static Stat initStat(@NotNull NamespacedKey statKey) {
        return Objects.requireNonNull(registeredStats.get(statKey),"No stat registered under "+statKey.asString()).get();
    }

    /**
     * Unregisters a stat (unimplemented) (real)
     * @param key The stat to be unregistered
     */
    public static void unregisterStat(NamespacedKey key) {
        if(!registeredStats.containsKey(key)){
            return;
        }
        for (Map.Entry<Class<? extends Event>, RegisteredStatListener> entry: registeredStatEvents.get(key).entrySet()) {
            try {
                Method method = getRegistrationClass(entry.getKey()).getDeclaredMethod("getHandlerList");
                method.setAccessible(true);
                HandlerList handlerList = ((HandlerList) method.invoke(null));
                handlerList.unregister(entry.getValue());


            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        registeredStats.remove(key);
        registeredStatEvents.remove(key);

    }

    static class RegisteredStatListener extends RegisteredListener {

        Stat stat;
        Set<Class<? extends Event>> listenEvents;

        public RegisteredStatListener(Stat stat, Set<Class<? extends Event>> listenEvents) throws InvalidClassException {
            super(stat, null, stat.priority(),null, false);
            this.listenEvents = listenEvents;
            this.stat = stat;
        }


        @Override
        public void callEvent(@NotNull Event event) {

            //????????
            for (Class<? extends Event> listenEvent : listenEvents) {
                if (listenEvent.isAssignableFrom(event.getClass())) {
                    stat.trigger(event);
                    return;
                }
            }

        }

    }

}
