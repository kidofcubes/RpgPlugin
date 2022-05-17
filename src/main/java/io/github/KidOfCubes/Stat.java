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

public abstract class Stat{



    public static String description;

    public static StatType statType;
    public int level;
    public RpgElement statOwner;
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

    public Stat(int level, RpgElement statOwner){
        this.level = level;
        this.statOwner = statOwner;
    }




    public boolean activateConditions(RpgActivateStatEvent event){
        return event.getTriggerStats().contains(this);
    }

    public void trigger(RpgActivateStatEvent event){
        run(event);
    }

    protected abstract void run(RpgActivateStatEvent event);
}
