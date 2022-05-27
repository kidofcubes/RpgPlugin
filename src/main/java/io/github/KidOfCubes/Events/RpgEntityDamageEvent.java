package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.Types.DamageType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class RpgEntityDamageEvent extends Event implements Cancellable {

    //region Event stuff

    private static final HandlerList handlers = new HandlerList();
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
        return handlers;
    }


    public static HandlerList getHandlerList() {
        return handlers;
    }
    //endregion


    public RpgEntityDamageEvent(RpgEntity entity, DamageType type, double amount){
        this.entity = entity;
        initDamage();
        setDamage(type,amount);
    }
    public RpgEntityDamageEvent(RpgEntity entity, Map<DamageType,Double> amount){
        this.entity = entity;
        initDamage();
        for (Map.Entry<DamageType,Double> pair : amount.entrySet()) {
            setDamage(pair.getKey(),pair.getValue());
        }
    }
    private void initDamage(){
        DamageType[] types = DamageType.values();
        for (DamageType type : types) {
            damage.put(type, 0.0);
        }
    }

    //private final DamageType defaultType = DamageType.Physical;

    private final Map<DamageType,Double> damage = new HashMap<>();

    private RpgEntity entity;
    private double amount=0;

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


    public double getDamage(DamageType type){
        return damage.get(type);
    }
    public Map<DamageType,Double> getDamage(){
        return damage;
    }

    public double getTotalDamage(){
        double total = 0;
        for (Map.Entry<DamageType,Double> pair : damage.entrySet()) {
            if(pair.getValue()>0) total += pair.getValue();
        }
        return total;
    }

    public void addDamage(DamageType type, double additional){
        setDamage(type,getDamage(type)+additional);
    }
    public void multiplyDamage(DamageType type, double multiplier){
        setDamage(type,getDamage(type)*multiplier);
    }
    public void setDamage(DamageType type, double newDamage){
        damage.put(type,newDamage);
    }
}
