package io.github.KidOfCubes;

import io.github.KidOfCubes.Types.StatTriggerType;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public abstract class Stat {
    public static String description;
    public int level;
    public RpgElement statParent;
    public static StatTriggerType triggerType;
    public static StatType statType;

    public static boolean sameThread = true;

    public Stat(int level, RpgElement statParent/*,StatTriggerType triggerType,boolean useSameThread*/){
        this.level = level;
        this.statParent = statParent;
/*        this.triggerType = triggerType;
        this.sameThread = useSameThread;*/
    }


    public String getName(){
        return this.getClass().getSimpleName();
    }


    public void trigger(RpgElement statParent, RpgElement caller, Event event){
        trigger(statParent,caller,-1,event);
    }
    public void trigger(RpgElement statParent, RpgElement caller, int level, Event event){
        if(statParent==null){
            statParent = this.statParent;
        }
        if(caller==null){
            caller = this.statParent;
        }
        if(level<1){
            level = this.level;
        }
        if(sameThread) {
            run(statParent, caller, level, event);
        }else{
            RpgElement finalStatParent = statParent;
            RpgElement finalCaller = caller;
            int finalLevel = level;
            Thread thread = new Thread(() -> {
                run(finalStatParent, finalCaller, finalLevel, event);
            });

            thread.start();
        }
    }

    protected abstract void run(RpgElement statParent, RpgElement caller, int level, Event event);
}
