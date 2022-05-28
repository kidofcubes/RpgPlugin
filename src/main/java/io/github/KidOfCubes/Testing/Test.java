package io.github.KidOfCubes.Testing;

import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgItem;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Stats.Sharpness;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowman;
import org.jetbrains.annotations.NotNull;

public class Test implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof LivingEntity livingEntity){
            if(args[0].equalsIgnoreCase("0")){
                Location location = livingEntity.getLocation();
                World world = location.getWorld();
                for (int i = 0; i < 1000; i++) {
                    world.spawn(location, Snowman.class);
                }
            }
            if(args[0].equalsIgnoreCase("1")){
                Location location = livingEntity.getLocation();
                World world = location.getWorld();
                for (int i = 0; i < 1000; i++) {
                    world.spawn(location, Blaze.class);
                }
            }
        }
        return true;
    }



}
