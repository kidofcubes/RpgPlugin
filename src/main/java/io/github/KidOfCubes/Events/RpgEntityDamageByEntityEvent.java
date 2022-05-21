package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RpgEntityDamageByEntityEvent extends RpgEntityDamageEvent {


    public RpgEntityDamageByEntityEvent(RpgEntity victim, double damage, RpgElement attacker) {
        super(victim, damage);
        this.attacker = attacker;
    }

    private RpgElement attacker;


    public RpgElement getAttacker(){
        return attacker;
    }
    public void setAttacker(RpgElement attacker){
        this.attacker = attacker;
    }
}
