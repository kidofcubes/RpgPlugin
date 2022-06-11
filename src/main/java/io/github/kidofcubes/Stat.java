package io.github.kidofcubes;


import io.github.kidofcubes.events.RpgActivateStatEvent;
import io.github.kidofcubes.managers.StatManager;
import io.github.kidofcubes.types.StatType;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.github.kidofcubes.RpgPlugin.gson;

public abstract class Stat implements Listener {


    protected static String description = "Default description";

    protected static StatType statType = StatType.stat;

    private static Map<String,String> emptyData = new HashMap<>();

    private int level = 0;

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
        return description;
    }

    public StatType getStatType() {
        return statType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
            Stat statInstance = toCheck.getEffectiveStatsMap().getOrDefault(this.getClass().getName(),null);
            if(statInstance!=null) {
                if (toCheck.getMana() >= getManaCost() || getManaCost() == 0) {
                    if (event instanceof RpgActivateStatEvent rpgActivateStatEvent) {
                        if (rpgActivateStatEvent.getTriggerStats().contains(getName())) {
                            statInstance.run(event);
                        }
                    } else {
                        statInstance.run(event);
                        toCheck.setMana(toCheck.getMana() - getManaCost());
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
