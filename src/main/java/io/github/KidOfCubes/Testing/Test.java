package io.github.KidOfCubes.Testing;

import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgItem;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Stats.Sharpness;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

import static io.github.KidOfCubes.Managers.EntityManager.RpgEntities;
import static io.github.KidOfCubes.Managers.EntityManager.getRpgEntity;
import static io.github.KidOfCubes.RpgPlugin.logger;

public class Test implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof LivingEntity livingEntity){
            new EntityDamageByBlockEvent(livingEntity.getTargetBlock(100),livingEntity, EntityDamageEvent.DamageCause.CUSTOM,1).callEvent();




/*            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 100000; i++) {
                rpgItem.toItemStack();
            }
            long endTime = System.currentTimeMillis();

            long duration = (endTime - startTime);
            logger.info("doing 100000 took "+duration);
            ItemStack temp = rpgItem.toItemStack();
            long startTime2 = System.currentTimeMillis();
            for (int i = 0; i < 100000; i++) {
                livingEntity.getEquipment().setItemInOffHand(temp);
            }
            long endTime2 = System.currentTimeMillis();

            long duration2 = (endTime2 - startTime2);
            logger.info("doing 100000 took "+duration2);*/
            //livingEntity.getEquipment().setItemInOffHand(rpgItem.toItemStack());
        }
        return true;
    }



}
