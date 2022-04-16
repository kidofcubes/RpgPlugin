package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RpgEntityDamageByEntityEvent extends RpgEntityDamageEvent {


    public RpgEntityDamageByEntityEvent(RpgEntity victim, double damage, RpgEntity attacker) {
        super(victim, damage);
        this.attacker = attacker;
    }

    private RpgEntity attacker;


    public RpgEntity getAttacker(){
        return attacker;
    }
    public void setAttacker(RpgEntity attacker){
        this.attacker = attacker;
    }
}
