package io.github.KidOfCubes.Managers;

import io.github.KidOfCubes.Events.RpgEntityDamageByEntityEvent;
import io.github.KidOfCubes.Events.RpgEntityDamageEvent;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgPlugin;
import io.github.KidOfCubes.Types.StatTriggerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.KidOfCubes.RpgPlugin.key;
import static io.github.KidOfCubes.RpgPlugin.logger;

public class EntityManager implements Listener {


    public static Map<UUID, RpgEntity> RpgEntities = new HashMap<>();
    public static Map<UUID, RpgEntity> TempRpgEntities = new HashMap<>();


    public static void init(){
        Bukkit.getScheduler().runTaskTimer(RpgPlugin.plugin, () -> {
            for (RpgEntity rpgEntity : RpgEntities.values()) {
                rpgEntity.attemptActivateStats(StatTriggerType.onTick,null);
            }
            for (RpgEntity rpgEntity : TempRpgEntities.values()) {
                rpgEntity.attemptActivateStats(StatTriggerType.onTick,null);
            }
        }, 20,1);
    }

    public static void close(){
        for (RpgEntity rpgEntity :
                TempRpgEntities.values()) {
            rpgEntity.livingEntity.remove();
            RpgEntities.remove(rpgEntity.livingEntity.getUniqueId());
        }
        for (RpgEntity rpgEntity :
                RpgEntities.values()) {
            rpgEntity.livingEntity.getPersistentDataContainer().set(key, PersistentDataType.STRING,rpgEntity.toJson());
        }

    }


    public static RpgEntity getRpgEntity(LivingEntity livingEntity){
        if(RpgEntities.containsKey(livingEntity.getUniqueId())){
            return RpgEntities.get(livingEntity.getUniqueId());
        }else{
            if(livingEntity.getPersistentDataContainer().has(key)){
                RpgEntity newEntity = new RpgEntity(livingEntity, RpgEntity.fromJson(livingEntity.getPersistentDataContainer().get(key,PersistentDataType.STRING)));
                RpgEntities.put(livingEntity.getUniqueId(),newEntity);
                return newEntity;
            }
            RpgEntity newEntity = new RpgEntity(livingEntity);
            RpgEntities.put(livingEntity.getUniqueId(),newEntity);
            return newEntity;
        }
    }
    public static RpgEntity getRpgEntity(UUID uuid){
        if(RpgEntities.containsKey(uuid)){
            return RpgEntities.get(uuid);
        }else{
            if(Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity) {
                if (livingEntity.getPersistentDataContainer().has(key)) {
                    RpgEntity newEntity = new RpgEntity(livingEntity, RpgEntity.fromJson(livingEntity.getPersistentDataContainer().get(key, PersistentDataType.STRING)));
                    RpgEntities.put(livingEntity.getUniqueId(), newEntity);
                    return newEntity;
                }
                RpgEntity newEntity = new RpgEntity(livingEntity);
                RpgEntities.put(livingEntity.getUniqueId(), newEntity);
                return newEntity;
            }else{
                throw new IllegalArgumentException("UUID "+uuid+" was either not a livingentity or was invalid");
            }
        }
    }


    @EventHandler
    public void onDamageByEntity(RpgEntityDamageByEntityEvent event){
        event.getAttacker().attemptActivateStats(StatTriggerType.onAttack,event);
    }

    @EventHandler
    public void onDamage(RpgEntityDamageEvent event){
        logger.info("RPG ENTITY DAMAGE EVENT HAPPENED");
        event.getEntity().attemptActivateStats(StatTriggerType.onDamage,event);
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(RpgEntities.containsKey(event.getEntity().getUniqueId())){
            RpgEntities.remove(event.getEntity().getUniqueId());
        }
        if(TempRpgEntities.containsKey(event.getEntity().getUniqueId())){
            TempRpgEntities.remove(event.getEntity().getUniqueId());
        }
    }




}
