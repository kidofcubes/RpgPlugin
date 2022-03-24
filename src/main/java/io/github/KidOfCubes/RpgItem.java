package io.github.KidOfCubes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
                RpgItemJsonContainer temp = RpgPlugin.gson.fromJson(data,RpgItemJsonContainer.class);
                for (int i = 0; i < temp.stats.length; i++) {
                    try {
                        Class<? extends Stat> stat = Class.forName("io.github.KidOfCubes.Stats."+temp.stats[i]).asSubclass(Stat.class);
                        Stat realStat = (Stat)stat.getConstructors()[0].newInstance(temp.statlevels[i],this);
                        addStat(realStat);
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

            }
        }else{
            throw new IllegalArgumentException("tried to make a rpg item out of air");
        }
    }
    public ItemStack toItemStack(){
        ItemStack itemStack = new ItemStack(item);
        ItemMeta itemMeta = itemStack.getItemMeta();
        RpgItemJsonContainer container = new RpgItemJsonContainer();
        container.name = name;
        container.level = level;
        List<String> statsList = new ArrayList<>();
        List<Integer> levelsList = new ArrayList<>();
        for (Stat tempStat : getAllStats()){
            statsList.add(tempStat.getClass().getSimpleName());
            levelsList.add(tempStat.level);
        }
        container.stats = statsList.toArray(new String[0]);
        container.statlevels = levelsList.toArray(new Integer[0]);
        itemMeta.getPersistentDataContainer().set(key,PersistentDataType.STRING,gson.toJson(container));

        List<Component> lore = new ArrayList<>();
        for (Stat tempStat : getAllStats()){
            lore.add(Component.text(ChatColor.GREEN+tempStat.getClass().getSimpleName()+" "+toRoman(tempStat.level)));
        }
        itemMeta.lore(lore);




        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
    class RpgItemJsonContainer{
        public String name;
        public int level;
        public String[] stats;
        public Integer[] statlevels;
    }

}
