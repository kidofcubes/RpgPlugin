package io.github.KidOfCubes.Managers;

import io.github.KidOfCubes.RpgPlugin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageManager implements Listener {
    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof LivingEntity){
            RpgPlugin.logger.info("SET NO DAMAGE TICKS");
            ((LivingEntity)event.getEntity()).setNoDamageTicks(0);
            ((LivingEntity)event.getEntity()).setMaximumNoDamageTicks(0);
        }
    }
}
