package io.github.KidOfCubes;

import io.github.KidOfCubes.Events.*;
import io.github.KidOfCubes.Types.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
    public void damage(DamageType type, double amount){ //AMOUNT IS FOR BASE AMOUNT
        RpgEntityDamageEvent event = new RpgEntityDamageEvent(this, type,amount);
        event.callEvent();
        livingEntity.damage(event.getTotalDamage());
    }

    public RpgEntityDamageEvent damage(DamageType type, double amount, RpgElement attacker){ //AMOUNT IS FOR BASE AMOUNT, WILL RUN STATS i think
        RpgEntityDamageEvent event = new RpgEntityDamageByElementEvent(this,type,amount, attacker);
        event.callEvent();
        livingEntity.damage(event.getTotalDamage());
        return event;
    }
    public RpgEntityHealEvent heal(double amount){
        RpgEntityHealEvent event = new RpgEntityHealEvent(this, amount);
        event.callEvent();
        livingEntity.setHealth(livingEntity.getHealth()+event.getAmount());
        return event;
    }
    public RpgEntityHealByElementEvent heal(double amount, @NotNull RpgElement healer){

        RpgEntityHealByElementEvent event = new RpgEntityHealByElementEvent(this, amount, healer);
        event.callEvent();
        livingEntity.setHealth(livingEntity.getHealth()+event.getAmount());
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


    List<String> effectiveStatsCache = new ArrayList<>();
    long effectiveStatsLastUpdate = 0;
    @Override
    public List<String> getEffectiveStats(){
        long now = System.currentTimeMillis();
        if(now-effectiveStatsLastUpdate>1000) {
            effectiveStatsLastUpdate = now;
            List<String> list = new ArrayList<>(getStats());
            if (livingEntity.getEquipment() != null) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack item = livingEntity.getEquipment().getItem(slot);
                    if (!isEmpty(item)) {
                        RpgItem temp = new RpgItem(item);
                        logger.info("length of effective stats is "+temp.getEffectiveStats().size());
                        list.addAll(temp.getEffectiveStats());
                    }
                }
            }
            Map<String, Integer> levels = new HashMap<>();
            for (String stat : list) {
                logger.info("i have a stat "+stat);
                levels.putIfAbsent(stat, -1);
/*                if (levels.get(stat.getName()).level < stat.level) {
                    levels.put(stat.getName(), stat);
                }*/
            }
            effectiveStatsCache = levels.keySet().stream().toList();
        }
        return effectiveStatsCache;

    }
}
