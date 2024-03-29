package io.github.kidofcubes;

import java.util.ArrayList;
import java.util.List;

public abstract class RpgClass {
    private int level;
    private final static List<Stat> emptyStats = new ArrayList<>();
    private final static List<Class<? extends RpgClass>> emptyClasses = new ArrayList<>();

    /**
     * Override this to change what stats get added to any object with this class
     * @return
     */
    public List<Stat> classStats(){
        return emptyStats;
    }
    /**
     * Unused, just thought would be handy
     * @return
     */
    public List<Class<? extends RpgClass>> requiredClasses(){
        return emptyClasses;
    }


    public String getShortName() {
        return getClass().getSimpleName();
    }
    public String getFullName() {
        return getClass().getName();
    }



    public int getLevel() {
        return level;
    }

    public RpgClass setLevel(int level) {
        this.level = level;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RpgClass rpgClass){
            return rpgClass.getFullName().equals(getFullName());
        }else{
            if(obj instanceof String string){
                return string.equals(getFullName());
            }
        }
        return false;
    }
}
