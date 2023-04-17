package io.github.kidofcubes.rpgplugin;


import it.unimi.dsi.fastutil.Pair;
import net.minecraft.nbt.CompoundTag;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.function.ToBooleanBiFunction;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Triplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.*;

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

//    /**
//     *
//     * @param <T> argument type
//     * @param <R> return type
//     */
//    public static class ModifiableFunction<T,R>{
//        public ModifiableFunction(BiFunction<T,R,Pair<R,Boolean>> original){
//            overrides.add(original);
//        }
//        private final ArrayList<BiFunction<T,R,Pair<R,Boolean>>> overrides = new ArrayList<>();
//        public R run(T arg){
//            R returnValue = null;
//            for(int i = overrides.size()-1;i>-1;i--){
//                Pair<R,Boolean> out = overrides.get(i).apply(arg,returnValue);
//                returnValue = out.left();
//                if(out.second()) break;
//            }
//            return returnValue;
//        }
//    }








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
        if(onEvent(event)) return;
        if (toCheck == null) return;
        List<Stat> instances = toCheck.getUsedStatsMap().get(identifier);
        if(instances==null) return;
        if(instances.size()==0) return;
        if(onTrigger(event,toCheck,instances)) return;

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
//            stat.onActivate(event);
            onActivate(event);
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
