//package io.github.kidofcubes;
//
//import org.bukkit.event.Event;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//
//public abstract class TimedStat extends Stat{
//    static final Map<TimedStat,Integer> statInstances = new HashMap<>(); //key stat, value count
//
//    /**
//     * Override this if you want to support events too
//     * @param event
//     * @return
//     */
//    @Override
//    public RpgObject getParent(Event event) {
//        return null;
//    }
//
//    @Override
//    public void onAddStat(RpgObject object) {
//        super.onAddStat(object);
//        statInstances.put(this,0);
//    }
//
//    @Override
//    public void onRemoveStat() {
//        super.onRemoveStat();
//        statInstances.remove(this);
//    }
//
//    @Override
//    public void onUseStat(RpgObject object) {
//        super.onUseStat(object);
//    }
//
//    @Override
//    public void onStopUsingStat() {
//        super.onStopUsingStat();
//    }
//
//    public int getInterval(){
//        return 1;
//    }
//
//    /**
//     * Runs checks for event, and runs stat if passes
//     * Checks are:
//     * stat on check object
//     * level!=0
//     * check object mana enough
//     *
//     * @param event
//     */
//    @Override
//    public void trigger(Event event) {
//        if(event==null) { //check if timed event
//            //if(count>=this.getInterval()){
//            //    count=0;
//            HashSet<Map.Entry<TimedStat,Integer>> entries = new HashSet<>(statInstances.entrySet());
//            for (Map.Entry<TimedStat,Integer> entry: entries) {
//                entry.setValue(entry.getValue()+1);
//                TimedStat statInstance = entry.getKey();
//                if(entry.getValue()>=statInstance.getInterval()){
//                    entry.setValue(0);
//                    double manaCost = statInstance.getManaCost();
//                    if(manaCost == 0){
//                        statInstance.activateStat(null);
//                    }else{
//                        double cost=statInstance.getManaCost();
//                        if(statInstance.getParent().getMana()+(statInstance.getParent()==statInstance.getUser() ? 0 : statInstance.getUser().getMana())<cost){
//                            return;
//                        }
//                        cost-=statInstance.getParent().getMana();
//                        statInstance.getParent().setMana(Math.max(statInstance.getParent().getMana()-statInstance.getManaCost(),0));
//                        if(cost>0){
//                            statInstance.getUser().setMana(statInstance.getUser().getMana()-cost);
//                        }
//                        statInstance.activateStat(null);
//                    }
//                }
//
//            }
//            //}
//        }else{
//            super.trigger(event);
//        }
//    }
//}
