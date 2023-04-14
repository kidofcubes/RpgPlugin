package io.github.kidofcubes.rpgplugin;


import net.minecraft.nbt.CompoundTag;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class Stat implements Listener {

    private int level = 0;

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

    @Nullable
    public NamespacedKey overrides(){
        return null;
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
    public RpgObject getObject(Event event){throw new NotImplementedException();}

    /**
     * Runs checks for event, and runs stat if passes
     * Checks are:
     *   stat on check object
     *   check object + user mana enough
     * @param event Event that was passed in
     */
    public void passEvent(Event event) {
        NamespacedKey identifier = getIdentifier();
        RpgObject toCheck = this.getObject(event);
        //get stats that depend this stat
        onEvent(event);
        if (toCheck == null) return;
        List<Stat> instances = toCheck.getUsedStatsMap().get(identifier);
        if(instances==null) return;
        if(instances.size()==0) return;
        if(!onTrigger(event,toCheck,instances)) return;

        for(Stat stat: instances){
            //todo new mana thing sometime
            double cost = stat.getManaCost();
            if (cost != 0.0) {
                if (stat.getUser().getMana()  < cost) {
                    continue;
                }else{
                    stat.getUser().setMana(stat.getUser().getMana()-cost);
                }
            }
            stat.onActivate(event);
        }


    }

    private boolean triggerEvent(Event event, @Nullable Stat caller){
        NamespacedKey[] toRun = RpgRegistry.getStatModifiers(getIdentifier(),StatModifierType.RUN_BEFORE);

        boolean cancelled = false;
        if(toRun!=null){
            for(int i=0;i<toRun.length;i++) {
                cancelled|=RpgRegistry.getStatInstance(toRun[i]).triggerEvent(event, this);
            }
        }
        if(cancelled) return true;

        toRun = RpgRegistry.getStatModifiers(getIdentifier(),StatModifierType.OVERRIDE);
        if(toRun!=null){
            if(toRun.length>0) cancelled|=RpgRegistry.getStatInstance(toRun[0]).triggerEvent(event,this);
            else onEvent(event);
        }else onEvent(event);

        if(cancelled) return true;
        toRun = RpgRegistry.getStatModifiers(getIdentifier(),StatModifierType.RUN_AFTER);
        if(toRun!=null){
            for(int i=0;i<toRun.length;i++) {
                cancelled|=RpgRegistry.getStatInstance(toRun[i]).triggerEvent(event, this);
            }
        }
        if(cancelled) return true;
        return false;
    }

    private boolean triggerTriggers(@NotNull Event event, @NotNull RpgObject toCheck, @NotNull Map<NamespacedKey,List<Stat>> usedStats, @Nullable Stat caller){
        if (toCheck == null) return false;
        NamespacedKey identifier = getIdentifier();
        List<Stat> instances = usedStats.get(identifier);
        if(instances==null||instances.size()==0) return false;

        boolean cancelled = false;

        NamespacedKey[] toRun = RpgRegistry.getStatModifiers(getIdentifier(),StatModifierType.RUN_BEFORE);
        if(toRun!=null){
            for(int i=0;i<toRun.length;i++) {
                if(usedStats.get(identifier)==null||usedStats.get(identifier).size()==0) continue;
                cancelled|=RpgRegistry.getStatInstance(toRun[i]).triggerTriggers(event,toCheck,usedStats,this);
            }
        }
        if(cancelled) return true;

        toRun = RpgRegistry.getStatModifiers(getIdentifier(),StatModifierType.OVERRIDE);
        if(toRun!=null){
            boolean found=false;
            for(int i=0;i<toRun.length;i++) {
                if(usedStats.get(identifier)==null||usedStats.get(identifier).size()==0) continue; //search thru overrides for one that is on the tocheck
                cancelled|=RpgRegistry.getStatInstance(toRun[i]).triggerTriggers(event,toCheck,usedStats,this);
                found=true;
                break;
            }
            if(!found) onTrigger(event, toCheck, instances);
        }else onTrigger(event, toCheck, instances);
        if(cancelled) return true;

        toRun = RpgRegistry.getStatModifiers(getIdentifier(),StatModifierType.RUN_AFTER);
        if(toRun!=null){
            for(int i=0;i<toRun.length;i++) {
                if(usedStats.get(identifier)==null||usedStats.get(identifier).size()==0) continue;
                cancelled|=RpgRegistry.getStatInstance(toRun[i]).triggerTriggers(event,toCheck,usedStats,this);
            }
        }
        if(!cancelled) return true;
        return false;
    }

    private boolean triggerRun(Event event, RpgObject toCheck, Map<NamespacedKey,List<Stat>> usedStats, @Nullable Stat caller){
        if (toCheck == null) return false;
        NamespacedKey identifier = getIdentifier();
        List<Stat> instances = usedStats.get(identifier);
        if(instances==null||instances.size()==0) return false;
        for(Stat stat : instances) {
            double cost = stat.getManaCost();
            if (cost != 0.0) {
                if (stat.getUser().getMana() >= cost) stat.getUser().setMana(stat.getUser().getMana() - cost);
                else continue;
            }

            stat.onActivate(event);
        }

        return false;
    }
    public enum StatModifierType{
        RUN_BEFORE,
        OVERRIDE,
        RUN_AFTER
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

    public boolean onTrigger(Event event, @NotNull RpgObject checkObject, List<Stat> instances){return false;}


    public EventPriority priority(){
        return EventPriority.NORMAL;
    }

    /**
     * Override this to run code when your stat is successfully activated
     *
     * @param event an event that's an instanceof one of the events you asked for
     */
    public void onActivate(Event event){};

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
     * @return a tag
     */
    public CompoundTag asTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("lvl",getLevel());
        return tag;
    }

    /**
     * Override this to load stat json
     * @param data
     */
    public void loadTag(CompoundTag data) {level = data.getInt("lvl");}



}
