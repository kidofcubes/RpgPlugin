package io.github.KidOfCubes.Stats;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.Events.RpgEntityDamageByEntityEvent;
import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.github.KidOfCubes.ParticleHelper.particleRing;
import static io.github.KidOfCubes.RpgPlugin.logger;

public class DamagingAura extends Stat {


    public static String description = "Sends a damaging aura every 2 seconds that damages enemies";
    public static StatType statType = StatType.stat;
    public static List<String> triggerStrings = new ArrayList<String>(){{add("RPGTICK");}};

    int countTick = 0;
    Location runLocation;
    float radius = 10;
    float pulseInterval = 30;
    float pulseTime = 10;
    int particlesInRing = 32;
    Color particleColor = Color.fromRGB(255,64,64);

    public DamagingAura(int level, RpgElement statOwner) {
        super(level, statOwner);
    }

    @Override
    protected void run(RpgActivateStatEvent event) {


        if(countTick==0){

            if(statOwner instanceof RpgEntity rpgEntity){
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
            if(statOwner instanceof RpgEntity rpgEntity) owner = rpgEntity;
            if(event.getCaster() instanceof RpgEntity rpgEntity) owner = rpgEntity;
            logger.info("DAMAGING AURA THE OWNER NAME IS "+owner.livingEntity.name());
            if(owner==null) return;
            for (LivingEntity entity : nearby) {
                logger.info("entitynearby is "+entity.getName());
                if(owner.isTarget(EntityManager.getRpgEntity(entity))){
                    //RpgEntityDamageByEntityEvent damageEvent = EntityManager.getRpgEntity(entity).damage(level, owner, false);
                    //owner.attemptActivateStats(StatTriggerType.onAttack, damageEvent);
                    //damageEvent.setDamage(damageEvent.getDamage()/10);
                    //damageEvent.callEvent();
                }
            }
        }
        if(countTick<=pulseTime){
            particleRing(Particle.REDSTONE, particleColor,runLocation,particlesInRing,(countTick/pulseTime)*radius);
        }

        countTick++;
        if(countTick>=pulseInterval){
            countTick = 0;
        }
    }
}
