package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.NotNull;

public class RpgEntityHealEvent extends RpgEntityHealthChangeEvent {
    public RpgEntityHealEvent(RpgEntity entity, double change) {
        super(entity, change);
    }

//gonna be like damage but oppisite



}
