package io.github.kidofcubes.managers;

import io.github.kidofcubes.RpgEntity;
import io.github.kidofcubes.RpgItem;
import io.github.kidofcubes.RpgObject;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.kidofcubes.RpgPlugin.uuidKey;

public class RpgManager implements Listener {
    private static final Map<UUID, RpgEntity> allEntities = new HashMap<>();
    private static final Map<UUID, RpgItem> allItems = new HashMap<>();

    public static void init() {

    }

    /**
     * Deletes all temporary entities and saves everything
     */
    public static void close() {
        cleanTempEntities();
        saveAllEntities();
        saveAllItems();
    }

    public static void cleanTempEntities() {
        for (Map.Entry<UUID, RpgEntity> entry : allEntities.entrySet()) {
            if (entry.getValue().isTemporary()) {
                entry.getValue().livingEntity.setHealth(0);
                entry.getValue().livingEntity.remove();
            }
        }
        allEntities.entrySet().removeIf(entry -> entry.getValue().isTemporary());
    }

    /**
     * Attempts to get a RpgEntity, then a RpgItem, if it can't find any then returns null
     * @param uuid UUID to search for
     * @return RpgEntity, RpgItem, or null
     */
    @Nullable
    public static RpgObject getRpgObject(UUID uuid) {
        RpgEntity returnEntity = allEntities.getOrDefault(uuid, null);
        if (returnEntity != null) {
            return returnEntity;
//            if (returnEntity.exists()) {
//                return returnEntity;
//            } else {
//                allEntities.remove(uuid);
//            }
        }
        return allItems.getOrDefault(uuid, null);

    }

    /**
     * Removes all non-existing RpgEntities, saves all RpgItems then clears the item cache
     */
    public static void garbageCollect() {
        allEntities.entrySet().removeIf(entry -> !entry.getValue().exists());
        saveAllItems();
        allItems.entrySet().clear();
    }

    /**
     * Saves all non-temporary RpgEntities into the metadata of their respective livingEntities
     */
    public static void saveAllEntities() {
        for (Map.Entry<UUID, RpgEntity> entry : allEntities.entrySet()) {
            if (entry.getValue().exists() && !entry.getValue().isTemporary()) {
                entry.getValue().save();
            }
        }
    }
    /**
     * Saves all RpgItems into the metadata of their respective itemstacks
     */
    public static void saveAllItems() {
        for (Map.Entry<UUID, RpgItem> entry : allItems.entrySet()) {
            entry.getValue().save();
        }
    }

    public static void addRpgEntity(UUID key, RpgEntity rpgEntity) {
        allEntities.put(key, rpgEntity);
    }

    /**
     * Gets a RpgEntity from a livingEntity (will create if doesnt exist yet)
     * @param livingEntity
     * @return The RpgEntity of the livingEntity
     */
    @NotNull
    public static RpgEntity getRpgEntity(LivingEntity livingEntity) {
        RpgEntity returnEntity = getRpgEntity(livingEntity.getUniqueId());
        if (returnEntity != null) {
            return returnEntity;
        } else {
            return new RpgEntity(livingEntity);
        }
    }


    /**
     * Gets a RpgEntity from an uuid
     * @param uuid UUID to search with
     * @return The found RpgEntity or null if not found
     */
    @Nullable
    public static RpgEntity getRpgEntity(UUID uuid) {
        RpgEntity returnValue = allEntities.getOrDefault(uuid, null);
        if (returnValue != null) {
            if (returnValue.exists()) {
                return returnValue;
            } else {
                allEntities.remove(uuid);
                return null;
            }
        } else {
            return null;
        }

    }

    /**
     * dunno why this exists, might be removed later
     * @param livingEntity
     * @return
     */
    public static RpgEntity makeTempRpgEntity(LivingEntity livingEntity) {
        return new RpgEntity(livingEntity, true);
    }

    /**
     * Check if there's a RpgEntity bound to an uuid
     * @param uuid
     * @return true if theres a RpgEntity that exists with this uuid
     */
    public static boolean checkEntityExists(UUID uuid) {
        RpgEntity checkEntity = allEntities.getOrDefault(uuid, null);
        if (checkEntity != null) return checkEntity.exists();
        return false;
    }

    /**
     *
     * @param itemStack
     * @return
     */
    public static RpgItem getItem(ItemStack itemStack) { //reads uuid every time we get item, maybe not the smartest way?
        if (itemStack.getItemMeta().getPersistentDataContainer().has(uuidKey, PersistentDataType.STRING)) {
            RpgItem item = allItems.getOrDefault(UUID.fromString(itemStack.getItemMeta().getPersistentDataContainer().get(uuidKey, PersistentDataType.STRING)), null);
            if (item != null) {
                return item;
            }
        }
        return new RpgItem(itemStack);
    }


    @EventHandler
    public void onEntityDeath(EntityDeathEvent entityDeathEvent) {
        allEntities.remove(entityDeathEvent.getEntity().getUniqueId());
    }
}
