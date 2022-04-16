package io.github.KidOfCubes;

import io.github.KidOfCubes.Events.RpgEntityDamageByEntityEvent;
import io.github.KidOfCubes.Events.RpgEntityHealByEntityEvent;
import io.github.KidOfCubes.Types.StatTriggerType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static io.github.KidOfCubes.ExtraFunctions.isEmpty;
import static io.github.KidOfCubes.Managers.EntityManager.TempRpgEntities;

public class RpgEntity extends RpgElement{
    public LivingEntity livingEntity;
    private double health;
    private double maxHealth;
    private List<RpgEntity> targets = new ArrayList<>();
    private List<RpgEntity> allies  = new ArrayList<>();

    public RpgEntity(LivingEntity livingEntity){
        this.livingEntity = livingEntity;
        allies.add(this);
        level = 0;
    }
    public RpgEntity(LivingEntity livingEntity, boolean tempEntity){
        this(livingEntity);
        if(tempEntity) {
            TempRpgEntities.put(livingEntity.getUniqueId(),this);
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
    public RpgEntityDamageByEntityEvent damage(double amount, RpgEntity attacker){ //AMOUNT IS FOR BASE AMOUNT, WILL
        RpgEntityDamageByEntityEvent event = new RpgEntityDamageByEntityEvent(this,amount, attacker);
        event.callEvent();
        return event;
    }
    public RpgEntityDamageByEntityEvent damage(double amount, RpgEntity attacker, boolean call){ //AMOUNT IS FOR BASE AMOUNT, WILL
        RpgEntityDamageByEntityEvent event = new RpgEntityDamageByEntityEvent(this,amount, attacker);
        event.callEvent();
        return event;
    }
    public void heal(double amount, RpgEntity attacker){
        RpgEntityHealByEntityEvent event = new RpgEntityHealByEntityEvent(this,amount, attacker);
        event.callEvent();
    }

    /*public RpgEntity(LivingEntity livingEntity, int level){
        this.livingEntity = livingEntity;
        allies.add(this);
        this.level = level;
    }*/

    public void addTarget(RpgEntity target){
        targets.add(target);
    }

    public boolean isAlly(RpgEntity entity){
        if(entity!=this) {
            if (parent != null) {
                return parent.isAlly(entity);
            } else {
                return allies.contains(entity);
            }
        }else{
            return true;
        }
    }

    public boolean isTarget(RpgEntity entity){
        if(entity!=this) {
            if (parent != null) {
                return parent.isTarget(entity);
            } else {
                return targets.contains(entity);
            }
        }else{
            return false;
        }
    }

    public void attemptActivateStats(StatTriggerType type, Event event){
        if(stats.containsKey(type)) {
            for (Stat stat :
                    stats.get(type)) {
                stat.trigger(null,this, event);
            }
        }
        if(livingEntity.getEquipment()!=null) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack item = livingEntity.getEquipment().getItem(slot);
                if(!isEmpty(item)){
                    RpgItem temp = new RpgItem(item);
                    if(temp.stats.containsKey(type)) {
                        for (Stat stat :
                                temp.stats.get(type)) {
                            stat.trigger(null, this, event);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<Stat> getEffectiveStats(){
        List<Stat> list = new ArrayList<>();
        list.addAll(getAllStats());
        if(livingEntity.getEquipment()!=null) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack item = livingEntity.getEquipment().getItem(slot);
                if(!isEmpty(item)){
                    RpgItem temp = new RpgItem(item);
                    list.addAll(temp.getEffectiveStats());
                }
            }
        }
        Map<String,Stat> levels = new HashMap<>();
        for (Stat stat : list) {
            levels.putIfAbsent(stat.getName(),stat);
            if(levels.get(stat.getName()).level<stat.level){
                levels.put(stat.getName(),stat);
            }
        }
        return levels.values().stream().toList();
    }
}
