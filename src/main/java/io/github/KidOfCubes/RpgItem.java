package io.github.KidOfCubes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static io.github.KidOfCubes.ExtraFunctions.isEmpty;
import static io.github.KidOfCubes.ExtraFunctions.toRoman;
import static io.github.KidOfCubes.RpgPlugin.gson;
import static io.github.KidOfCubes.RpgPlugin.key;

public class RpgItem extends RpgElement{
    public ItemStack item;
    public RpgItem(ItemStack item){
        this.item = item;
        if(!isEmpty(item)) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String data = itemMeta
                        .getPersistentDataContainer()
                        .get(key, PersistentDataType.STRING);
                loadFromJson(data);

            }
        }else{
            throw new IllegalArgumentException("tried to make a rpg item out of air");
        }
    }
    public ItemStack toItemStack(){
        ItemStack itemStack = new ItemStack(item);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key,PersistentDataType.STRING,toJson());

        List<Component> lore = new ArrayList<>();
        for (String tempStat : getStats()){
            lore.add(Component.text(tempStat.getClass().getSimpleName()+" "+toRoman(/*tempStat.level*/1)).color(TextColor.color(0,255,0)));
        }
        itemMeta.lore(lore);




        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
    static class RpgItemJsonContainer{
        public String name;
        public int level;
        public String[] stats;
        public Integer[] statlevels;
    }

}
