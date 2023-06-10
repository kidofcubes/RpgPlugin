package io.github.kidofcubes.rpgplugin;


import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Stat implements Listener {

    static final String LEVEL_KEY="lvl";
    final TagWrapper data;
    private final RPG parent;
    private RPG user;
    public RPG getParent() {
        return parent;
    }

    public Stat(RPG parent, TagWrapper data) {
        this.parent = parent;
        this.data = data;
    }
    public TagWrapper getData(){
        return data;
    }
    public Stat setUser(RPG rpgObject){
        user=rpgObject;
        return this;
    }
    
    /**
     * Default user is self
     * @return
     */
    public RPG getUser(){
        return user==null ? parent : user;
    }


    public double getManaCost(){
        return 0;
    }

    public String getName() {
        return this.getClass().getName();
    }

    public String getDescription() {
        return "Default description";
    }









    /**
     * Gets the unique identifier for this stat type.
     * ( Highly recommended to override with your own namespace to prevent name collisions )
     * @return A unique NamespacedKey for this type of stat
     */
    public NamespacedKey getIdentifier(){ return new NamespacedKey("rpgplugin",getClass().getSimpleName().replaceAll("\\$","_").toLowerCase());}
    //return new NamespacedKey(getPlugin(),getClass().getName().replaceAll("\\$","_").toLowerCase());
    //return new NamespacedKey(getClass().getPackage().getName().replaceAll("\\.","_").toLowerCase(),getClass().getName().replaceAll("\\$","_").toLowerCase());

    /**
     * Gets the level of this stat
     * default is 0
     * @return
     */
    public int getLevel() {
        return data.getInt(LEVEL_KEY);
    }


    public Stat setLevel(int level) {
        data.putInt(LEVEL_KEY,level);
        return this;
    }

    public void onAddStat(RPG object){}
    public void onRemoveStat(RPG object){}
    public void onUseStat(RpgEntity entity){
        user=entity;
    }
    public void onStopUsingStat(RpgEntity entity){}

    /**
     * Returns the RpgObject which will have its stat instance activated
     * @param event
     * @return
     */
    public RPG getObject(Event event){throw new NotImplementedException();}



    /**
     * Runs checks for event, and runs stat if passes
     * Checks are:
     *   stat on check object
     *   check object + user mana enough
     * @param event Event that was passed in
     */
    public void passEvent(Event event) {
        NamespacedKey identifier = getIdentifier();
        RPG toCheck = this.getObject(event);
        //get stats that depend this stat
        if(onEvent(event)) return;
        if (toCheck == null) return;
        Map<RPG,Stat> stats = new HashMap<>();
        toCheck.addUsedStats(identifier,stats);
        if(stats.size()==0) return;
        if(onTrigger(event,toCheck,stats)) return;
        for(Map.Entry<RPG,Stat> entry : stats.entrySet()){
            entry.getValue().setUser(toCheck);

            //todo new mana thing sometime
            double cost = entry.getValue().getManaCost();
            if (cost != 0.0) {
                if (toCheck.getMana() < cost) {
                    continue;
                }else{
                    toCheck.setMana(toCheck.getMana()-cost);
                }
            }
            entry.getValue().onActivate(event);
        }
    }

    /**
     * Gets called first when any of the listened events are called, before all checks (basically an event listener)
     * @param event
     * @return Whether to cancel
     */
    public boolean onEvent(Event event){return false;}


    /**
     * Gets called once for each event if the check object is using atleast 1 of this stat
     * @param event
     * @return Whether to cancel
     */

    public boolean onTrigger(Event event, @NotNull RPG checkObject, Map<RPG,Stat> instances){return false;}

    /**
     * Override this to run code when your stat is successfully activated
     *
     * @param event an event that's an instanceof one of the events you asked for
     */
    public void onActivate(Event event){};



}
