package io.github.KidOfCubes.Stats;

import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.Events.RpgEntityDamageByEntityEvent;
import io.github.KidOfCubes.Events.RpgEntityHealByEntityEvent;
import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.Stat;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.Event;

public class BaseDamage extends Stat {
    public static String description = "BaseDamage";
    public static StatType statType = StatType.hidden;
    public BaseDamage(int level) {
        super(level);
    }
    @Override
    protected void run(RpgActivateStatEvent event) {
        if(event.getTriggerEvent() instanceof RpgEntityDamageByEntityEvent damageByEntityEvent){
            damageByEntityEvent.setDamage(damageByEntityEvent.getChange()+level);
        }
    }
}
