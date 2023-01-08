package io.github.kidofcubes;

import com.google.gson.Gson;
import io.github.kidofcubes.managers.StatManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class RpgPlugin extends JavaPlugin {
    private static Logger logger;
    public static NamespacedKey key;
    public static NamespacedKey uuidKey;
    protected static Gson gson;
    public static RpgPlugin plugin;

    @Override
    public void onEnable() {
        logger = getLogger();
        gson = new Gson();
        key = new NamespacedKey(this, "RpgPluginData");
        uuidKey = new NamespacedKey(this, "RpgPluginUUID");
        plugin = this;


        Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, () -> {
            getServer().getPluginManager().registerEvents(new RpgInjector(), plugin);
//            getServer().getPluginManager().registerEvents(new EntityManager(), plugin);
//            getServer().getPluginManager().registerEvents(new EventManager(), plugin);
//            getServer().getPluginManager().registerEvents(new RpgManager(), plugin);
            StatManager.init();
            saveDefaultConfig();
            FileConfiguration config = this.getConfig();
            try {
                ManaDisplayMethod = ManaDisplayType.valueOf(config.getString("manaDisplayMethod"));
            } catch (Exception ex) {
                ManaDisplayMethod = ManaDisplayType.none;
                logger.info("manaDisplayMethod was not one of: "+ Arrays.toString(ManaDisplayType.values()));
            }

//            StatManager.register(new DamageModifier(), List.of(RpgEntityDamageEvent.class));



//            RpgManager.init();
//            EntityManager.init();
        }, 0);


    }
    public enum ManaDisplayType{
        none,
        level
    }

    public static ManaDisplayType ManaDisplayMethod = ManaDisplayType.none;

    @Override
    public void onDisable() {
//        RpgManager.close();
    }
}
