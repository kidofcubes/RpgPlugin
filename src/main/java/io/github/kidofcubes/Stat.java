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

    private static final Map<String,String> emptyData = new HashMap<>();

    public RpgObject getParent() {
        return parent;
    }

    private RpgObject parent;
    public float getManaCost(){
        return 0;
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

    public Stat newInstance() {
        try {
            return (Stat) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
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
    }
    public void onRemoveStat(RpgObject object){
        parent=null;
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

            List<Stat> statInstances = toCheck.getEffectiveStatsMap().getOrDefault(this.getClass().getName(),null);
            if(statInstances!=null) {
                for (Stat statInstance : statInstances) {
                    float manaCost = statInstance.getManaCost();
                    if (manaCost == 0 || toCheck.getMana() >= manaCost) {
                        if (event instanceof RpgActivateStatEvent rpgActivateStatEvent) {
                            if (rpgActivateStatEvent.getTriggerStats().contains(getName())) {
                                toCheck.setMana(toCheck.getMana() - manaCost);
                                statInstance.run(event);
                            }
                        } else {
                            toCheck.setMana(toCheck.getMana() - manaCost);
                            statInstance.run(event);
                        }
                    }
                }
            }
        }
    }

    public EventPriority priority(){
        return EventPriority.NORMAL;
    }

    public abstract RpgObject checkObject(Event event);

    /**
     * @param event an event that's an instanceof one of the events you asked for
     */
    public abstract void run(Event event);

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
