package io.github.KidOfCubes.Commands;

import io.github.KidOfCubes.Managers.EntityManager;
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
                            RpgEntity rpgEntity = EntityManager.getRpgEntity(livingEntity);
                            //Stat realStat = Stat.fromText(args[1],Integer.parseInt(args[2]));
                            String name = Stat.fromText(args[1]).getName();
                            rpgEntity.addStat(name);
                            livingEntity.sendMessage("ADDED A "+name+" "/*+realStat.level*/);
                        }else{
                            RpgItem rpgItem = new RpgItem(livingEntity.getEquipment().getItemInMainHand());
                            //Stat realStat = Stat.fromText(args[1],Integer.parseInt(args[2]));
                            String name = Stat.fromText(args[1]).getName();
                            rpgItem.addStat(name);
                            livingEntity.sendMessage("ADDED A "+name+" "/*+realStat.level*/);
                            livingEntity.getEquipment().setItemInMainHand(rpgItem.toItemStack());
                        }
                        break;


                    case "remove":

                        if(isEmpty(livingEntity.getEquipment().getItemInMainHand())){
                            RpgEntity rpgEntity = EntityManager.getRpgEntity(livingEntity);

                            List<String> stats = rpgEntity.getStats();
                            for (int i = stats.size()-1; i >= 0; i--) {
                                if(stats.get(i).equalsIgnoreCase(args[1])){
                                    //if(args[2]=="-1"||Integer.parseInt(args[2])==stats.get(i).level){
                                        livingEntity.sendMessage("REMOVED A "+stats.get(i)+" "/*+stats.get(i).level*/);
                                        stats.remove(i);
                                    //}
                                }
                            }

                        }else{
                            RpgItem rpgItem = new RpgItem(livingEntity.getEquipment().getItemInMainHand());

                            List<String> stats = rpgItem.getStats();
                            for (int i = stats.size()-1; i >= 0; i--) {
                                if(stats.get(i).equalsIgnoreCase(args[1])){
                                    //if(args[2]=="-1"||Integer.parseInt(args[2])==stats.get(i).level){
                                        livingEntity.sendMessage("REMOVED A "+stats.get(i)+" "/*+stats.get(i).level*/);
                                        stats.remove(i);
                                    //}
                                }
                            }
                        }
                        break;

                    default: //LIST
                        if(isEmpty(livingEntity.getEquipment().getItemInMainHand())){
                            RpgEntity rpgEntity = EntityManager.getRpgEntity(livingEntity);

                            List<String> stats = rpgEntity.getEffectiveStats();
                            for(String stat : stats){
                                livingEntity.sendMessage("YOU HAVE A "+stat+" "/*+stat.level*/);
                            }

                        }else{
                            RpgItem rpgItem = new RpgItem(livingEntity.getEquipment().getItemInMainHand());

                            List<String> stats = rpgItem.getStats();
                            for(String stat : stats){
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
