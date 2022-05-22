package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RpgEntityHealEvent extends RpgEntityHealthChangeEvent {
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    public RpgEntityHealEvent(RpgEntity victim, double change) {
        super(victim, change);
    }
}
