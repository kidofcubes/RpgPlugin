package io.github.KidOfCubes.Managers;

import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgPlugin;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Stats.Sharpness;
import io.github.KidOfCubes.Types.StatRegisteredListener;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class StatManager implements Listener {

    public Stat[] allStats;

    public void init(){
/*        RegisteredListener registeredListener = new RegisteredListener(this, (listener, event) -> onEvent(event), EventPriority.NORMAL, RpgPlugin.plugin, false);
        for (HandlerList handler : HandlerList.getHandlerLists())
            handler.register(registeredListener);*/



        List<Stat> allStatsList = new ArrayList<>();
        allStatsList.add(new Sharpness(-1));
        PluginLoader pluginLoader = RpgPlugin.plugin.getPluginLoader();
        //PluginManager pluginManager = RpgPlugin.plugin.getServer().getPluginManager();

        for (Stat stat: allStatsList) {

            Map<Class<? extends Event>, Set<RegisteredListener>> listeners = pluginLoader.createRegisteredListeners(stat, RpgPlugin.plugin);
            for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : listeners.entrySet() ) {
                for (RegisteredListener registeredListener : entry.getValue()) {
                    try {
                        ((HandlerList)(entry.getKey().getDeclaredMethod("getHandlerList").invoke(null))).register(new StatRegisteredListener(registeredListener,stat));
                    } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InvalidClassException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEvent(Event event){
        logger.info("EVENT "+event.getEventName());

    }

}
