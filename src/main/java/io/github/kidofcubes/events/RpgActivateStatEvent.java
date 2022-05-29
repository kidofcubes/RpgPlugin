package io.github.kidofcubes.events;

import io.github.kidofcubes.RpgObject;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RpgActivateStatEvent extends Event implements Cancellable {


    //region Event stuff
    private static final HandlerList HANDLERS = new HandlerList();
    private RpgObject caster;
    private RpgObject parent;
    private RpgObject target;
    private List<String> triggerStats = new ArrayList<>();

    /*    public RpgActivateStatEvent(RpgObject parent){
            this.parent = parent;
        }*/
    private boolean directOnly = false;
    private boolean canceled = false;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public RpgActivateStatEvent addTriggerStat(String stat) {
        triggerStats.add(stat);
        return this;
    }

    public RpgActivateStatEvent caster(RpgObject caster) {
        setCaster(caster);
        return this;
    }

    public RpgActivateStatEvent parent(RpgObject parent) {
        setParent(parent);
        return this;
    }

    //region getter setters

    public RpgActivateStatEvent target(RpgObject target) {
        setTarget(target);
        return this;
    }

    public RpgActivateStatEvent directOnly(boolean directOnly) {
        setDirectOnly(directOnly);
        return this;
    }

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

    public RpgObject getCaster() {
        return caster;
    }

    public void setCaster(@Nullable RpgObject caster) {
        this.caster = caster;
    }

    public @NotNull
    RpgObject getParent() {
        return parent;
    }

    public void setParent(@NotNull RpgObject parent) {
        this.parent = parent;
    }

    public RpgObject getTarget() {
        return target;
    }

    public void setTarget(@Nullable RpgObject target) {
        this.target = target;
    }

    public List<String> getTriggerStats() {
        return triggerStats;
    }
    //endregion

    public void setTriggerStats(List<String> triggerStats) {
        this.triggerStats = triggerStats;
    }

    public boolean isDirectOnly() {
        return directOnly;
    }

    public void setDirectOnly(boolean directOnly) {
        this.directOnly = directOnly;
    }

    @Override
    public boolean isCancelled() {
        return this.canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }

    @Override
    public @NotNull
    HandlerList getHandlers() {
        return HANDLERS;
    }
    //endregion

}
