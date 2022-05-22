package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class RpgEntityHealthChangeEvent extends Event implements Cancellable {

    //region Event stuff
    public static final HandlerList HANDLERS = new HandlerList();
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
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    //endregion

    public RpgEntityHealthChangeEvent(RpgEntity entity, double change){
        this(entity,change,null);
    }
    public RpgEntityHealthChangeEvent(RpgEntity entity, double change, RpgElement cause){
        this.entity = entity;
        this.change = change;
        this.cause = cause;
    }

    private RpgEntity entity;
    private double change=0;
    private RpgElement cause;

    public RpgEntity getEntity() {
        return entity;
    }

    public void setEntity(RpgEntity entity) {
        this.entity = entity;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public RpgElement getCause() {
        if(cause instanceof RpgEntity rpgEntity){
            logger.info("cause reason was "+rpgEntity.livingEntity.name());
        }
        return cause;
    }

    public void setCause(RpgElement cause) {
        this.cause = cause;
    }
}
