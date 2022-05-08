package io.github.KidOfCubes.Stats;

import io.github.KidOfCubes.Events.RpgEntityHealByEntityEvent;
import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatTriggerType;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import java.util.Collection;

import static io.github.KidOfCubes.ParticleHelper.particleRing;

public class HealingAura extends Stat {

    int countTick = 0;
    Location runLocation;
    float radius = 10;
    float pulseInterval = 40;
    float pulseTime = 30;
    int particlesInRing = 32;
    Color particleColor = Color.fromRGB(64,255,64);

    public HealingAura(int level, RpgElement statParent) {
        super(level, statParent);
        description = "Sends a healing aura every 2 seconds that heal allies based on their max health";
        triggerType = StatTriggerType.onTick;
        statType = StatType.stat;
    }

    @Override
    protected void run(RpgElement statParent, RpgElement caller, int level, Event event) {

        if(countTick==0){

            if(statParent instanceof RpgEntity rpgEntity){
                runLocation = rpgEntity.livingEntity.getLocation();
            }else{
                if(caller instanceof RpgEntity rpgEntity){
                    runLocation = rpgEntity.livingEntity.getLocation();
                }else{
                    return;
                }
            }
            Collection<LivingEntity> nearby = runLocation.getNearbyLivingEntities(radius);
            RpgEntity owner = null;
            if(caller instanceof RpgEntity rpgEntity) owner = rpgEntity;
            if(statParent instanceof RpgEntity rpgEntity) owner = rpgEntity;
            //if(owner==null) return;



            for (LivingEntity entity : nearby) {
                if(owner.isAlly(EntityManager.getRpgEntity(entity))){
                    RpgEntityHealByEntityEvent healEvent = EntityManager.getRpgEntity(entity).heal(level, owner, false);
                    owner.attemptActivateStats(StatTriggerType.onHeal, healEvent);
                    healEvent.setChange(healEvent.getChange()/5);
                    healEvent.callEvent();
                }
            }
        }
        if(countTick<=pulseTime){
            particleRing(Particle.REDSTONE,particleColor,runLocation,particlesInRing,(countTick/pulseTime)*radius);
        }
        countTick++;

        if(countTick>=pulseInterval){
            countTick = 0;
        }
    }
}
