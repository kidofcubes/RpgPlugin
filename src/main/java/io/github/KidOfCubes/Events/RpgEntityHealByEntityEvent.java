package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.NotNull;

public class RpgEntityHealByEntityEvent extends RpgEntityHealEvent {
    public RpgEntityHealByEntityEvent(RpgEntity victim, double change, RpgEntity healer) {
        super(victim, change);
        this.healer = healer;
    }

    private RpgEntity healer;


    public RpgEntity getHealer(){
        return healer;
    }
    public void setHealer(RpgEntity healer){
        this.healer = healer;
    }
}
