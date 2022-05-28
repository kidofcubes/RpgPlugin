package io.github.KidOfCubes.Managers;

import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.KidOfCubes.RpgPlugin.key;
import static io.github.KidOfCubes.RpgPlugin.logger;

public class RpgManager implements Listener {
    private static final HashMap<UUID, RpgObject> allElements = new HashMap<UUID, RpgObject>();

    public static void init(){

    }
    public static void close(){
        saveAll();
    }


    @Nullable
    public static RpgObject getRpgObject(UUID uuid){
        RpgObject returnValue = allElements.getOrDefault(uuid,null);
        if(returnValue!=null){
            if(returnValue.exists()){
                return returnValue;
            }else{
                allElements.remove(uuid);
                return null;
            }
        }else{
            return null;
        }

    }

    public static void garbageCollect(){
        allElements.entrySet().removeIf(entry -> entry.getValue().exists());
    }

    public static void saveAll(){
        for (Map.Entry<UUID, RpgObject> entry : allElements.entrySet()) {
            if(entry.getValue().exists()){
                entry.getValue().save();
            }
        }
    }

    public static void addRpgObject(UUID uuid, RpgObject object){
        allElements.put(uuid, object);
    }
    public static void addTempRpgObject(UUID uuid, RpgObject object){
        allElements.put(uuid, object); //different map maybe?
    }


    public static RpgEntity getRpgEntity(LivingEntity livingEntity){
        RpgEntity returnEntity = (RpgEntity) getRpgObject(livingEntity.getUniqueId());
        if(returnEntity!=null){
            return returnEntity;
        }else{
            logger.info("MADE ENTITY"+livingEntity.name());
            if(livingEntity.getPersistentDataContainer().has(key)){
                RpgEntity newEntity = new RpgEntity(livingEntity, RpgEntity.fromJson(livingEntity.getPersistentDataContainer().get(key, PersistentDataType.STRING)));
                addRpgObject(newEntity.getUUID(),newEntity);
                return newEntity;
            }
            RpgEntity newEntity = new RpgEntity(livingEntity);
            addRpgObject(newEntity.getUUID(),newEntity);
            return newEntity;
        }
    }

    public static RpgEntity getRpgEntity(UUID uuid){
        if(Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity) {
            return getRpgEntity(livingEntity);
        }
        return null;
    }

    public static RpgEntity makeTempRpgEntity(LivingEntity livingEntity){
        RpgEntity newEntity = new RpgEntity(livingEntity,true);
        addTempRpgObject(livingEntity.getUniqueId(),newEntity);
        return newEntity;
    }
}
