package io.github.KidOfCubes.Stats;

import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.Managers.EntityManager;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgPlugin;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import static io.github.KidOfCubes.RpgPlugin.logger;
import static io.github.KidOfCubes.RpgPlugin.plugin;

public class SummonTotem extends Stat {


    public static String description = "SUMMONS THE TOTEM NOT FROM WYNNCRAFT";
    public static StatType statType = StatType.stat;

    public SummonTotem(int level) {
        super(level);
    }


    @Override
    public boolean activateConditions(RpgActivateStatEvent event) {
        return event.getTriggerStats().contains(this);
    }

    @Override
    protected void run(RpgActivateStatEvent event) {
        logger.info("totem activated");
        if(event.getCaster() instanceof RpgEntity caster){
            ArmorStand armorStand = (ArmorStand) caster.livingEntity.getWorld().spawnEntity(caster.livingEntity.getLocation().add(0,1,0), EntityType.ARMOR_STAND);
            //armorStand.setGravity(false);
            armorStand.setVelocity(caster.livingEntity.getLocation().getDirection().multiply(2.5));
            RpgEntity totem = new RpgEntity(armorStand,caster,true);

            new BukkitRunnable() {

                @Override
                public void run() {
                    if(armorStand.isOnGround()){
                        totem.addStat(new DamagingAura(level));
                        totem.addStat(new HealingAura(level));
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            armorStand.setHealth(0);
                        }, level*5*20);
                        this.cancel();

                    }
                }
            }.runTaskTimer(plugin, 0, 1);
        }
    }
}
