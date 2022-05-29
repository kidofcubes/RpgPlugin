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

    public static void close() {
        cleanTempEntities();
        saveAll();
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

    @Nullable
    public static RpgObject getRpgObject(UUID uuid) {
        RpgEntity returnEntity = allEntities.getOrDefault(uuid, null);
        if (returnEntity != null) {
            if (returnEntity.exists()) {
                return returnEntity;
            } else {
                allEntities.remove(uuid);
            }
        }
        return allItems.getOrDefault(uuid, null);

    }

    public static void garbageCollect() {
        allEntities.entrySet().removeIf(entry -> !entry.getValue().exists());
        saveAllItems();
        allItems.entrySet().clear();
    }

    private static void saveAllEntities() {
        for (Map.Entry<UUID, RpgEntity> entry : allEntities.entrySet()) {
            if (entry.getValue().exists() && !entry.getValue().isTemporary()) {
                entry.getValue().save();
            }
        }
    }

    private static void saveAllItems() {
        for (Map.Entry<UUID, RpgItem> entry : allItems.entrySet()) {
            entry.getValue().save();
        }
    }

    public static void saveAll() {
        saveAllEntities();
        saveAllItems();
    }


    public static void addRpgEntity(UUID key, RpgEntity rpgEntity) {
        allEntities.put(key, rpgEntity);
    }


    public static RpgEntity getRpgEntity(LivingEntity livingEntity) {
        RpgEntity returnEntity = getRpgEntity(livingEntity.getUniqueId());
        if (returnEntity != null) {
            return returnEntity;
        } else {
            return new RpgEntity(livingEntity);
        }
    }

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

    public static RpgEntity makeTempRpgEntity(LivingEntity livingEntity) {
        return new RpgEntity(livingEntity, true);
    }

    public static boolean checkEntityExists(UUID uuid) {
        RpgEntity checkEntity = allEntities.getOrDefault(uuid, null);
        if (checkEntity != null) return checkEntity.exists();
        return false;
    }


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
