package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RpgEntityHealthChangeEvent extends Event implements Cancellable {

    //region Event stuff
    private static final HandlerList HANDLERS = new HandlerList();
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
    //endregion

    public RpgEntityHealthChangeEvent(RpgEntity victim, double change){
        this.victim = victim;
        this.change = change;
    }

    private RpgEntity victim;
    private double change=0;

    public RpgEntity getEntity(){
        return victim;
    }

    public void setEntity(RpgEntity victim){
        this.victim = victim;
    }

    public double getChange(){
        return change;
    }

    public void setChange(double change){
        this.change = change;
    }
}
