package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.NotNull;

public class RpgEntityHealByElementEvent extends RpgEntityHealEvent {
    public RpgEntityHealByElementEvent(RpgEntity victim, double change, RpgElement healer) {
        super(victim, change);
        this.healer = healer;
    }

    private RpgElement healer;


    public RpgElement getHealer(){
        return healer;
    }
    public void setHealer(RpgElement healer){
        this.healer = healer;
    }
}
