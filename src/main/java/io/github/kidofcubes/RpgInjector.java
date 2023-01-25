package io.github.kidofcubes;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.pool.TypePool;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.apache.commons.io.FileUtils.getFile;

public class RpgInjector implements Listener {
    public void init(){
        rpgify();
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

    static Field ItemMetaField;
    static {
        try {
            ItemMetaField = ItemStack.class.getDeclaredField("meta");
            ItemMetaField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    @EventHandler
    private void onEntitySpawn(EntitySpawnEvent event) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        if(event.getEntity() instanceof CraftLivingEntity livingEntity){
            rpgify(livingEntity);
        }
    }
    public void rpgify() {
//        net.minecraft.world.item.ItemStack
//        ItemStack itemStackTest = new ItemStack(Material.DIAMOND_AXE);
//        TypePool.Default.ofBootLoader()
//        org.bukkit.plugin.java.PluginClassLoader
//        ByteBuddyAgent.install();
//        CraftItemStack
        System.out.println("my classLoaoder is " + getClass().getClassLoader());
        System.out.println("my classLoaoder parent is " + getClass().getClassLoader().getParent());
        System.out.println("its classLoaoder is " + org.bukkit.Bukkit.class.getClassLoader());
        System.out.println("its classLoaoder parent is " + org.bukkit.Bukkit.class.getClassLoader().getParent());
        System.out.println("its classLoaoder2 is " + net.minecraft.world.level.block.DispenserBlock.class.getClassLoader());
        System.out.println("its classLoaoder2 parent is " + net.minecraft.world.level.block.DispenserBlock.class.getClassLoader().getParent());
        System.out.println("its classLoaoder3 is " + net.minecraft.world.level.block.entity.DispenserBlockEntity.class.getClassLoader());
        System.out.println("its classLoaoder3 parent is " + net.minecraft.world.level.block.entity.DispenserBlockEntity.class.getClassLoader().getParent());
//        TypeDescription type = TypePool.Default.of(ClassFileLocator.ForClassLoader.of(org.bukkit.Bukkit.class.getClassLoader())).describe("org.bukkit.v1_19_R2.inventory.CraftItemStack").resolve();
        ClassLoader classLoader = org.bukkit.craftbukkit.v1_19_R2.CraftServer.class.getClassLoader();
        TypePool typePool = TypePool.Default.of(ClassFileLocator.ForClassLoader.of(classLoader));

        System.out.println("the server classloader is "+classLoader);
        System.out.println("the server classloader parent is "+classLoader.getParent());
//        try {
////            classLoader.loadClass("inventory.CraftItemStack");
////            classLoader.loadClass("org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }


        // install agent
        // byte-buddy redefinition code above

//        String thing = "org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack";

//        try {
//            Field f = ClassLoader.class.getDeclaredField("classes");
//            f.setAccessible(true);
//
//            ClassLoader classLoader2 = RpgPlugin.class.getClassLoader().getParent();
//            ConcurrentHashMap<?, ?> classes =  (ConcurrentHashMap<?, ?>) f.get(classLoader2);
//            for (Map.Entry<?,?> entry: classes.entrySet() ) {
//                System.out.println("class loaded: "+((String)entry.getKey())+" and "+((Class<?>)entry.getValue()).getName() );
//            }
//
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }

        System.out.println("class loader of testcmd "+TestCommand.class.getClassLoader());
        System.out.println("class loader of testcmd parent "+TestCommand.class.getClassLoader().getParent());
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        ;
        for(URL url : ((URLClassLoader) Bukkit.class.getClassLoader()).getURLs()){
            System.out.println("A URL IS "+url);
        }
//            System.out.println(connection.getJarFile().getName());
//            System.out.println(connection.getJarFileURL());
//            System.out.println(connection.getURL());
        //        String thing = "org.bukkit.inventory.ItemStack";
//        ByteBuddyAgent.install();
//        new ByteBuddy().redefine(CraftItemStack.class, ClassFileLocator.ForClassLoader.of(org.bukkit.Bukkit.class.getClassLoader()))
////                .method(named("getMaxStackSize"))
////                .intercept(FixedValue.value(9))
//                .method(named("getItemMeta"))
////                .intercept(MethodDelegation.to(BaseRpgItem.class))
//                .intercept(FixedValue.nullValue())
//
////                .implement(RpgItem.class)
////                .intercept(MethodCall.invokeSelf().on(new BaseRpgItem()).withAllArguments())
//                .name(CraftItemStack.class.getName())
//                .make()
//                .load(org.bukkit.Bukkit.class.getClassLoader(),
//                        ClassReloadingStrategy.fromInstalledAgent());
//        new ByteBuddy().redefine(ItemStack.class).method(ElementMatchers.named("load"))
//                .intercept(MethodDelegation.to(BaseRpgItem.class))
//                .name(ItemStack.class.getName())
//                .make()
//                .load(ItemStack.class.getClassLoader(),ClassReloadingStrategy.fromInstalledAgent());
//        try {
//            URL url = new File(RpgPlugin.class.getProtectionDomain().getCodeSource().getLocation()
//                    .toURI()).toURI().toURL();
//            System.out.println("THE URL IS "+url.toString());
////            ByteBuddyAgent.getInstrumentation().appendToBootstrapClassLoaderSearch(new JarFile(new File(RpgPlugin.class.getProtectionDomain().getCodeSource().getLocation().toURI())));
//            ByteBuddyAgent.getInstrumentation().appendToSystemClassLoaderSearch(new JarFile(new File(RpgPlugin.class.getProtectionDomain().getCodeSource().getLocation().toURI())));
//        } catch (URISyntaxException |
//                 IOException e) {
//            throw new RuntimeException(e);
//        }
//        new ByteBuddy()
//                .redefine(typePool.describe(thing).resolve(), ClassFileLocator.ForClassLoader.of(classLoader))
////                .redefine(CraftItemStack.class, ClassFileLocator.ForClassLoader.of(org.bukkit.Bukkit.class.getClassLoader()))
//                .method(ElementMatchers.nameContains("getMaxStackSize"))
//                .intercept(FixedValue.value(9))
////                .method(ElementMatchers.named("serialize"))
////                .intercept(MethodDelegation.withDefaultConfiguration().to(new ItemStackTest()))
////                .intercept(MethodDelegation.to(new ItemStackTest()))
//                .name(thing)
//                .make()
//                .load(TestCommand.class.getClassLoader().getParent(), ClassReloadingStrategy.fromInstalledAgent());
//        System.out.println("SECOND THING IS "+new ItemStackTest().getMaxStackSize());



    }


    public void rpgify(CraftLivingEntity livingEntity) throws NoSuchFieldException, InvocationTargetException, InstantiationException, IllegalAccessException {

//        if(livingEntity instanceof RpgEntity){
//            return;
//        }
//
//        BaseRpgEntity instance = new BaseRpgEntity((CraftServer) livingEntity.getServer(),livingEntity.getHandle());
//        DynamicType.Builder<? extends CraftLivingEntity> thing = new ByteBuddy()
//                .subclass(livingEntity.getClass(), ConstructorStrategy.Default.IMITATE_SUPER_CLASS);
//
//        //theres a better way to do this, but its 2 am
//        //i cannot stress enough how much i dont like this solution
//        for (Method method : BaseRpgEntity.class.getDeclaredMethods()) {
//            thing=thing.method(namedIgnoreCase(method.getName())).intercept(MethodDelegation.to(instance));
//        }
//        thing=thing.implement(RpgEntity.class).intercept(MethodCall.invokeSelf().on(instance).withAllArguments());
//        Class<? extends CraftLivingEntity> newClass = thing.make().load(getClass().getClassLoader()).getLoaded();
//
//        Field field = net.minecraft.world.entity.Entity.class.getDeclaredField("bukkitEntity");
//        field.setAccessible(true); // Force to access the field
//        field.set(livingEntity.getHandle(),
//                newClass.getDeclaredConstructors()[0].newInstance(
//                        livingEntity.getServer(),
//                        livingEntity.getHandle().getClass().cast(livingEntity.getHandle())
//                ));
    }
}
