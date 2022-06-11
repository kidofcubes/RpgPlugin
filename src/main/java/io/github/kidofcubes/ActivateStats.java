package io.github.kidofcubes;

import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ActivateStats {
    Map<Class<? extends Stat>, Stat> activationStats = new HashMap<>();

    default void addActivationStat(Stat stat){
        activationStats.put(stat.getClass(),stat);
    }
    default void addActivationStats(List<Stat> stats){
        System.out.println("ADDED ACTIVATION STATS");
        if(stats!=null) {
            for (Stat stat : stats) {
                addActivationStat(stat);
            }
        }
    }

    default void runActivationStats(Event event){
        for (Stat stat : activationStats.values()) {
            stat.run(event);
        }
    }
}
