package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class RpgActivateStatEvent extends Event implements Cancellable {

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


/*    public List<Object> data1 = new ArrayList<>();
    public List<Object> data2 = new ArrayList<>();
    public List<Obj
    public RpgActivateStatEvent(RpgEntity caster, RpgElement parent){
        this(caster,parent,null);
    }ect> data3 = new ArrayList<>();*/

    private Event triggerEvent;



    private RpgElement caster;
    private RpgElement parent;
    private RpgElement target;
    public List<RpgElement> spawnedElements = new ArrayList<>();
    private String triggerString;
    protected boolean activated = false;

    public RpgActivateStatEvent(){
        this(null);
    }
    public RpgActivateStatEvent(RpgElement caster){
        this(caster,caster,null);
    }
    public RpgActivateStatEvent(RpgElement caster, RpgElement parent){
        this(caster,parent,null);
    }
    public RpgActivateStatEvent(RpgElement caster, RpgElement parent, String triggerString){
        this(caster,parent,triggerString,null);
    }
    public RpgActivateStatEvent(RpgElement caster, RpgElement parent, String triggerString, RpgEntity target){
        this.caster = caster;
        this.parent = parent;
        this.triggerString = triggerString;
        this.target = target;
    }

    public String getTriggerString() {
        return triggerString;
    }

    public Event getTriggerEvent(){
        return triggerEvent;
    }

    public RpgElement getCaster(){
        return caster;
    }

    public RpgElement getParent() {
        return parent;
    }

    public RpgElement getTarget() {
        return target;
    }    public void setTriggerEvent(Event triggerEvent) {
        this.triggerEvent = triggerEvent;
    }

    public void setCaster(RpgElement caster) {
        this.caster = caster;
    }

    public void setParent(RpgElement parent) {
        this.parent = parent;
    }

    public void setTarget(RpgElement target) {
        this.target = target;
    }

    public void setTriggerString(String triggerString) {
        this.triggerString = triggerString;
    }
}
