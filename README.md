# RpgPlugin
Minecraft [Paper](https://papermc.io) server RPG library for 1.19
(Paper only I believe)
# Development setup

Grab the latest jar from the [releases](https://github.com/KidOfCubes/RpgPlugin/releases/), drop it in your /libs folder, and add it as a compileOnly dependency.


Gradle:
```groovy
dependencies {
    compileOnly(files("${project.projectDir}/libs/RpgPlugin-VERSION.jar"))
}
```

Replace **VERSION** with the version of your downloaded jar.
(You will also need the plugin running on your server)

# Usage

Example plugin:

ExampleAddon.java
```java
public class ExampleAddon extends JavaPlugin {
    @Override
    public void onEnable(){
        StatManager.register(new SmiteStat(), List.of(RpgActivateStatEvent.class)); //has to be registered or else it will not work
    }
}
```

SmiteStat.java
```java
public class SmiteStat extends Stat {
	@Override
	public RpgObject elementToStatCheck(Event event){
		return ((RpgActivateStatEvent)event).getParent(); //Gets the object that has the stat thats being activated
	}

	@Override
	public void run(Event event){ //the code to be run when the stat is eligible to run
		RpgActivateStatEvent activateEvent = (RpgActivateStatEvent) event;
		if(activateEvent.getTarget() instanceof RpgEntity target) { //target could be item or entity
			target.livingEntity.getWorld().strikeLightningEffect(target.livingEntity.getLocation());
			target.damage(DamageType.Thunder, level * 3);
		}
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

Now to add the SmiteStat to something, you use the addStat function on RpgEntity or RpgItem like so: <br>
`RpgManager.getRpgEntity(livingEntity).addStat(new SmiteStat());`


`RpgManager.getRpgItem(itemStack).addStat(new SmiteStat());`

# Things
RpgManager:

    How you get RpgItems and RpgEntities from itemStacks and livingEntities 
    

Stat:
    
    Can cost mana
    Listens for events
    Has a parent (where the stat is on) and a user (whats currently using the stat)
TimedStat:

    A extension of Stat that gets called every `getInterval()` ticks (calls run with a null event)

RpgObject:

    Holds stats
    Can use other RpgObjects to use their stats
    Can have multiple RpgClasses

RpgEntity:

    Extension of RpgObject
    Linked to a LivingEntity

RpgItem:

    Extension of RpgObject
    Somewhat linked to a itemstack

RpgClass:
    
    Has stats that get added when this class is added



# Other section

Still in beta so there's probably some (hopefully not critical) bugs, It's still in active development though so if you made a GitHub issue I should be able to fix it relatively soon.

Ideas are also welcome (provided that they aren't too specific)

Some parts may drastically change over beta, so I do not recommend upgrading in production without testing.

Compatibility with other plugins should be okayish

I can be contacted on Discord at `KidOfCubes#4867` if you need or want to report anything
<br>
<br>
<br>

Future plans: (all going to be configurable hopefully)  

    Custom hit detection (custom range, attack hitbox size, etc)
    Less bugs

Other ideas:
    
    Custom world generation things
    Custom mob spawning
    
