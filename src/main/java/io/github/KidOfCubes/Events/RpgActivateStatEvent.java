package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.Stat;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class RpgActivateStatEvent {


    private RpgElement caster;
    private RpgElement parent;
    private RpgElement target;

    private Event triggerEvent;
    public List<RpgElement> spawnedElements = new ArrayList<>();
    private List<Stat> triggerStats = new ArrayList<>();
    private boolean directOnly = false;

    public RpgActivateStatEvent addTriggerStat(Stat stat){
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

    public RpgActivateStatEvent event(Event event){
        setTriggerEvent(event);
        return this;
    }

    public RpgActivateStatEvent directOnly(boolean directOnly){
        setDirectOnly(directOnly);
        return this;
    }

    public RpgActivateStatEvent callEvent(RpgElement _parent){
        setParent(_parent);
        return callEvent();
    }
    public RpgActivateStatEvent callEvent(){

        double startTime = System.nanoTime();
        List<Stat> statsToCall = parent.getEffectiveStats(); //sorted in priority order
        for (Stat stat :
                statsToCall) {
            if(stat.activateConditions(this)){
                stat.trigger(this);
            }
        }
        long endTime = System.nanoTime();
        double duration = (endTime - startTime)/1000000.0;
        if(statsToCall.size()>0) {
            logger.info("callEvent tag took "+duration);
        }
        return this;
    }


    //region gettersetters
    public Event getTriggerEvent() {
        return triggerEvent;
    }

    public void setTriggerEvent(Event triggerEvent) {
        this.triggerEvent = triggerEvent;
    }

    public RpgElement getCaster() {
        return caster;
    }

    public void setCaster(RpgElement caster) {
        this.caster = caster;
    }

    public RpgElement getParent() {
        return parent;
    }

    public void setParent(RpgElement parent) {
        this.parent = parent;
    }

    public RpgElement getTarget() {
        return target;
    }

    public void setTarget(RpgElement target) {
        this.target = target;
    }

    public List<Stat> getTriggerStats() {
        return triggerStats;
    }

    public void setTriggerStats(List<Stat> triggerStats) {
        this.triggerStats = triggerStats;
    }

    public boolean isDirectOnly() {
        return directOnly;
    }

    public void setDirectOnly(boolean directOnly) {
        this.directOnly = directOnly;
    }
    //endregion
}
