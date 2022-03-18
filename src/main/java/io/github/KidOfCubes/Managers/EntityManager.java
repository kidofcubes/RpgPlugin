package io.github.KidOfCubes.Managers;

import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.Types.StatTriggerType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityManager implements Listener {


    public static Map<UUID, RpgEntity> RpgEntities = new HashMap<>();



    public static RpgEntity getRpgEntity(LivingEntity livingEntity){
        if(RpgEntities.containsKey(livingEntity.getUniqueId())){
            return RpgEntities.get(livingEntity.getUniqueId());
        }else{
            RpgEntity newEntity = new RpgEntity(livingEntity);
            RpgEntities.put(livingEntity.getUniqueId(),newEntity);
            return newEntity;
        }
    }


    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof LivingEntity _victim && event.getDamager() instanceof LivingEntity _attacker){
            RpgEntity victim = getRpgEntity(_victim);
            RpgEntity attacker = getRpgEntity(_attacker);
            attacker.attemptActivateStats(StatTriggerType.onAttack,event);
            victim.attemptActivateStats(StatTriggerType.onDamage,event);
        }
    }

}
