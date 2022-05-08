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
    public RpgEntityDamageByEntityEvent damage(double amount, RpgEntity attacker){ //AMOUNT IS FOR BASE AMOUNT
        RpgEntityDamageByEntityEvent event = new RpgEntityDamageByEntityEvent(this,amount, attacker);
        event.callEvent();
        return event;
    }
    public RpgEntityDamageByEntityEvent damage(double amount, RpgEntity attacker, boolean call){ //AMOUNT IS FOR BASE AMOUNT, WILL RUN STATS i think
        RpgEntityDamageByEntityEvent event = new RpgEntityDamageByEntityEvent(this,amount, attacker);
        if(call) event.callEvent();
        return event;
    }
    public RpgEntityHealByEntityEvent heal(double amount, RpgEntity healer){
        RpgEntityHealByEntityEvent event = new RpgEntityHealByEntityEvent(this,amount, healer);
        event.callEvent();
        return event;
    }
    public RpgEntityHealByEntityEvent heal(double amount, RpgEntity healer, boolean call){
        RpgEntityHealByEntityEvent event = new RpgEntityHealByEntityEvent(this,amount, healer);
        if(call) event.callEvent();
        return event;
    }

    /*public RpgEntity(LivingEntity livingEntity, int level){
        this.livingEntity = livingEntity;
        allies.add(this);
        this.level = level;
    }*/

    public void addTarget(RpgEntity target){
        logger.info(livingEntity.getName()+ "added a target "+target.livingEntity.getName());
        logger.info(livingEntity.getName()+ "added a target "+target.livingEntity.getName());
        logger.info(livingEntity.getName()+ "added a target "+target.livingEntity.getName());
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
                logger.info("parent "+parent.livingEntity.getName()+" says is target"+parent.isTarget(entity)+" and i think "+targets.contains(entity.livingEntity.getUniqueId())+" and the length of my targets is "+targets.size());
                return parent.isTarget(entity)||targets.contains(entity.livingEntity.getUniqueId());
            } else {
                logger.info("i think "+targets.contains(entity.livingEntity.getUniqueId())+" and the length of my targets is "+targets.size());
                return targets.contains(entity.livingEntity.getUniqueId());
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
                    if(type!=StatTriggerType.onTick) logger.info("YOU HAVE A ITEM "+item.getItemMeta().displayName());

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
        list.addAll(getStats());
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
