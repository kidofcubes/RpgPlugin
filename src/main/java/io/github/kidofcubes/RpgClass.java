package io.github.kidofcubes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RpgClass { //unneeded class maybe idk i dont like how instances dont do anything
    //i really do not know how to do this help
    //using classes for everything feels jank no cap because instances and stuff
    private int level;

    /**
     * Override this to change what stats get added to any object with this class
     * @return
     */
    public List<Stat> classStats(){
        return new ArrayList<>();
    }
    /**
     * Unused, just thought would be handy
     * @return
     */
    public List<RpgClass> requiredClasses(){
        return new ArrayList<>();
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

    public void setLevel(int level) {
        this.level = level;
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
