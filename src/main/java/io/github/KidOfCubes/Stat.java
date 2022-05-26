package io.github.KidOfCubes;


import io.github.KidOfCubes.Managers.StatManager;
import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import static io.github.KidOfCubes.RpgPlugin.logger;

public abstract class Stat implements Listener {


    private boolean initalized=false;
    private Stat instance;

    public static String description;

    public static StatType statType;

    public int level;

    //public static List<RpgElement> elementsWithStat = new ArrayList<RpgElement>();

    //override stats run slower i suppose?

    public Stat(){

    }

    public String getName(){
        return this.getClass().getName();
    }

    public String getDescription() {
        return description;
    }
    public StatType getStatType() {
        return statType;
    }

    public static Class<? extends Stat> fromText(String name){
        for (Class<? extends Stat> stat : StatManager.getRegisteredStats()) {
            logger.info("checking if "+stat.getSimpleName() + " the same as "+name);
            if(stat.getSimpleName().equalsIgnoreCase(name)){
                return stat;
            }
        }
        return null;
    }




    public void trigger(Event event){
        run(event);
    }
    public abstract RpgElement elementToStatCheck(Event event);
    public abstract void run(Event event);



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
