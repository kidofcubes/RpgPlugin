package io.github.kidofcubes;

import com.google.gson.Gson;
import io.github.kidofcubes.managers.EntityManager;
import io.github.kidofcubes.managers.EventManager;
import io.github.kidofcubes.managers.RpgManager;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class RpgPlugin extends JavaPlugin {
    public static Logger logger;
    public static NamespacedKey key;
    public static NamespacedKey uuidKey;
    public static Gson gson;
    public static RpgPlugin plugin;

    @Override
    public void onEnable() {
        logger = getLogger();
        gson = new Gson();
        key = new NamespacedKey(this, "RpgPluginData");
        uuidKey = new NamespacedKey(this, "RpgPluginUUID");
        plugin = this;


        RpgManager.init();
        getServer().getPluginManager().registerEvents(new EntityManager(), plugin);
        getServer().getPluginManager().registerEvents(new EventManager(), plugin);
        getServer().getPluginManager().registerEvents(new RpgManager(), plugin);

    }

    @Override
    public void onDisable() {
        RpgManager.close();
    }
}
