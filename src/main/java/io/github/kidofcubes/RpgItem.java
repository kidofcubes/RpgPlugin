package io.github.kidofcubes;

import io.github.kidofcubes.managers.RpgManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

import static io.github.kidofcubes.ExtraFunctions.isEmpty;
import static io.github.kidofcubes.RpgPlugin.key;
import static io.github.kidofcubes.RpgPlugin.uuidKey;

public class RpgItem extends RpgObject {
    public ItemStack item;

    /**
     * Makes a new item (pls use RpgManager.getItem instead of this)
     * @param item
     */
    public RpgItem(ItemStack item) {
        this.item = item;
        if (!isEmpty(item)) {


            ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta.getPersistentDataContainer().has(uuidKey, PersistentDataType.STRING)) {
                setUUID(UUID.fromString(itemMeta.getPersistentDataContainer().get(uuidKey,PersistentDataType.STRING)));
                if (itemMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    String data = itemMeta
                            .getPersistentDataContainer()
                            .get(key, PersistentDataType.STRING);
                    loadFromJson(data);
                }
            }else{
                setUUID(UUID.randomUUID());
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
    public String getName() {
        return LegacyComponentSerializer.legacySection().serialize(item.displayName());
    }

    @Override
    public void save() {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, toJson());
        itemMeta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING, getUUID().toString());


        item.setItemMeta(itemMeta);
    }

}
