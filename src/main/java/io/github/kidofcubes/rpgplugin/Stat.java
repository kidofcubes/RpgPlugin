package io.github.kidofcubes.rpgplugin;


import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Stat implements Listener {

    private int level = 0;

    public Class<? extends Stat> dependentStat() {
        return null;
    }

    private static final Map<String,String> emptyData = new HashMap<>();

    public RpgObject getParent() {
        return parent;
    }
    public RpgObject getUser() {
        return user;
    }

    private RpgObject parent;
    private RpgObject user;
    public double getManaCost(){
        return 0;
    }



    public String getName() {
        return this.getClass().getName();
    }

    public String getDescription() {
        return "Default description";
    }

    public abstract NamespacedKey getIdentifier();
    //return new NamespacedKey(getPlugin(),getClass().getName().replaceAll("\\$","_").toLowerCase());
    //return new NamespacedKey(getClass().getPackage().getName().replaceAll("\\.","_").toLowerCase(),getClass().getName().replaceAll("\\$","_").toLowerCase());

    /**
     * Gets the level of this stat
     * default is 0
     * @return
     */
    public int getLevel() {
        return level;
    }


    public Stat setLevel(int level) {
        this.level = level;
        return this;
    }

    public void onAddStat(RpgObject object){
        parent = object;
    }
    public void onRemoveStat(){
        parent=null;
    }

    public void onUseStat(RpgObject object){
        setUser(object);
    }
    public void onStopUsingStat(){
        user=null;
    }

    /**
     * Returns the RpgObject which will have its stat instance activated
     * @param event
     * @return
     */
    public abstract RpgObject getParent(Event event);

    /**
     * Runs checks for event, and runs stat if passes
     * Checks are:
     *   stat on check object
     *   check object + user mana enough
     * @param event Event that was passed in
     */
    public void trigger(Event event) {
        RpgObject toCheck = this.getParent(event);
        if (toCheck != null) {
            Map<NamespacedKey, List<Stat>> map = toCheck.getUsedStatsMap();
            if(map.get(getIdentifier())!=null){
                for(Stat stat: map.get(getIdentifier())){
                    double cost = stat.getManaCost();
                    if (cost != 0.0) {
                        if (stat.getParent().getMana() + (toCheck == stat.getUser() ? 0.0 : stat.getUser().getMana()) < cost) {
                            return;
                        }

                        cost -= stat.getParent().getMana();
                        toCheck.setMana(Math.max(toCheck.getMana() - stat.getManaCost(), 0.0));
                        if (cost > 0.0) {
                            stat.getUser().setMana(stat.getUser().getMana() - cost);
                        }
                    }

                    stat.activateStat(event);
                }
            }
        }
    }
    public void activateStat(Event event){
        run(event);
    }


    public EventPriority priority(){
        return EventPriority.NORMAL;
    }

    /**
     * Override this to run code when your stat is successfully activated
     *
     * @param event an event that's an instanceof one of the events you asked for
     */
    public abstract void run(Event event);

    public void join(Stat stat){
        setLevel(stat.getLevel()+getLevel());
    }
    public void remove(Stat stat){
        setLevel(getLevel()-stat.getLevel());
    }

    public void setUser(RpgObject rpgObject){
        user=rpgObject;
    }


    /**
     * Override this to save things
     * @return The data to save
     */
    public JsonObject serialize() {return new JsonObject();}

    /**
     * Override this to load stat json
     * @param savedData
     */
    public void loadCustomData(JsonObject savedData) {}


    public StatContainer asContainer(){
        StatContainer container = new StatContainer();
        container.level = level;
        container.customData = serialize();
        return (container);
    }
    public static class StatContainer{
        public int level;
        public JsonObject customData;
    }


}
