package io.github.kidofcubes.rpgplugin;

import io.github.kidofcubes.rpgplugin.world.RegionSpawning;
import org.bukkit.Bukkit;
import org.bukkit.block.TileState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

import static io.github.kidofcubes.rpgplugin.RpgRegistry.registerTypeConstructor;


public class RpgPlugin extends JavaPlugin {


    public RpgPlugin() {
        super();
        try {
            Field field = RpgRegistry.class.getDeclaredField("pluginInstance");
            field.setAccessible(true);
            field.set(null,this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {
        registerTypeConstructor(RpgEntity.class,RPG.DEFAULT_TYPE_KEY, RpgEntityImpl::new);
        registerTypeConstructor(RpgItem.class,RPG.DEFAULT_TYPE_KEY,RpgItemImpl::new);
//        registerTypeConstructor(RpgTile.class,RpgObject.defaultTypeKey,(TileState thing) -> new RpgTileImpl(thing).loadTag(RpgTileImpl.getHolder(thing)));

        Bukkit.getScheduler().runTaskLater(this, () -> { //?????????
            saveDefaultConfig();
            FileConfiguration config = this.getConfig();
            if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
                System.out.println("THERE IS WORLDGUARD");
                RegionSpawning regionSpawning = new RegionSpawning(this);
                getServer().getPluginManager().registerEvents(regionSpawning,this);
            }
        }, 0);





    }



    @Override
    public void onDisable() {}
}
