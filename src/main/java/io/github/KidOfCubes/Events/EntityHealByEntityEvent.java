package io.github.KidOfCubes.Events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.NotNull;

public class EntityHealByEntityEvent extends EntityRegainHealthEvent {
    private LivingEntity healer;
    public EntityHealByEntityEvent(@NotNull Entity entity, double amount, @NotNull LivingEntity healer) {
        super(entity, amount, RegainReason.CUSTOM);
        this.healer = healer;
    }
    public EntityHealByEntityEvent(@NotNull Entity entity, double amount, @NotNull RegainReason regainReason) {
        super(entity, amount, regainReason);
    }

    public EntityHealByEntityEvent(@NotNull Entity entity, double amount, @NotNull RegainReason regainReason, boolean isFastRegen) {
        super(entity, amount, regainReason, isFastRegen);
    }
    public LivingEntity getHealer(){
        return healer;
    }
    public void setHealer(LivingEntity healer){
        this.healer = healer;
    }
}
