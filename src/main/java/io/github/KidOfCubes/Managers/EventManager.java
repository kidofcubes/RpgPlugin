package io.github.KidOfCubes.Managers;

import io.github.KidOfCubes.Events.RpgEntityDamageByEntityEvent;
import io.github.KidOfCubes.Events.RpgEntityDamageEvent;
import io.github.KidOfCubes.Events.RpgEntityHealByEntityEvent;
import io.github.KidOfCubes.Events.RpgEntityHealEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import static io.github.KidOfCubes.RpgPlugin.logger;


public class EventManager implements Listener {



    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            entity.setNoDamageTicks(0);
            entity.setMaximumNoDamageTicks(0);
            logger.info("DAMAGE CAUSE IS "+event.getCause());
            if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM) { //TRIGGERED BY NORMAL
                event.setCancelled(true);

                //START THE CUSTOM PROCESS
                if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && entityDamageByEntityEvent.getDamager() instanceof LivingEntity damager) { //RpgEntityDamageByEntityEvent
                    RpgEntityDamageByEntityEvent customEvent = new RpgEntityDamageByEntityEvent(EntityManager.getRpgEntity(entity), event.getDamage(), EntityManager.getRpgEntity(damager));
                    customEvent.callEvent();
                    EntityManager.getRpgEntity(damager).addTarget(EntityManager.getRpgEntity(entity));
                }else{ //RpgEntityDamageEvent
                    RpgEntityDamageEvent customEvent = new RpgEntityDamageEvent(EntityManager.getRpgEntity(entity), event.getDamage());
                    customEvent.callEvent();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.CUSTOM) { //TRIGGERED BY NORMAL
                event.setCancelled(true);

                //START THE CUSTOM PROCESS
                RpgEntityHealEvent customEvent = new RpgEntityHealEvent(EntityManager.getRpgEntity(entity), event.getAmount());
                customEvent.callEvent();
            }
        }
    }


    //BACK TO NORMAL EVENT
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEndRpgEntityDamage(RpgEntityDamageEvent event){
        if(!event.isCancelled()) {
            if (event instanceof RpgEntityDamageByEntityEvent damageByEntityEvent) {
/*              EntityDamageByEntityEvent apiEvent = new EntityDamageByEntityEvent(damageByEntityEvent.getAttacker().livingEntity,
                    damageByEntityEvent.getEntity().livingEntity,
                    EntityDamageEvent.DamageCause.CUSTOM,0);
                damageByEntityEvent.getEntity().livingEntity.setHealth(damageByEntityEvent.getEntity().livingEntity.getHealth()-event.getDamage());*/
                //damageByEntityEvent.getEntity().livingEntity.damage(damageByEntityEvent.getDamage(), damageByEntityEvent.getAttacker().livingEntity);
                damageByEntityEvent.getEntity().livingEntity.damage(damageByEntityEvent.getDamage());

                //TODO custom knockback??????????
                //TODO custom damaging???????????
                //damageByEntityEvent.getEntity().livingEntity.setHurtDirection();
                //damageByEntityEvent.getEntity().livingEntity.damage(damageByEntityEvent.getDamage(),damageByEntityEvent.getAttacker().livingEntity, EntityDamageEvent.DamageCause.CUSTOM);
            } else {
                event.getEntity().livingEntity.damage(event.getDamage());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEndRpgEntityHeal(RpgEntityHealEvent event){
        if(!event.isCancelled()) {
            if (event instanceof RpgEntityHealByEntityEvent healByEntityEvent) {
                healByEntityEvent.getEntity().livingEntity.setHealth((Math.max(healByEntityEvent.getEntity().livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),healByEntityEvent.getChange())));
            } else {
                event.getEntity().livingEntity.setHealth((Math.max(event.getEntity().livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),event.getChange())));
            }
        }
    }


}
