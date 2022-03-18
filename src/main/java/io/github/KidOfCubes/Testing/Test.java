package io.github.KidOfCubes.Testing;

import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgItem;
import io.github.KidOfCubes.Stats.Sharpness;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

import static io.github.KidOfCubes.Managers.EntityManager.getRpgEntity;

public class Test implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof LivingEntity livingEntity){
            RpgEntity rpgEntity = getRpgEntity(livingEntity);

            //rpgEntity.addStat(new Sharpness(10, rpgEntity));
            RpgItem rpgItem = new RpgItem(livingEntity.getEquipment().getItemInMainHand());
            rpgItem.addStat(new Sharpness(10,rpgItem));
            livingEntity.getEquipment().setItemInOffHand(rpgItem.toItemStack());
        }
        return true;
    }
}
