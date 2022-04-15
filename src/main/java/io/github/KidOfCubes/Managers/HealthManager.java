package io.github.KidOfCubes.Managers;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import io.github.KidOfCubes.Events.EntityHealByEntityEvent;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static io.github.KidOfCubes.RpgPlugin.logger;
import static io.github.KidOfCubes.RpgPlugin.plugin;

public class HealthManager implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof LivingEntity){
            logger.info("SET NO DAMAGE TICKS");
            ((LivingEntity)event.getEntity()).setNoDamageTicks(0);
            ((LivingEntity)event.getEntity()).setMaximumNoDamageTicks(0);
        }
    }
    @EventHandler
    public void onHeal(EntityRegainHealthEvent e){
        if(e instanceof EntityHealByEntityEvent event){
            
        }else{

        }
    }
    public static void changeHealthBy(RpgEntity attacker, RpgEntity victim, double amount){
        if(amount<0){
            victim.livingEntity.damage(-amount,attacker.livingEntity);
        }else{
            victim.livingEntity.setHealth(victim.livingEntity.getHealth()+amount);
        }
    }
}
