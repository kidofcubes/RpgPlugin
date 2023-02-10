package io.github.kidofcubes;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import static io.github.kidofcubes.RpgRegistry.registerTypeConstructor;

public class RpgPlugin extends JavaPlugin {


    public RpgPlugin() {
        super();
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
