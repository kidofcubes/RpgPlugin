package io.github.KidOfCubes;

import io.github.KidOfCubes.Events.RpgActivateStatEvent;
import io.github.KidOfCubes.Types.StatRequireType;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Stat implements EventExecutor, Listener{

    public static Plugin plugin;


    public static String description;

    public static List<String> triggerStrings = new ArrayList<String>();
    public static StatType statType;
    public static final EventPriority runPriority=EventPriority.NORMAL;
    public static final StatRequireType requirement = StatRequireType.Parent;
    public int level;

    public static boolean sameThread = true;



    public String getName(){
        return this.getClass().getSimpleName();
    }

    public String getDescription() {
        return description;
    }
    public StatType getStatType() {
        return statType;
    }
    public boolean inTriggerStrings(String triggerString) {
        return triggerStrings.contains("ANY")||(triggerStrings).contains(triggerString)||triggerString.equalsIgnoreCase(getName());
    }
    public static void register(Plugin _plugin){
        plugin = _plugin;
    }
    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
        if(event instanceof RpgActivateStatEvent rpgActivateStatEvent){
            onActivateStat(rpgActivateStatEvent);
        }
    }

    public Stat(int level){
        this.level = level;
        if(plugin!=null) {
            RpgActivateStatEvent.getHandlerList().register(new RegisteredListener(this, this, runPriority, plugin, false));
        }
    }








    @EventHandler
    public void onActivateStat(RpgActivateStatEvent event){
        if(inTriggerStrings(event.getTriggerString())){
            if(sameThread) {
                if(requirement==StatRequireType.Parent){
                    if(event.getParent().hasStat(event.getTriggerString())){
                        run(event);
                    }
                }else{
                    if(event.getTarget().hasStat(event.getTriggerString())){
                        run(event);
                    }
                }
            }else{
                Thread thread = new Thread(() -> {

                    if(requirement==StatRequireType.Parent){
                        if(event.getParent().hasStat(event.getTriggerString())){
                            run(event);
                        }
                    }else{
                        if(event.getTarget().hasStat(event.getTriggerString())){
                            run(event);
                        }
                    }
                });

                thread.start();
            }
        }
    }

    protected abstract void run(RpgActivateStatEvent event);
}
