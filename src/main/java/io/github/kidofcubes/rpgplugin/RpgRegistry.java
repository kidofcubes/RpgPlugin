package io.github.kidofcubes.rpgplugin;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.Plugin;
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

    private static Plugin instance;

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
    private static final Map<NamespacedKey,EnumMap<Stat.StatModifierType,NamespacedKey[]>> registeredStatModifiers = new HashMap<>(); //somebody once told me that arrays were pretty fast
    private static final Map<NamespacedKey,Stat> registeredStatInstances = new HashMap<>();

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
//    private static final NamespacedKey[] emptyArray = new NamespacedKey[0];
    @Nullable
    public static NamespacedKey[] getStatModifiers(NamespacedKey key, Stat.StatModifierType type){
        EnumMap<Stat.StatModifierType,NamespacedKey[]> map = registeredStatModifiers.get(key);
        return map==null ? null : map.getOrDefault(type,null);
    }

    static Stat getStatInstance(NamespacedKey key){
        return Objects.requireNonNull(registeredStatInstances.get(key),"Couldn't get the instance for stat "+key);
    }


    /**
     * Registers a stat to listen for events
     * @param listenEvents A list of events it will listen for
     */
    public static <T extends Stat> void register(T stat, Supplier<T> supplier, List<Class<? extends Event>> listenEvents, Plugin plugin) {
        NamespacedKey key = stat.getIdentifier();
        if (!registeredStats.containsKey(key)) {
//            if(stat instanceof TimedStat timedStat){
//                timedStats.add(timedStat);
//                timedStats.sort(Comparator.comparing(Stat::priority));
//            }

//            instance.getServer().getPluginManager().registerEvents(stat,instance);


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
            registeredStatInstances.put(key,stat);
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
//        for (Map.Entry<Class<? extends Event>, RegisteredStatListener> entry: registeredStatEvents.get(key).entrySet()) {
//            try {
//                Method method = getRegistrationClass(entry.getKey()).getDeclaredMethod("getHandlerList");
//                method.setAccessible(true);
//                HandlerList handlerList = ((HandlerList) method.invoke(null));
//                handlerList.unregister(entry.getValue());
//
//
//            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//        }
        HandlerList.unregisterAll(registeredStatInstances.get(key));
        registeredStats.remove(key);
        registeredStatInstances.remove(key);

    }


    static class RegisteredStatListener extends RegisteredListener {

        Stat stat;
        Set<Class<? extends Event>> listenEvents;

        public RegisteredStatListener(Stat stat, Set<Class<? extends Event>> listenEvents) throws InvalidClassException {
            super(stat, null, stat.priority(),instance, false);
            this.listenEvents = listenEvents;
            this.stat = stat;
        }


        @Override
        public void callEvent(@NotNull Event event) {

            //????????
            for (Class<? extends Event> listenEvent : listenEvents) {
                if (listenEvent.isAssignableFrom(event.getClass())) {
                    stat.passEvent(event);
                    return;
                }
            }

        }

    }

}
