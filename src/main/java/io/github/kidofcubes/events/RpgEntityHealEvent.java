package io.github.kidofcubes.events;

import io.github.kidofcubes.RpgEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RpgEntityHealEvent extends Event implements Cancellable {

    //region Event stuff

    private static final HandlerList handlers = new HandlerList();
    double amount;
    private boolean canceled = false;
    @NotNull
    private RpgEntity entity;

    public RpgEntityHealEvent(@NotNull RpgEntity entity, double amount) {
        this.entity = entity;
        this.amount = amount;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    //endregion

    @Override
    public boolean isCancelled() {
        return this.canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public @NotNull
    RpgEntity getEntity() {
        return entity;
    }

    public void setEntity(@NotNull RpgEntity entity) {
        this.entity = entity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


//gonna be like damage but oppisite


}
