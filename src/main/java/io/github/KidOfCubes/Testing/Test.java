package io.github.KidOfCubes.Testing;

import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgItem;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Stats.Sharpness;
import io.github.KidOfCubes.Types.StatTriggerType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

import static io.github.KidOfCubes.Managers.EntityManager.RpgEntities;
import static io.github.KidOfCubes.Managers.EntityManager.getRpgEntity;
import static io.github.KidOfCubes.RpgPlugin.logger;

public class Test implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof LivingEntity livingEntity){
            if(args.length==2) {
                RpgEntity rpgEntity = getRpgEntity(livingEntity);

                //rpgEntity.addStat(new Sharpness(10, rpgEntity));
                RpgItem rpgItem = new RpgItem(livingEntity.getEquipment().getItemInMainHand());


                Class<? extends Stat> stat = null;
                try {
                    stat = Class.forName("io.github.KidOfCubes.Stats." + args[0]).asSubclass(Stat.class);
                    Stat realStat = (Stat) stat.getConstructors()[0].newInstance(Integer.parseInt(args[1]), rpgItem);
                    rpgItem.addStat(realStat);
                    livingEntity.getEquipment().setItemInMainHand(rpgItem.toItemStack());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }else{
                if(args.length>=3){

                    RpgEntity rpgEntity = getRpgEntity(livingEntity);


                    Class<? extends Stat> stat = null;
                    try {
                        stat = Class.forName("io.github.KidOfCubes.Stats." + args[0]).asSubclass(Stat.class);
                        Stat realStat = (Stat) stat.getConstructors()[0].newInstance(Integer.parseInt(args[1]), rpgEntity);
                        rpgEntity.addStat(realStat);
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }




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
