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
    public List<Object> data3 = new ArrayList<>();*/

    private RpgEntity caster;
    private RpgEntity parent;
    private RpgEntity target;
    private List<RpgEntity> spawnedEntities = new ArrayList<>();
    private String triggerString;
    private boolean activated = false;

    public RpgActivateStatEvent(RpgEntity caster, RpgElement parent, String triggerString){
        this.caster = caster;
        this.triggerString = triggerString;
    }
    public RpgActivateStatEvent(RpgEntity caster, String triggerString, RpgEntity target){
        this.caster = caster;
        this.triggerString = triggerString;
        this.target = target;
    }

}
