package io.github.kidofcubes;


import io.github.kidofcubes.events.RpgActivateStatEvent;
import io.github.kidofcubes.managers.StatManager;
import io.github.kidofcubes.types.StatType;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.github.kidofcubes.RpgPlugin.gson;

public abstract class Stat implements Listener {

    private int level = 0;

    private final Class<? extends Stat> runBeforeStat = runBeforeStat();

    public Class<? extends Stat> runBeforeStat() {
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
    public boolean manaSourceFromParent(){
        return false;
    }



    public String getName() {
        return this.getClass().getName();
    }

    public String getShortName() {
        return this.getClass().getSimpleName();
    }

    public String getDescription() {
        return "Default description";
    }

    public StatType getStatType() {
        return StatType.stat;
    }


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
        System.out.println("ON ADD STAT "+getName()+" ON "+object.getName());
    }
    public void onRemoveStat(RpgObject object){
        parent=null;
        System.out.println("ON REMOVE STAT "+getName()+" ON "+object.getName());
    }

    public void onUseStat(RpgObject object){
        user = object;
        System.out.println("ON USE STAT"+getName()+" ON "+object.getName());
    }
    public void onStopUsingStat(RpgObject object){
        user=null;
        System.out.println("ON STOP USE STAT"+getName()+" ON "+object.getName());
    }


    /**
     * Override this to save things
     * @return
     */
    public Map<String, String> saveCustomData() {return emptyData;}

    /**
     * Override this to run code when saved things are loaded
     * @param customData
     */
    public void loadCustomData(Map<String, String> customData) {}

    /**
     * Runs checks for event, and runs stat if passes
     * Checks are:
     *   stat on check object
     *   level!=0
     *   check object mana enough
     * @param event
     */
    public void trigger(Event event) {
        RpgObject toCheck = checkObject(event);
        if (toCheck != null) {

            List<Stat> statInstances = toCheck.getEffectiveStatsMap().getOrDefault(this.getClass(),null);
            if(statInstances!=null) {
                for (Stat statInstance : statInstances) {
                    if (event instanceof RpgActivateStatEvent rpgActivateStatEvent) {
                        if (!rpgActivateStatEvent.getTriggerStats().contains(getName())) {
                            continue;
                        }
                    }
                    //mana
                    double manaCost = statInstance.getManaCost();
                    if(manaCost == 0){
                        statInstance.activateStat(event);
                    }else{
                        if(manaSourceFromParent()){
                            if (statInstance.getParent().getMana() >= manaCost) {
                                statInstance.getParent().setMana(statInstance.getParent().getMana() - manaCost);


                                statInstance.activateStat(event);
                            }
                        }else{
                            if (statInstance.getUser().getMana() >= manaCost) {
                                statInstance.getUser().setMana(statInstance.getUser().getMana() - manaCost);


                                statInstance.activateStat(event);
                            }
                        }
                    }
                }
            }
        }
    }
    public void activateStat(Event event){
        ArrayList<Stat> runBeforeStats = new ArrayList<>();
        for (Map.Entry<Class<? extends Stat>,List<Stat>> pair : getUser().getEffectiveStatsMap().entrySet()) {
            if(pair.getValue().size()>0){
                if(pair.getValue().get(0).runBeforeStat!=null) {
                    if (this.getClass().isAssignableFrom(pair.getValue().get(0).runBeforeStat)) {
                        System.out.println("FOUND A "+pair.getValue().get(0).getName()+" FROM "+pair.getValue().get(0).getParent()+" USED BY "+pair.getValue().get(0).getUser()+" TO RUN BEFORE "+getName());
                        runBeforeStats.addAll(pair.getValue());
                    }
                }
            }
        }
        for (Stat stat : runBeforeStats) {
            stat.sourceStat=this;
            stat.activateStat(event);
        }
        run(event);
        sourceStat=null;
    }

    public Stat sourceStat = null;





    public EventPriority priority(){
        return EventPriority.NORMAL;
    }

    public RpgObject checkObject(Event event){return null;}

    /**
     * Override this to run code when your stat is activated
     * @param event an event that's an instanceof one of the events you asked for
     */
    public void run(Event event){}

    public void join(Stat stat){
        setLevel(stat.getLevel()+getLevel());
    }
    public void remove(Stat stat){
        setLevel(getLevel()-stat.getLevel());
    }


    public StatContainer asContainer(){
        StatContainer container = new StatContainer();
        container.level = level;
        container.customData = saveCustomData();
        return (container);
    }
    public void loadFromContainer(StatContainer container){
        level = container.level;
        loadCustomData(container.customData);
    }
    public static class StatContainer{
        public int level;
        public Map<String,String> customData;
    }


}
