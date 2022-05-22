package io.github.KidOfCubes.Managers;

import io.github.KidOfCubes.Events.*;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgPlugin;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.RegisteredListener;

import static io.github.KidOfCubes.RpgPlugin.logger;


public class EventManager implements Listener {


    public void init(){
        Bukkit.getScheduler().runTaskTimer(RpgPlugin.plugin, () -> {
            //new RpgTickEvent().callEvent();
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

                if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM) { //CUSTOM IS IGNORED

                    //START THE CUSTOM PROCESS
                    RpgEntityDamageEvent customEvent;
                    if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && entityDamageByEntityEvent.getDamager() instanceof LivingEntity damager) { //RpgEntityDamageByEntityEvent
                        customEvent = new RpgEntityDamageEvent(EntityManager.getRpgEntity(entity), event.getDamage(), EntityManager.getRpgEntity(damager));
                    } else { //RpgEntityDamageEvent
                        customEvent = new RpgEntityDamageEvent(EntityManager.getRpgEntity(entity), event.getDamage());
                    }
                    long startTime = System.nanoTime();
                    customEvent.callEvent();
                    long endTime = System.nanoTime();
                    logger.info("setting damage to "+customEvent.getDamage() + " took time "+((endTime-startTime)/1000000.0));
                    event.setDamage(customEvent.getDamage());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {//todo check if map normal heals
            if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.CUSTOM) { //TRIGGERED BY NORMAL

                //START THE CUSTOM PROCESS
                RpgEntityHealEvent customEvent = new RpgEntityHealEvent(EntityManager.getRpgEntity(entity), event.getAmount());
                customEvent.callEvent();
                event.setAmount(customEvent.getChange());
            }
        }
    }


}
