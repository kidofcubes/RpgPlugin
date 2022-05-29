package io.github.KidOfCubes.Managers;

import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgItem;
import io.github.KidOfCubes.RpgObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
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

import static io.github.KidOfCubes.RpgPlugin.*;

public class RpgManager implements Listener {
    private static final Map<UUID, RpgEntity> allEntities = new HashMap<UUID, RpgEntity>();
    private static final Map<UUID, RpgItem> allItems = new HashMap<UUID, RpgItem>();

    public static void init() {

    }

    public static void close() {
        saveAll();
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
            if (entry.getValue().exists()) {
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

/*    public static void addRpgObject(UUID uuid, RpgObject object){
        allElements.put(uuid, object);
    }
    public static void addTempRpgObject(UUID uuid, RpgObject object){
        allElements.put(uuid, object); //different map maybe?
    }*/

    public static void addRpgEntity(UUID key, RpgEntity rpgEntity) {
        allEntities.put(key, rpgEntity);
    }

    public static void addTempRpgEntity(UUID key, RpgEntity rpgEntity) {
        allEntities.put(key, rpgEntity); //considering different map
    }

    public static RpgEntity getRpgEntity(LivingEntity livingEntity) {
        RpgEntity returnEntity = getRpgEntity(livingEntity.getUniqueId());
        if (returnEntity != null) {
            return returnEntity;
        } else {
            RpgEntity newEntity = new RpgEntity(livingEntity);
            addRpgEntity(newEntity.getUUID(), newEntity);
            return newEntity;
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
        RpgEntity newEntity = new RpgEntity(livingEntity, true);
        addTempRpgEntity(livingEntity.getUniqueId(), newEntity);
        return newEntity;
    }

    public static boolean checkEntityExists(UUID uuid) {
        RpgEntity checkEntity = allEntities.getOrDefault(uuid, null);
        if (checkEntity != null) return checkEntity.exists();
        return false;
    }


    public static RpgItem getItem(ItemStack itemStack) { //reads uuid every time we get item, maybe not the smartest way?
        if (itemStack.getItemMeta().getPersistentDataContainer().has(uuidKey)) {
            RpgItem item = allItems.getOrDefault(UUID.fromString(itemStack.getItemMeta().getPersistentDataContainer().get(uuidKey, PersistentDataType.STRING)), null);
            if (item != null) {
                return item;
            }
        }
        return new RpgItem(itemStack);
    }





    @EventHandler
    public void onEntityDeath(EntityDeathEvent entityDeathEvent){
        allEntities.remove(entityDeathEvent.getEntity().getUniqueId());
    }
}
