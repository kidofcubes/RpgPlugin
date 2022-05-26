package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class RpgEntityHealthChangeEvent extends Event implements Cancellable {

    //figure out good way

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

    public RpgEntityHealthChangeEvent(RpgEntity entity, double amount){
        this.entity = entity;
        this.amount = amount;
    }

    private RpgEntity entity;
    private double amount =0;

    public RpgEntity getEntity() {
        return entity;
    }

    public void setEntity(RpgEntity entity) {
        this.entity = entity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
