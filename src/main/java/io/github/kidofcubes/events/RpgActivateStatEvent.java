package io.github.kidofcubes.events;

import io.github.kidofcubes.RpgObject;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


/**
 * idea for this was something like
 * item has the stat SMITE
 * entity holding item
 * new activate event
 * parent is item
 * entity is caster
 * target is target
 */
public class RpgActivateStatEvent extends Event implements Cancellable {


    private RpgObject caster;
    private final RpgObject parent;
    private RpgObject target;
    private final List<String> triggerStats;

    //todo better way to activate stats
    public RpgActivateStatEvent(RpgObject parent, List<String> triggerStats){
        this(parent, null, triggerStats);
    }

    public RpgActivateStatEvent(RpgObject parent, RpgObject caster, List<String> triggerStats){
        this(parent, caster, null, triggerStats);
    }

    public RpgActivateStatEvent(RpgObject parent, RpgObject caster, RpgObject target, List<String> triggerStats){
        this.parent = parent;
        this.caster = caster;
        this.target = target;
        this.triggerStats = triggerStats;
    }


    /**
     * Calls the event and tests if cancelled.
     *
     * @return false if event was cancelled or parent was not set, otherwise true.
     */
    @Override
    public boolean callEvent() {
        if (parent != null) {
            if (target == null) {
                target = parent;
            }
            if (caster == null) {
                caster = parent;
            }
            return super.callEvent();
        }
        return false;
    }

    /**
     * Gets the RpgObject that activated the stats (defaults to parent if null)
     * @return The RpgObject that activated the stats
     */
    @NotNull
    public RpgObject getCaster() {
        if (caster == null) {
            return getParent();
        }else{
            return caster;
        }
    }

    /**
     * Gets the RpgObject that owns the triggerStats
     * @return The RpgObject that owns the triggerStats
     */
    @NotNull
    public RpgObject getParent() {
        return parent;
    }

    /**
     * Gets the target RpgObject (defaults to parent if null)
     * @return The target RpgObject
     */
    @NotNull
    public RpgObject getTarget() {
        return target;
    }


    /**
     * Gets the stats that will be triggered
     * @return The stats that will be triggered
     */
    public List<String> getTriggerStats() {
        return triggerStats;
    }
    //endregion



    private boolean cancelled = false;

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull
    HandlerList getHandlers() {
        return HANDLERS;
    }
    //endregion

}
