package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.Stat;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class RpgActivateStatEvent extends Event implements Cancellable{



    private RpgElement caster;
    private RpgElement parent;
    private RpgElement target;


    private List<String> triggerStats = new ArrayList<>();
    private boolean directOnly = false;

/*    public RpgActivateStatEvent(RpgElement parent){
        this.parent = parent;
    }*/

    public RpgActivateStatEvent addTriggerStat(String stat){
        triggerStats.add(stat);
        return this;
    }

    public RpgActivateStatEvent caster(RpgElement caster){
        setCaster(caster);
        return this;
    }

    public RpgActivateStatEvent parent(RpgElement parent){
        setParent(parent);
        return this;
    }

    public RpgActivateStatEvent target(RpgElement target){
        setTarget(target);
        return this;
    }

    public RpgActivateStatEvent directOnly(boolean directOnly){
        setDirectOnly(directOnly);
        return this;
    }

    @Override
    public boolean callEvent() {
        if(parent!=null) {
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

    //region getter setters

    public @Nullable RpgElement getCaster() {
        return caster;
    }

    public void setCaster(@Nullable RpgElement caster) {
        this.caster = caster;
    }

    public @NotNull RpgElement getParent() {
        return parent;
    }

    public void setParent(@NotNull RpgElement parent) {
        this.parent = parent;
    }

    public @Nullable RpgElement getTarget() {
        return target;
    }

    public void setTarget(@Nullable RpgElement target) {
        this.target = target;
    }

    public List<String> getTriggerStats() {
        return triggerStats;
    }

    public void setTriggerStats(List<String> triggerStats) {
        this.triggerStats = triggerStats;
    }

    public boolean isDirectOnly() {
        return directOnly;
    }

    public void setDirectOnly(boolean directOnly) {
        this.directOnly = directOnly;
    }
    //endregion

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
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    //endregion

}
