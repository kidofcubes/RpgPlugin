# RpgPlugin
Minecraft [Paper](https://papermc.io) server RPG library for 1.19
(Paper only probably)

# Usage

drop the jar into your plugins folder, and run it as a javaagent when starting the server with the flag

`-javaagent:plugins/RpgPlugin-VERSION.jar`



# Example plugin

ExampleAddon.java
```java
public class ExampleAddon extends JavaPlugin {
    @Override
    public void onEnable(){
        // needs to be registered sorta like a listener
        RpgRegistry.register(new SmiteStat(), SmiteStat::new, List.of(EntityDamageByEntityEvent.class));
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
    public RpgObject getParent(Event event){
        if(((EntityDamageByEntityEvent)event).getDamager() instanceof LivingEntity livingEntity){
            return (RpgObject) livingEntity;
        }else{
            return null;
        }
    }

    @Override
    public void run(Event event){ //the code to be run when the stat is eligible to run
        EntityDamageByEntityEvent attackEvent = (EntityDamageByEntityEvent) event;
        attackEvent.getEntity().getLocation().getWorld().strikeLightning(attackEvent.getEntity().getLocation());
    }
    
    @Override
    public float getManaCost(){ //Set the mana cost of activating this stat
        return 5;
    }
}
```


plugin.yml
```yml
main: io.github.kidofcubes.ExampleAddon
name: ExampleAddon
version: 0.0.1
description: A example plugin
api-version: 1.19
depend: [RpgPlugin]
commands:
```

Gradle:

```groovy
repositories {
    //you will have to clone the repo and run publishToMavenLocal to get it in your local repo
    mavenLocal()
}
dependencies{
    implementation "io.github.kidofcubes:RpgPlugin:VERSION"
}
```

Now to add the SmiteStat to something, you use the addStat function on any LivingEntity or ItemStack
`((RpgEntity)livingEntity).addStat(new SmiteStat());`


`((RpgItem)itemStack).addStat(new SmiteStat());`

# Things

Stat:
    
    Can cost mana
    Listens for events
    Has a parent (where the stat is on) and a user (whats currently using the stat)

[//]: # (TimedStat:)

[//]: # ()
[//]: # (    A extension of Stat that gets called every `getInterval&#40;&#41;` ticks &#40;calls run with a null event&#41;)



# Other section

Alpha

<br>
<br>
<br>

Future plans: 

    Custom hit detection (custom range, attack hitbox size, etc)
    Less bugs

Other ideas:
    
    Custom world generation things
    Custom mob spawning
    
