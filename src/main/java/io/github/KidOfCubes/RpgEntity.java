package io.github.KidOfCubes;

import io.github.KidOfCubes.Events.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static io.github.KidOfCubes.ExtraFunctions.isEmpty;
import static io.github.KidOfCubes.Managers.EntityManager.TempRpgEntities;
import static io.github.KidOfCubes.RpgPlugin.logger;

public class RpgEntity extends RpgElement{
    public LivingEntity livingEntity;
    private double health;
    private double maxHealth;
    private List<UUID> targets = new ArrayList<>();
    private List<UUID> allies  = new ArrayList<>();

    public RpgEntity(LivingEntity livingEntity){
        this.livingEntity = livingEntity;
        level = 0;
    }
    public RpgEntity(LivingEntity livingEntity, boolean tempEntity){
        this(livingEntity);
        if(tempEntity) {
            TempRpgEntities.put(livingEntity.getUniqueId(),this);
            logger.info("TEMPENTITIES: "+TempRpgEntities.size());
        }
    }
    public RpgEntity(LivingEntity livingEntity, RpgEntity parent, boolean tempEntity){
        this(livingEntity,tempEntity);
        if(parent!=null) {
            this.parent = parent;
        }
    }
    public RpgEntity(LivingEntity livingEntity, RpgElement from){
        this(livingEntity);
        stats = from.stats;
        level = from.level;
        name = from.name;
        if(from instanceof RpgEntity rpgEntity) {
            targets = rpgEntity.targets;
            allies = rpgEntity.allies;
        }
    }
    public RpgEntityDamageByEntityEvent damage(double amount, RpgElement attacker){ //AMOUNT IS FOR BASE AMOUNT
        return damage(amount,attacker,true);
    }
    public RpgEntityDamageByEntityEvent damage(double amount, RpgElement attacker, boolean call){ //AMOUNT IS FOR BASE AMOUNT, WILL RUN STATS i think
        RpgEntityDamageByEntityEvent event = new RpgEntityDamageByEntityEvent(this,amount, attacker);
        if(call) event.callEvent();
        return event;
    }
    public RpgEntityHealByElementEvent heal(double amount, RpgElement healer){
        return heal(amount, healer,  true);
    }
    public RpgEntityHealByElementEvent heal(double amount, RpgElement healer, boolean call){
        RpgEntityHealByElementEvent event = new RpgEntityHealByElementEvent(this,amount, healer);
        if(call) event.callEvent();
        return event;
    }

    /*public RpgEntity(LivingEntity livingEntity, int level){
        this.livingEntity = livingEntity;
        allies.add(this);
        this.level = level;
    }*/

    public void addTarget(RpgEntity target){
        targets.add(target.livingEntity.getUniqueId());
    }

    public boolean isAlly(RpgEntity entity){
        if(entity!=this) {
            if (parent != null) {
                return parent.isAlly(entity);
            } else {
                return allies.contains(entity.livingEntity.getUniqueId());
            }
        }else{
            return true;
        }
    }

    public boolean isTarget(RpgEntity entity){
        if(entity!=this) {
            if (parent != null) {
                return parent.isTarget(entity)||targets.contains(entity.livingEntity.getUniqueId());
            } else {
                return targets.contains(entity.livingEntity.getUniqueId());
            }
        }else{
            return false;
        }
    }


    List<Stat> effectiveStatsCache = new ArrayList<>();
    long effectiveStatsLastUpdate = 0;
    @Override
    public List<Stat> getEffectiveStats(){
        long now = System.currentTimeMillis();
        if(now-effectiveStatsLastUpdate>1000) {
            effectiveStatsLastUpdate = now;
            List<Stat> list = new ArrayList<>(getStats());
            if (livingEntity.getEquipment() != null) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack item = livingEntity.getEquipment().getItem(slot);
                    if (!isEmpty(item)) {
                        RpgItem temp = new RpgItem(item);
                        list.addAll(temp.getEffectiveStats());
                    }
                }
            }
            Map<String, Stat> levels = new HashMap<>();
            for (Stat stat : list) {
                levels.putIfAbsent(stat.getName(), stat);
                if (levels.get(stat.getName()).level < stat.level) {
                    levels.put(stat.getName(), stat);
                }
            }
            effectiveStatsCache = levels.values().stream().toList();
        }
        return effectiveStatsCache;

    }
}
