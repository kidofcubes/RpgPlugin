package io.github.kidofcubes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static io.github.kidofcubes.ExtraFunctions.isEmpty;
import static io.github.kidofcubes.RpgPlugin.key;
import static io.github.kidofcubes.RpgPlugin.uuidKey;

public class RpgItem extends RpgObject {
    public ItemStack item;

    public RpgItem(ItemStack item) {
        this.item = item;
        if (!isEmpty(item)) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                String data = itemMeta
                        .getPersistentDataContainer()
                        .get(key, PersistentDataType.STRING);
                loadFromJson(data);

            }
        } else {
            throw new IllegalArgumentException("Can't make a air item");
        }
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(item);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, toJson());
        itemMeta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING, getUUID().toString());


        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void save() {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, toJson());
        itemMeta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING, getUUID().toString());


        item.setItemMeta(itemMeta);
    }

}