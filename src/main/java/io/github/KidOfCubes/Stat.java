package io.github.KidOfCubes;


import io.github.KidOfCubes.Types.StatType;
import org.bukkit.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import static io.github.KidOfCubes.RpgPlugin.logger;

public abstract class Stat implements Listener {



    public static String description;

    public static StatType statType;

    public int level;

    //public static List<RpgElement> elementsWithStat = new ArrayList<RpgElement>();

    //override stats run slower i suppose?



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




    public void trigger(Event event){

        run(event);
    }
    public abstract void run(Event event);
    public abstract RpgElement elementToStatCheck(Event event);



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
