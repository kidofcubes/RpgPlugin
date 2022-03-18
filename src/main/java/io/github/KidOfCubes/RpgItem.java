package io.github.KidOfCubes;

import io.github.KidOfCubes.Stats.Sharpness;
import io.github.KidOfCubes.Types.StatTriggerType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.KidOfCubes.RpgPlugin.gson;
import static io.github.KidOfCubes.RpgPlugin.logger;
import static io.github.KidOfCubes.RpgPlugin.key;

public class RpgItem extends RpgElement{
    public ItemStack item;
    public RpgItem(ItemStack item){
        this.item = item;
        if(!item.getType().isAir()) {
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
                        logger.info("uhhh the stat is  "+realStat.getClass());
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
        for (List<Stat> templist: stats.values()){
            for (Stat tempStat : templist){
                statsList.add(tempStat.name);
                levelsList.add(tempStat.level);
            }
        }
        container.stats = statsList.toArray(new String[0]);
        container.statlevels = levelsList.toArray(new Integer[0]);
        itemMeta.getPersistentDataContainer().set(key,PersistentDataType.STRING,gson.toJson(container));
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
