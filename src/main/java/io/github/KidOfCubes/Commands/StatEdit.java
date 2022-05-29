package io.github.KidOfCubes.Commands;

import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.Managers.RpgManager;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgItem;
import io.github.KidOfCubes.RpgPlugin;
import io.github.KidOfCubes.Stat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static io.github.KidOfCubes.ExtraFunctions.isEmpty;

public class StatEdit implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof LivingEntity livingEntity) {
            if(args.length>=3) { //stat add sharpness 5
                RpgPlugin.logger.info("args0 is "+args[0]);
                switch (args[0]){
                    case "add":

                        if(isEmpty(livingEntity.getEquipment().getItemInMainHand())){
                            RpgEntity rpgEntity = RpgManager.getRpgEntity(livingEntity);
                            //Stat realStat = Stat.fromText(args[1],Integer.parseInt(args[2]));
                            String name = Stat.fromText(args[1]).getName();
                            rpgEntity.addStat(name,Integer.parseInt(args[2]));
                            livingEntity.sendMessage("ADDED A "+name+" "/*+realStat.level*/);
                        }else{
                            RpgItem rpgItem = RpgManager.getItem(livingEntity.getEquipment().getItemInMainHand());
                            //Stat realStat = Stat.fromText(args[1],Integer.parseInt(args[2]));
                            String name = Stat.fromText(args[1]).getName();
                            rpgItem.addStat(name,Integer.parseInt(args[2]));
                            livingEntity.sendMessage("ADDED A "+name+" "/*+realStat.level*/);
                            livingEntity.getEquipment().setItemInMainHand(rpgItem.toItemStack());
                        }
                        break;


                    case "remove":

                        if(isEmpty(livingEntity.getEquipment().getItemInMainHand())){
                            RpgEntity rpgEntity = RpgManager.getRpgEntity(livingEntity);
                            rpgEntity.removeStat(args[1]);

                        }else{
                            RpgItem rpgItem = RpgManager.getItem(livingEntity.getEquipment().getItemInMainHand());
                            rpgItem.removeStat(args[1]);
                        }
                        break;

                    default: //LIST
                        if(isEmpty(livingEntity.getEquipment().getItemInMainHand())){
                            RpgEntity rpgEntity = RpgManager.getRpgEntity(livingEntity);

                            Map<String,Integer> stats = rpgEntity.getEffectiveStats();
                            for(String stat : stats.keySet()){
                                livingEntity.sendMessage("YOU HAVE A "+stat+" "/*+stat.level*/);
                            }

                        }else{
                            RpgItem rpgItem = RpgManager.getItem(livingEntity.getEquipment().getItemInMainHand());

                            Map<String,Integer> stats = rpgItem.getStats();
                            for(String stat : stats.keySet()){
                                livingEntity.sendMessage("YOU HAVE A "+stat+" "/*+stat.level*/);
                            }
                        }
                        break;
                }
                return true;
            }else{
                return false;
            }
        }else{
            sender.sendMessage("Can only be ran by a livingentity");
        }
        return false;
    }
}
