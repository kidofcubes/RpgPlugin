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

public class RpgEntityDamageEvent extends Event implements Cancellable, ActivateStats {

    private static final HandlerList handlers = new HandlerList();
    private final Map<DamageType, Double> damage = new HashMap<>();
    private boolean canceled = false;
    private RpgEntity entity;
    private double amount = 0;


    public RpgEntityDamageEvent(@NotNull RpgEntity entity, @NotNull DamageType type, double amount) {
        this(entity,type,amount,null);
    }
    public RpgEntityDamageEvent(@NotNull RpgEntity entity, @NotNull DamageType type, double amount, List<Stat> activateStats) {
        this(entity,new HashMap<>(),activateStats);
        setDamage(type, amount);
    }


    public RpgEntityDamageEvent(@NotNull RpgEntity entity, @NotNull Map<DamageType, Double> amount, List<Stat> activateStats) {
        this.entity = entity;
        initDamage();
        for (Map.Entry<DamageType, Double> pair : amount.entrySet()) {
            setDamage(pair.getKey(), pair.getValue());
        }
        addActivationStats(activateStats);
    }

    public static HandlerList getHandlerList() {
        return handlers;
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
    public HandlerList getHandlers() {
        return handlers;
    }

    private void initDamage() {
        DamageType[] types = DamageType.values();
        for (DamageType type : types) {
            damage.put(type, 0.0);
        }
    }

    public RpgEntity getEntity() {
        return entity;
    }

    public void setEntity(RpgEntity entity) {
        this.entity = entity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public double getDamage(DamageType type) {
        return damage.get(type);
    }

    public Map<DamageType, Double> getDamage() {
        return damage;
    }

    public double getTotalDamage() {
        double total = 0;
        for (Map.Entry<DamageType, Double> pair : damage.entrySet()) {
            if (pair.getValue() > 0) total += pair.getValue();
        }
        return total;
    }

    public void addDamage(DamageType type, double additional) {
        setDamage(type, getDamage(type) + additional);
    }

    public void multiplyDamage(DamageType type, double multiplier) {
        setDamage(type, getDamage(type) * multiplier);
    }

    public void setDamage(DamageType type, double newDamage) {
        damage.put(type, newDamage);
    }
}
