package io.github.kidofcubes;

import io.github.kidofcubes.managers.RpgManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static io.github.kidofcubes.ExtraFunctions.isEmpty;
import static io.github.kidofcubes.RpgPlugin.key;
import static io.github.kidofcubes.RpgPlugin.uuidKey;


//note, create and destroy on whims
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
                if (itemMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    String data = itemMeta
                            .getPersistentDataContainer()
                            .get(key, PersistentDataType.STRING);
                    loadFromJson(data);
                }
            }

        } else {
            throw new IllegalArgumentException("Can't make a air item");
        }
    }


    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(item);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, toJson());
        itemMeta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING, getUUID().toString());


        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    //test


    @Override
    public void removeRpgClass(RpgClass rpgClass) {
        super.removeRpgClass(rpgClass);
        save();
    }

    @Override
    public void removeStat(String stat) {
        super.removeStat(stat);
        save();
    }

    @Override
    public void addRpgClass(RpgClass rpgClass) {
        super.addRpgClass(rpgClass);
        save();
    }

    @Override
    public void addStat(Stat stat) {
        super.addStat(stat);
        save();
    }

    @Override
    public void setUUID(UUID uuid) {
        super.setUUID(uuid);
        save();
    }

    @Override
    public void setMana(double mana) {
        super.setMana(mana);
        save();
    }

    @Override
    public void setMaxMana(double maxMana) {
        super.setMaxMana(maxMana);
        save();
    }

    @Override
    public void setParent(RpgObject parent) {
        super.setParent(parent);
        save();
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);
        save();
    }

    @NotNull
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
