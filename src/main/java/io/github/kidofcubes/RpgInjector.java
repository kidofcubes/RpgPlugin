package io.github.kidofcubes;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.namedIgnoreCase;

public class RpgInjector implements Listener {
    public void init(){

    }
    @EventHandler
    private void onEntityLoad(ChunkLoadEvent event) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        for(Entity entity : event.getChunk().getEntities()){
            if(entity instanceof CraftLivingEntity livingEntity){
                rpgify(livingEntity);
//                livingEntity.getHandle().load(livingEntity.getHandle().saveWithoutId(new CompoundTag()));
                livingEntity.getHandle().restoreFrom(livingEntity.getHandle());
            }
        }
    }
    @EventHandler
    private void onEntitySpawn(EntitySpawnEvent event) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        if(event.getEntity() instanceof CraftLivingEntity livingEntity){
            rpgify(livingEntity);
            System.out.println("SPAWNED");
        }
    }
    @EventHandler
    private void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof RpgEntity rpgEntity){
            System.out.println("A RPGENTITY GOT DAMAGED"+event.getEntity().getName());
        }else{
            System.out.println("A NON RPGENTITY GOT DAMAGED"+event.getEntity().getName());
        }
    }


    public void rpgify(CraftLivingEntity livingEntity) throws NoSuchFieldException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(livingEntity instanceof RpgEntity){
            return;
        }

        BaseRpgEntity instance = new BaseRpgEntity((CraftServer) livingEntity.getServer(),livingEntity.getHandle());
        DynamicType.Builder<? extends CraftLivingEntity> thing = new ByteBuddy()
                .subclass(livingEntity.getClass());

        //theres a better way to do this, but its 2 am
        //i cannot stress enough how much i dont like this solution
        for (Method method : BaseRpgEntity.class.getDeclaredMethods()) {
            thing=thing.method(namedIgnoreCase(method.getName())).intercept(MethodDelegation.to(instance));
        }
        thing=thing.implement(RpgEntity.class).intercept(MethodDelegation.to(instance));
        Class<? extends CraftLivingEntity> newClass = thing.make().load(getClass().getClassLoader()).getLoaded();

        Field field = net.minecraft.world.entity.Entity.class.getDeclaredField("bukkitEntity");
        field.setAccessible(true); // Force to access the field
        field.set(livingEntity.getHandle(),
                newClass.getDeclaredConstructors()[0].newInstance(
                        livingEntity.getServer(),
                        livingEntity.getHandle().getClass().cast(livingEntity.getHandle())
                ));
    }
}
