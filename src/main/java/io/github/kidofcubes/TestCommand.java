package io.github.kidofcubes;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
//import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class TestCommand  implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        System.out.println("my classloader is "+TestCommand.class.getClassLoader());
        System.out.println("itemstack thing is "+(new ItemStack(Material.DIAMOND_AXE)).getMaxStackSize());
        System.out.println("itemstack instanc eof rpgitem? "+(new ItemStack(Material.DIAMOND_AXE) instanceof RpgItem));
        return true;
    }
}
