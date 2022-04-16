package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RpgEntityDamageEvent extends RpgEntityHealthChangeEvent{
    public RpgEntityDamageEvent(RpgEntity victim, double damage) {
        super(victim, -damage);
    }
    public void setDamage(double damage){
        setChange(-damage);
    }
    public double getDamage(){
        return -getChange();
    }
}
