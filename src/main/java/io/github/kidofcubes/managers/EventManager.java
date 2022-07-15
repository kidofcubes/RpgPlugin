package io.github.kidofcubes.managers;

import io.github.kidofcubes.events.RpgEntityDamageByObjectEvent;
import io.github.kidofcubes.events.RpgEntityDamageEvent;
import io.github.kidofcubes.events.RpgEntityHealEvent;
import io.github.kidofcubes.types.DamageType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.PluginManager;

import static io.github.kidofcubes.RpgPlugin.*;


public class EventManager implements Listener {

    private final static PluginManager pluginManager = plugin.getServer().getPluginManager();

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            ((LivingEntity) event.getEntity()).setNoDamageTicks(0);
            ((LivingEntity) event.getEntity()).setMaximumNoDamageTicks(0);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(event.getHitEntity()!=null){

        }

    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!event.isCancelled()) {
            if (event.getEntity().isDead() || !event.getEntity().isValid() || event.isCancelled()) {
                return;
            }
            if (event.getEntity() instanceof LivingEntity entity) {
                if (entity.getHealth() <= 0) {
                    return;
                }

                if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM) { //CUSTOM is ignored so no looping

                    RpgEntityDamageEvent customEvent;
                    if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && entityDamageByEntityEvent.getDamager() instanceof LivingEntity damager) {
                        customEvent = new RpgEntityDamageByObjectEvent(RpgManager.getRpgEntity(entity), DamageType.fromDamageCause(event.getCause()), event.getDamage(), RpgManager.getRpgEntity(damager));
                    } else if(event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && entityDamageByEntityEvent.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity shooter){
                        customEvent = new RpgEntityDamageByObjectEvent(RpgManager.getRpgEntity(entity), DamageType.fromDamageCause(event.getCause()), event.getDamage(), RpgManager.getRpgEntity(shooter));
                    }else{
                        customEvent = new RpgEntityDamageEvent(RpgManager.getRpgEntity(entity), DamageType.fromDamageCause(event.getCause()), event.getDamage());
                    }
                    customEvent.setCancelled(event.isCancelled());
                    pluginManager.callEvent(customEvent);
                    event.setCancelled(customEvent.isCancelled());
                    event.setDamage(customEvent.getTotalDamage());
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEarly(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && entityDamageByEntityEvent.getDamager() instanceof LivingEntity damager) {
            System.out.println("EARLY DAMAGE EVENT ON "+event.getEntity()+" WITH DAMAGE "+event.getDamage()+" FROM "+damager.getName()+" IS CANCELLED IS "+event.isCancelled());
        }else{
            System.out.println("EARLY DAMAGE EVENT ON "+event.getEntity()+" WITH DAMAGE "+event.getDamage()+" IS CANCELLED IS "+event.isCancelled());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageMoniter(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && entityDamageByEntityEvent.getDamager() instanceof LivingEntity damager) {
            System.out.println("MONITOR DAMAGE EVENT ON "+event.getEntity()+" WITH DAMAGE "+event.getDamage()+" FROM "+damager.getName()+" IS CANCELLED IS "+event.isCancelled());
        }else{
            System.out.println("MONITOR DAMAGE EVENT ON "+event.getEntity()+" WITH DAMAGE "+event.getDamage()+" IS CANCELLED IS "+event.isCancelled());
        }
    }

    @EventHandler
    public void onEntityHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.CUSTOM) { //ignore custom


                RpgEntityHealEvent customEvent = new RpgEntityHealEvent(RpgManager.getRpgEntity(entity), event.getAmount());
                pluginManager.callEvent(customEvent);
                event.setAmount(customEvent.getAmount());
            }
        }
    }






}
