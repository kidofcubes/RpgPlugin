package io.github.KidOfCubes;

import io.github.KidOfCubes.Types.StatTriggerType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpgEntity extends RpgElement{
    public LivingEntity livingEntity;
    public int level;
    public List<RpgEntity> targets = new ArrayList<>();

    public RpgEntity(LivingEntity livingEntity){
        this.livingEntity = livingEntity;
        level = 0;
    }
    public RpgEntity(LivingEntity livingEntity, int level){
        this.livingEntity = livingEntity;
        this.level = level;
    }

    public void addTarget(RpgEntity target){
        targets.add(target);
    }

    public void attemptActivateStats(StatTriggerType type, Event event){
        if(stats.containsKey(type)) {
            for (Stat stat :
                    stats.get(type)) {
                stat.trigger(this, event);
            }
        }

    }


}
