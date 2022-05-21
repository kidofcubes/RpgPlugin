package io.github.KidOfCubes.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RpgTickEvent extends Event implements Cancellable {

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

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    //endregion
}
