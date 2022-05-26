package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class RpgEntityDamageEvent extends RpgEntityHealthChangeEvent{
/*    public HandlerList getHandlerList() {
        return HANDLERS;
    }*/
    public RpgEntityDamageEvent(RpgEntity victim, double damage) {
        super(victim, -damage);
    }
    public void setDamage(double damage){
        setAmount(-damage);
    }
    public double getDamage(){
        return -getAmount();
    }
}
