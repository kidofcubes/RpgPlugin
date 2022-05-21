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
import java.util.Objects;

import static io.github.KidOfCubes.RpgPlugin.logger;

public abstract class Stat{



    public static String description;

    public static StatType statType;
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

    public Stat(int level){
        this.level = level;
    }

    public static Stat fromText(String name, int level) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends Stat> stat = Class.forName("io.github.KidOfCubes.Stats."+name).asSubclass(Stat.class);
        return (Stat)stat.getConstructors()[0].newInstance(level);
    }




    public abstract boolean activateConditions(RpgActivateStatEvent event);

    public void trigger(RpgActivateStatEvent event){
        run(event);
    }

    protected abstract void run(RpgActivateStatEvent event);

    @Override
    public boolean equals(Object other){
        if (other == null) {
            return false;
        }

        if (other.getClass() != this.getClass()) {
            return false;
        }

        final Stat otherStat = (Stat) other;
        if (level!=otherStat.level) {
            return false;
        }
        if (!Objects.equals(getName(), otherStat.getName())) {
            logger.info("false");
            return false;
        }
        return true;
    }
}
