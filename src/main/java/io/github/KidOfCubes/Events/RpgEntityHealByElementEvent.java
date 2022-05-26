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

public class RpgEntityHealByElementEvent extends RpgEntityHealEvent {//custom causeenum later?

    @NotNull
    private RpgElement cause;

    public RpgEntityHealByElementEvent(@NotNull RpgEntity entity, double change, @NotNull RpgElement cause) {
        super(entity, change);
        setCause(cause);
    }


    public @NotNull RpgElement getCause(){
        return cause;
    }
    public void setCause(@NotNull RpgElement cause){
        this.cause = cause;
    }
}
