package io.github.KidOfCubes.Commands;

import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgItem;
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
                switch (args[0]){
                    case "add":
                        try {
                            Class<? extends Stat> stat = Class.forName("io.github.KidOfCubes.Stats." + args[1]).asSubclass(Stat.class);

                            if(isEmpty(livingEntity.getEquipment().getItemInMainHand())){
                                RpgEntity rpgEntity = EntityManager.getRpgEntity(livingEntity);
                                Stat realStat = (Stat) stat.getConstructors()[0].newInstance(Integer.parseInt(args[2]), rpgEntity);
                                rpgEntity.addStat(realStat);
                                livingEntity.sendMessage("ADDED A "+realStat.getName()+" "+realStat.level);
                            }else{
                                RpgItem rpgItem = new RpgItem(livingEntity.getEquipment().getItemInMainHand());
                                Stat realStat = (Stat) stat.getConstructors()[0].newInstance(Integer.parseInt(args[2]), rpgItem);
                                rpgItem.addStat(realStat);
                                livingEntity.sendMessage("ADDED A "+realStat.getName()+" "+realStat.level);
                                livingEntity.getEquipment().setItemInMainHand(rpgItem.toItemStack());
                            }
                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        break;


                    case "remove":

                        if(isEmpty(livingEntity.getEquipment().getItemInMainHand())){
                            RpgEntity rpgEntity = EntityManager.getRpgEntity(livingEntity);

                            List<Stat> stats = rpgEntity.getStats();
                            for (int i = stats.size()-1; i >= 0; i--) {
                                if(stats.get(i).getName().equalsIgnoreCase(args[1])){
                                    if(args[2]=="-1"||Integer.parseInt(args[2])==stats.get(i).level){
                                        livingEntity.sendMessage("REMOVED A "+stats.get(i).getName()+" "+stats.get(i).level);
                                        stats.remove(i);
                                    }
                                }
                            }

                        }else{
                            RpgItem rpgItem = new RpgItem(livingEntity.getEquipment().getItemInMainHand());

                            List<Stat> stats = rpgItem.getStats();
                            for (int i = stats.size()-1; i >= 0; i--) {
                                if(stats.get(i).getName().equalsIgnoreCase(args[1])){
                                    if(args[2]=="-1"||Integer.parseInt(args[2])==stats.get(i).level){
                                        livingEntity.sendMessage("REMOVED A "+stats.get(i).getName()+" "+stats.get(i).level);
                                        stats.remove(i);
                                    }
                                }
                            }
                        }
                        break;

                    default: //LIST
                        if(isEmpty(livingEntity.getEquipment().getItemInMainHand())){
                            RpgEntity rpgEntity = EntityManager.getRpgEntity(livingEntity);

                            List<Stat> stats = rpgEntity.getEffectiveStats();
                            for(Stat stat : stats){
                                livingEntity.sendMessage("YOU HAVE A "+stat.getName()+" "+stat.level);
                            }

                        }else{
                            RpgItem rpgItem = new RpgItem(livingEntity.getEquipment().getItemInMainHand());

                            List<Stat> stats = rpgItem.getStats();
                            for(Stat stat : stats){
                                livingEntity.sendMessage("YOU HAVE A "+stat.getName()+" "+stat.level);
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
