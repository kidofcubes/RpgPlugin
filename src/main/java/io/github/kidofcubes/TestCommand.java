package io.github.kidofcubes;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
//import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemFactory;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.inventory.ItemStack;

public class TestCommand  implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        System.out.println("my classloader is "+TestCommand.class.getClassLoader());
        System.out.println("itemstack thing is "+(new ItemStack(Material.DIAMOND_AXE)).getMaxStackSize());
        System.out.println("itemstack instanc eof rpgitem? "+(new ItemStack(Material.DIAMOND_AXE) instanceof RpgItem));
        if(new ItemStack(Material.DIAMOND_AXE) instanceof RpgItem rpgItem){

        }
        System.out.println("itemstack instanc eof TestInterface? "+(new ItemStack(Material.DIAMOND_AXE) instanceof TestInterface));
        ItemStack newItemStack = new ItemStack(Material.DIAMOND_AXE);
        System.out.println("before it was "+newItemStack.hashCode());
        if(newItemStack instanceof TestInterface rpgItem){
            System.out.println("uhh "+rpgItem.getAsJSON());
            System.out.println("the class was "+rpgItem.getClass());
            System.out.println("uhh0 "+rpgItem.getAsJSON());
            rpgItem.addTest("among us");
            System.out.println("uhh1 "+rpgItem.getAsJSON());
            rpgItem.addTest("among us2");
            System.out.println("uhh2 "+rpgItem.getAsJSON());
            System.out.println("and uhjhfdkajdfhkjlasd "+ ((CraftPersistentDataContainer)newItemStack.getItemMeta().getPersistentDataContainer()).toTagCompound().getAsString());
            ItemStack newnewItemStack = newItemStack.clone();
            if(newnewItemStack instanceof TestInterface newrpgItem){
                System.out.println("IT IS STILL RPG ITEM");
                System.out.println("AND UHH "+newnewItemStack.getItemMeta());
                System.out.println("AND THE NEW RPG ITEM HAS "+newrpgItem.getAsJSON());
            }
            System.out.println("and now we want "+ newItemStack.clone());
        }
        System.out.println("NEW TES THKJADH KLFJAD HFILKA "+CraftItemFactory.instance().getItemMeta(Material.SAND).getAsString());
        System.out.println("WWHAHKADF "+ CraftItemStack.asNMSCopy(new ItemStack(Material.SAND)).toString());
        System.out.println("WWHAHKADF "+ CraftItemStack.asNMSCopy(new ItemStack(Material.SAND)).copy());
        System.out.println("AND UHHHH "+new ItemStack(Material.SAND).getItemMeta());



        return true;
    }
}
