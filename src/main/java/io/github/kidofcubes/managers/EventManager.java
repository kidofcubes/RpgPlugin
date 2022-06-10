package io.github.kidofcubes.managers;

import io.github.kidofcubes.events.RpgEntityDamageByObjectEvent;
import io.github.kidofcubes.events.RpgEntityDamageEvent;
import io.github.kidofcubes.events.RpgEntityHealEvent;
import io.github.kidofcubes.types.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
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
    public void onEntityDamage(EntityDamageEvent event) {
        if(MapDefaultDamage) {
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
                        } else {
                            customEvent = new RpgEntityDamageEvent(RpgManager.getRpgEntity(entity), DamageType.fromDamageCause(event.getCause()), event.getDamage());
                        }
                        pluginManager.callEvent(customEvent);
                        event.setCancelled(event.isCancelled());
                        event.setDamage(customEvent.getTotalDamage());
                    }
                }
            }
        }
    }



    @EventHandler
    public void onEntityHeal(EntityRegainHealthEvent event) {
        if (MapDefaultHealing) {
            if (event.getEntity() instanceof LivingEntity entity) {
                if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.CUSTOM) { //ignore custom


                    RpgEntityHealEvent customEvent = new RpgEntityHealEvent(RpgManager.getRpgEntity(entity), event.getAmount());
                    pluginManager.callEvent(customEvent);
                    event.setAmount(customEvent.getAmount());
                }
            }
        }
    }


}
