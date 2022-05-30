# RpgPlugin
framework for rpg
insert documentation on how 2


example addon:

ExampleAddon.java
```java
public class ExampleAddon extends JavaPlugin {
    @Override
    public void onEnable(){
        StatManager.register(new SmiteStat(), List.of(RpgActivateStatEvent.class));
    }
}
```

SmiteStat.java
```java
public class SmiteStat extends Stat {
	@Override
	public RpgObject elementToStatCheck(Event event){
		return ((RpgActivateStatEvent)event).getParent(); //Gets the object that has the stat being activated
	}

	@Override
	public void run(Event event, int level){
		RpgActivateStatEvent activateEvent = (RpgActivateStatEvent) event;
		if(activateEvent.getTarget() instanceof RpgEntity target) { //target could be item or entity
			target.livingEntity.getWorld().strikeLightningEffect(target.livingEntity.getLocation());
			target.damage(DamageType.Thunder, level * 3);
		}
	 }
}
```

add the latest release jar as a dependency in your gradle/maven/smth
also put the same jar as a plugin
(i think this only supports paper idk havent tested)
leave me a dm on discord if something KidOfCubes#4867




