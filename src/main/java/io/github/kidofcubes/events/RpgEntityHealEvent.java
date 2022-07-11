package io.github.kidofcubes.events;

import io.github.kidofcubes.ActivateStats;
import io.github.kidofcubes.RpgEntity;
import io.github.kidofcubes.Stat;
import io.github.kidofcubes.types.DamageType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpgEntityHealEvent extends Event implements Cancellable, ActivateStats {

    //region Event stuff

    private static final HandlerList handlers = new HandlerList();
    double amount;
    private boolean canceled = false;
    @NotNull
    private RpgEntity entity;

    public RpgEntityHealEvent(@NotNull RpgEntity entity, double amount) {
        this(entity,amount,null);
    }
    public RpgEntityHealEvent(@NotNull RpgEntity entity,double amount, List<Stat> activateStats) {
        this.entity = entity;
        this.amount = amount;
        if(activateStats!=null){
            for (Stat stat :
                    activateStats) {
                getActivationStats().put(stat.getClass(),stat);
            }
        }
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    //endregion

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
        return handlers;
    }

    public @NotNull
    RpgEntity getEntity() {
        return entity;
    }

    public void setEntity(@NotNull RpgEntity entity) {
        this.entity = entity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    private final Map<Class<? extends Stat>, Stat> activationStats = new HashMap<>();
    @Override
    public Map<Class<? extends Stat>, Stat> getActivationStats() {
        return activationStats;
    }
//gonna be like damage but oppisite


}
