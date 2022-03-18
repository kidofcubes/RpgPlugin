package io.github.KidOfCubes;

import com.google.gson.Gson;
import io.github.KidOfCubes.Managers.DamageManager;
import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.Testing.Test;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class RpgPlugin extends JavaPlugin {
    public static Logger logger;
    public static NamespacedKey key;
    public static Gson gson;
    @Override
    public void onEnable() {
        logger=getLogger();
        gson = new Gson();
        key = new NamespacedKey(this, "RpgPlugin");
        logger.info("RpgPlugin Starting");

        getServer().getPluginManager().registerEvents(new DamageManager(), this);
        getServer().getPluginManager().registerEvents(new EntityManager(), this);

        this.getCommand("test").setExecutor(new Test());
    }

    @Override
    public void onDisable() {
        logger.info("RpgPlugin Closing");
    }
}
