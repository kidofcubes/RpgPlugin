package io.github.KidOfCubes;

import io.github.KidOfCubes.Types.StatTriggerType;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public abstract class Stat {
    public static String name;
    public int level;
    public RpgElement statParent;
    public static StatTriggerType triggerType;

    public static boolean sameThread = true;

    public Stat(String name,int level,StatTriggerType triggerType,RpgElement statParent,boolean useSameThread){
        this.name = name;
        this.level = level;
        this.triggerType = triggerType;
        this.statParent = statParent;
        this.sameThread = useSameThread;
    }



    public void trigger(RpgElement caller){
        trigger(caller,level,null);
    }
    public void trigger(RpgElement caller, Event event){
        trigger(caller,level,event);
    }
    public void trigger(RpgElement caller, int level){
        trigger(caller,level,null);
    }
    private void trigger(RpgElement caller, int level, Event event) {
        if(sameThread) {
            run(caller,level,event);
        }else{
            Thread t = new Thread(() -> run(caller,level,null));
            t.start();
        }
    }
    protected abstract void run(RpgElement caller, int level, Event event);
}
