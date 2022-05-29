package io.github.KidOfCubes;


import io.github.KidOfCubes.Managers.StatManager;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.*;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static io.github.KidOfCubes.RpgPlugin.logger;

public abstract class Stat implements Listener {

    public static String description;

    public static StatType statType;


    public String getName(){
        return this.getClass().getName();
    }
    public String getShortName(){
        return this.getClass().getSimpleName();
    }

    public String getDescription() {
        return description;
    }
    public StatType getStatType() {
        return statType;
    }

    @Nullable
    public static Class<? extends Stat> fromText(String name){
        for (Class<? extends Stat> stat : StatManager.getRegisteredStats()) {
            if(stat.getSimpleName().equalsIgnoreCase(name)){
                return stat;
            }
        }
        return null;
    }




    public void trigger(Event event, int level){
        run(event, level);
    }

    public abstract RpgObject elementToStatCheck(Event event);

    /**
     *
     * @param event an event that's an instanceof one of the events you asked for
     * @param level the stat level, for things like more damage on sharpness 5 than sharpness 1 (will not give 0)
     */
    public abstract void run(Event event, int level);



    @Override
    public boolean equals(Object other){
        if (other == null) {
            return false;
        }

        if (other.getClass() != this.getClass()) {
            return false;
        }

        final Stat otherStat = (Stat) other;
        if (!Objects.equals(getName(), otherStat.getName())) {
            return false;
        }
        return true;
    }
}
