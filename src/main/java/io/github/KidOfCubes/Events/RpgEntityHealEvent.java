package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RpgEntityHealEvent extends Event implements Cancellable {

    //region Event stuff

    private static final HandlerList handlers = new HandlerList();
    private boolean canceled = false;

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


    public static HandlerList getHandlerList() {
        return handlers;
    }
    //endregion



    @NotNull
    private RpgEntity entity;

    double amount;

    public RpgEntityHealEvent(@NotNull RpgEntity entity, double amount) {
        this.entity = entity;
        this.amount = amount;
    }


    public @NotNull RpgEntity getEntity() {
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
