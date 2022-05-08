package io.github.KidOfCubes;

import io.github.KidOfCubes.Types.StatTriggerType;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public abstract class Stat {
    public static String description;
    public int level;
    public RpgElement statParent;
    public static StatTriggerType triggerType=StatTriggerType.onActivate;
    public static String[] triggerStrings;
    public static StatType statType;
    public static int runPriority=0;

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

    public String getDescription() {
        return description;
    }
    public int getRunPriority(){
        return  runPriority;
    }
    public StatTriggerType getTriggerType() {
        return triggerType;
    }
    public StatType getStatType() {
        return statType;
    }
    public boolean inTriggerStrings(String triggerString) {
        return Arrays.asList(triggerStrings).contains(triggerString)||triggerString.equalsIgnoreCase(getName());
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
