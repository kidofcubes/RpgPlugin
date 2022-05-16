package io.github.KidOfCubes.Stats;

import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.Events.RpgEntityHealByEntityEvent;
import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.github.KidOfCubes.ParticleHelper.particleRing;

public class HealingAura extends Stat {

    public static String description = "Sends a healing aura every 2 seconds that heal allies based on their max health";
    public static StatType statType = StatType.stat;
    int countTick = 0;
    Location runLocation;
    float radius = 10;
    float pulseInterval = 40;
    float pulseTime = 30;
    int particlesInRing = 32;
    Color particleColor = Color.fromRGB(64,255,64);
    public static List<String> triggerStrings = new ArrayList<String>(){{add("RPGTICK");}};

    public HealingAura(int level) {
        super(level);
    }

    @Override
    protected void run(RpgActivateStatEvent event) {

        if(countTick==0){

            if(event.getParent() instanceof RpgEntity rpgEntity){
                runLocation = rpgEntity.livingEntity.getLocation();
            }else{
                if(event.getCaster() instanceof RpgEntity rpgEntity){
                    runLocation = rpgEntity.livingEntity.getLocation();
                }else{
                    return;
                }
            }
            Collection<LivingEntity> nearby = runLocation.getNearbyLivingEntities(radius);
            RpgEntity owner = null;
            if(event.getCaster() instanceof RpgEntity rpgEntity) owner = rpgEntity;
            if(event.getParent() instanceof RpgEntity rpgEntity) owner = rpgEntity;
            //if(owner==null) return;



            for (LivingEntity entity : nearby) {
                if(owner.isAlly(EntityManager.getRpgEntity(entity))){
//                    RpgEntityHealByEntityEvent healEvent = EntityManager.getRpgEntity(entity).heal(level, owner, false);
//                    owner.attemptActivateStats(StatTriggerType.onHeal, healEvent);
//                    healEvent.setChange(healEvent.getChange()/5);
//                    healEvent.callEvent();
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
