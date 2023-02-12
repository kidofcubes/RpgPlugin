package io.github.kidofcubes.rpgplugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

import static io.github.kidofcubes.rpgplugin.RpgRegistry.registerTypeConstructor;

public class RpgPlugin extends JavaPlugin {


    public RpgPlugin() {
        super();
        try {
            Field field = RpgRegistry.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null,this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {
        registerTypeConstructor(RpgEntity.class,RpgObject.defaultTypeKey,(LivingEntity thing) -> new RpgLivingEntity(thing).loadFromJson(RpgLivingEntity.getHolder(thing).getJson()));
        registerTypeConstructor(RpgItem.class,RpgObject.defaultTypeKey,(ItemStack thing) -> new RpgItemStack(thing).loadFromJson(RpgItemStack.getHolder(thing).getJson()));
        Bukkit.getScheduler().runTaskLater(this, () -> {
            saveDefaultConfig();
            FileConfiguration config = this.getConfig();
        }, 0);



    }



    @Override
    public void onDisable() {
//        RpgManager.close();
    }
}
