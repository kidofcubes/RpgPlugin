# RpgPlugin
an opinionated (hopefully not slow) minecraft paper rpg plugin (named RpgPlugin) for 1.19.4

(i am looking for a new name for this thing)

## Usage

drop the jar into your plugins folder, and run it as a javaagent when starting the server with the flag

`-javaagent:plugins/RpgPlugin-VERSION.jar`



## Example plugin

ExampleAddon.java
```java
public class ExampleAddon extends JavaPlugin {
    @Override
    public void onEnable(){
        // needs to be registered sorta like a listener
        RpgRegistry.register(new SmiteStat(), SmiteStat::new, Map.of(EntityDamageByEntityEvent.class,EventPriority.NORMAL));
    }
}
```

SmiteStat.java
```java
public class SmiteStat extends Stat {

    @Override
    public NamespacedKey getIdentifier() {
        return new NamespacedKey("example_addon","smite_stat");
    }

    @Override
    public RpgObject getObject(Event event){
        if(((EntityDamageByEntityEvent)event).getDamager() instanceof LivingEntity livingEntity){
            return (RpgEntity) livingEntity; //the object on which to search for the stat to activate
        }else{
            return null; //cancels
        }
    }

    @Override
    public void onActivate(Event event){ //gets called when the stat gets activated
        EntityDamageByEntityEvent attackEvent = (EntityDamageByEntityEvent) event;
        attackEvent.getEntity().getLocation().getWorld().strikeLightning(attackEvent.getEntity().getLocation());
    }
    
    @Override
    public float getManaCost(){ //Set the mana cost of activating this stat
        return 5;
    }
}
```

Now to add the SmiteStat to something, you use the addStat function on any LivingEntity or ItemStack

`((RpgEntity)livingEntity).addStat(new SmiteStat());`


`((RpgItem)itemStack).addStat(new SmiteStat());`

## information

Stat:
    
    Can cost mana
    Listens for events
    Has a parent (where the stat is on) and a user (whats currently using the stat)

[//]: # (TimedStat:)

[//]: # ()
[//]: # (    A extension of Stat that gets called every `getInterval&#40;&#41;` ticks &#40;calls run with a null event&#41;)



## Other section

in development

todo:

    Custom hit detection (custom range, attack hitbox size, etc) //code already made, just havent put it in yet
    Less bugs
    Custom mob spawning
    packet entities
    
