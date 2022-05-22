package io.github.KidOfCubes;

import com.google.gson.Gson;
import io.github.KidOfCubes.Commands.Cast;
import io.github.KidOfCubes.Commands.StatEdit;
import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.Managers.EventManager;
import io.github.KidOfCubes.Managers.ItemManager;
import io.github.KidOfCubes.Managers.StatManager;
import io.github.KidOfCubes.Testing.Test;
import io.github.KidOfCubes.Testing.TestSharpness;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class RpgPlugin extends JavaPlugin {
    public static Logger logger;
    public static NamespacedKey key;
    public static Gson gson;
    public static RpgPlugin plugin;
    @Override
    public void onEnable() {
        logger=getLogger();
        gson = new Gson();
        key = new NamespacedKey(this, "RpgPlugin");
        plugin = this;
        logger.info("RpgPlugin Starting");


        //getServer().getPluginManager().registerEvents(new ItemManager(), this);
        //getServer().getPluginManager().registerEvents(new StatManager(), this);
        StatManager statManager = new StatManager();
        statManager.init();
        EntityManager entityManager = new EntityManager();
        getServer().getPluginManager().registerEvents(entityManager, plugin);
        EventManager eventManager = new EventManager();
        eventManager.init();
        getServer().getPluginManager().registerEvents(eventManager, plugin);
        getServer().getPluginManager().registerEvents(new TestSharpness(), plugin);

        this.getCommand("statedit").setExecutor(new StatEdit());
        this.getCommand("test").setExecutor(new Test());
        this.getCommand("cast").setExecutor(new Cast());


    }

    @Override
    public void onDisable() {
        EntityManager.close();
        logger.info("RpgPlugin Closing");
    }
}
