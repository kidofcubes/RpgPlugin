//package io.github.kidofcubes.stats;
//
//import io.github.kidofcubes.RpgObject;
//import io.github.kidofcubes.Stat;
//import io.github.kidofcubes.types.DamageType;
//import org.bukkit.event.Event;
//import org.bukkit.event.EventPriority;
//
//public class DamageModifier extends Stat {
//    boolean attacker = true;
//    float multiplier=1;
//    float addend=0;
//    public DamageModifier(){}
//    public DamageModifier(float multiplier,float addend, boolean attacker){
//        this.multiplier = multiplier;
//        this.addend = addend;
//        this.attacker = attacker;
//    }
//
//    @Override
//    public RpgObject checkObject(Event event) {
//        if(event instanceof RpgEntityDamageByObjectEvent damageByObjectEvent){
//            if(attacker){
//                return damageByObjectEvent.getCause();
//            }
//        }else{
//            return ((RpgEntityDamageEvent)event).getEntity();
//        }
//        return null;
//    }
//
//    /**
//     * @param event an event that's an instanceof one of the events you asked for
//     */
//    @Override
//    public void run(Event event) {
//        RpgEntityDamageEvent rpgEntityDamageEvent = ((RpgEntityDamageEvent)event);
//        for (DamageType damageType :
//                DamageType.values) {
//            rpgEntityDamageEvent.multiplyDamage(damageType,multiplier);
//            rpgEntityDamageEvent.addDamage(damageType,addend);
//        }
//    }
//
//    @Override
//    public EventPriority priority() {
//        return EventPriority.HIGH;
//    }
//}
