package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class RpgEntityDamageEvent extends RpgEntityHealthChangeEvent{
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    public RpgEntityDamageEvent(RpgEntity victim, double damage) {
        super(victim, -damage);
    }
    public RpgEntityDamageEvent(RpgEntity victim, double damage, RpgElement attacker) {
        super(victim, -damage, attacker);
        logger.info("attacker is a rpg element whaaa "+((RpgEntity)attacker).livingEntity.name());
    }
    public void setDamage(double damage){
        setChange(-damage);
    }
    public double getDamage(){
        return -getChange();
    }
}
