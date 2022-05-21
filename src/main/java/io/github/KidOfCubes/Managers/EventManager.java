package io.github.KidOfCubes.Managers;

import io.github.KidOfCubes.Events.*;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgPlugin;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import static io.github.KidOfCubes.RpgPlugin.logger;


public class EventManager implements Listener {


    public static void init(){
        Bukkit.getScheduler().runTaskTimer(RpgPlugin.plugin, () -> {
            new RpgTickEvent().callEvent();
        }, 20,1);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof LivingEntity){
//            logger.info("SET NO DAMAGE TICKS");
            ((LivingEntity)event.getEntity()).setNoDamageTicks(0);
            ((LivingEntity)event.getEntity()).setMaximumNoDamageTicks(0);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if(!event.isCancelled()) {
            if (event.getEntity().isDead() || !event.getEntity().isValid() || event.isCancelled()) {
                return;

            }
            if (event.getEntity() instanceof LivingEntity entity) {
                if (entity.getHealth() <= 0) {
                    return;
                }
/*                if (entity.getType() == EntityType.ZOGLIN) {
                    if (entity.getHealth() - event.getFinalDamage() > 0) {
                        logger.info("I CAN DO DAMAGETICKS");
                        Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, () -> {
                        }, 0);
                        Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, () -> {

                            ((LivingEntity)event.getEntity()).setNoDamageTicks(0);
                            ((LivingEntity)event.getEntity()).setMaximumNoDamageTicks(0);
                        }, 1);
                    } else {
                        logger.info("HI DMG TICK");
                        Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, () -> {
                            ((LivingEntity)event.getEntity()).setMaximumNoDamageTicks(100);
                            ((LivingEntity)event.getEntity()).setNoDamageTicks(100);
                        }, 0);
                        Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, () -> {
                            ((LivingEntity)event.getEntity()).setMaximumNoDamageTicks(100);
                            ((LivingEntity)event.getEntity()).setNoDamageTicks(100);
                        }, 1);
                    }
                    logger.info("entity type? " + entity.getName() +
                            " entity is dead? " + entity.isDead() +
                            " entity is valid? " + entity.isValid() +
                            " entity health? " + entity.getHealth());
                }*/
                if (!event.getEntity().isDead() && event.getEntity().isValid()) {
                    if (entity.getHealth() > 0) {
                        //logger.info("DAMAGE CAUSE IS " + event.getCause() + " AND DAMAGE IS " + event.getDamage() + " and " + event.getFinalDamage());
                        if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM) { //TRIGGERED BY NORMAL

                            //START THE CUSTOM PROCESS
                            if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && entityDamageByEntityEvent.getDamager() instanceof LivingEntity damager) { //RpgEntityDamageByEntityEvent
                                RpgEntityDamageByEntityEvent customEvent = new RpgEntityDamageByEntityEvent(EntityManager.getRpgEntity(entity), event.getDamage(), EntityManager.getRpgEntity(damager));
                                customEvent.callEvent();
                            } else { //RpgEntityDamageEvent
                                RpgEntityDamageEvent customEvent = new RpgEntityDamageEvent(EntityManager.getRpgEntity(entity), event.getDamage());
                                customEvent.callEvent();
                            }

/*                        TestDamageEvent test = new TestDamageEvent(event.getEntity(), EntityDamageEvent.DamageCause.CUSTOM, 100);
                        test.callEvent();
                        event.getEntity().setLastDamageCause(test);*/

                            //event.setDamage(0);
                            event.setCancelled(true);
                        }

                        //entity.setNoDamageTicks(0);
                        //entity.setMaximumNoDamageTicks(0);
                    }
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
        if(!event.isCancelled()&&!event.getEntity().livingEntity.isDead()&&event.getEntity().livingEntity.isValid()&&event.getEntity().livingEntity.getHealth()!=0) {
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
            if (event instanceof RpgEntityHealByElementEvent healByEntityEvent) {
                healByEntityEvent.getEntity().livingEntity.setHealth((Math.max(healByEntityEvent.getEntity().livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),healByEntityEvent.getChange())));
            } else {
                event.getEntity().livingEntity.setHealth((Math.max(event.getEntity().livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),event.getChange())));
            }
        }
    }


}
