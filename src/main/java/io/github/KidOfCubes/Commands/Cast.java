package io.github.KidOfCubes.Commands;

import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.Stat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

public class Cast implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof LivingEntity livingEntity) {
            if(args.length>0) {
                RpgEntity rpgEntity = EntityManager.getRpgEntity(livingEntity);
                RpgActivateStatEvent event = rpgEntity.getActivateStatEvent(args[0]);
                if(args.length>1){
                    event.target(rpgEntity.currentTarget());
                }
                event.callEvent();
                return true;
            }
            return true;
        }else{
            sender.sendMessage("Can only be ran by a livingentity");
            return false;
        }
    }
}
