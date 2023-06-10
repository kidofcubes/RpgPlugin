package io.github.kidofcubes.rpgplugin;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.utility.JavaModule;
import net.minecraft.nbt.Tag;
import org.bukkit.craftbukkit.v1_19_R3.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Map;
import java.util.jar.JarFile;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class RpgifierAgent {
    public static void premain(String arguments, Instrumentation instrumentation) {
        //update i am doing things
        new AgentBuilder.Default()
                .with(new AgentBuilderListenerThing())

//                    .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) //woah these mean things better not touch them until i need them
//                    .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
//                    .with(AgentBuilder.TypeStrategy.Default.REDEFINE)

                .type(named("org.bukkit.Bukkit"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                    try {
                        for(URL url: ((URLClassLoader)classLoader).getURLs()){
                            instrumentation.appendToSystemClassLoaderSearch((new JarFile(new File(url.toURI()))));
                        }
                    } catch (URISyntaxException | IOException e) {
                        throw new RuntimeException(e);
                    }

                    //todo gooden this class injector stuff
                    Class<?>[] classes = new Class[0];
                    classes = new Class[]{RPG.class, RpgClass.class, //y
                            Stat.class, RpgRegistry.RegisteredStatListener.class,
                            RPGImpl.class,
                            RpgItem.class, RpgItemImpl.class,
                            RpgEntity.class, RpgEntityImpl.class,
//                                RpgTile.class, RpgTileImpl.class,
                            EntityHolder.class, TagWrapper.class,
                            RpgRegistry.class};
                    for (Class<?> clazz : classes) {
                        new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(clazz), ClassFileLocator.ForClassLoader.read(clazz)));
                    }

                    return builder;
                })
                .type(named("org.bukkit.inventory.ItemStack"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                    try {
                        return
                                builder
                                        .implement(RpgItem.class)
                                        .method((isDeclaredBy(RPG.class)).or(isDeclaredBy(RpgItem.class)))
                                        .intercept(MethodCall.invokeSelf()
                                                .onMethodCall(MethodCall.invoke(RpgItemImpl.class.getMethod("getRpg", ItemStack.class)).withThis()).withAllArguments()
                                        )
                                ;
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                })
                .type(named("org.bukkit.entity.Entity"))
                .transform(new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
                        try {
                            return
                                    builder
                                            .implement(RpgEntity.class)
                                            .method((isDeclaredBy(RPG.class)).or(isDeclaredBy(RpgEntity.class)))
                                            .intercept(MethodCall.invokeSelf()
//                                                    .onMethodCall(MethodCall.invoke(RpgEntityImpl.class.getMethod("getRpg", LivingEntity.class)).withThis()).withAllArguments()
                                                    .onMethodCall(MethodCall.invoke(RpgEntityImpl.class.getMethod("getRpg", Entity.class)).withThis()).withAllArguments()
                                            )

                                    ;
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .type(named("org.bukkit.craftbukkit.v1_19_R3.persistence.CraftPersistentDataContainer"))

                .transform(new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
                        return builder.implement(EntityHolder.class)
                                .defineField("rpg",RpgEntity.class)
                                .method(named("getRpg")).intercept(FieldAccessor.ofField("rpg"))
                                .method(named("setRpg")).intercept(FieldAccessor.ofField("rpg"));
                    }
                })
//                .type(named("org.bukkit.block.TileState"))
//                .transform(new AgentBuilder.Transformer() {
//                    @Override
//                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
//                        try {
//                            return
//                                    builder
//                                            .implement(RpgTile.class)
//                                            .method((isDeclaredBy(RpgObject.class)).or(isDeclaredBy(RpgTile.class)))
//                                            .intercept(MethodCall.invokeSelf()
//                                                    .onMethodCall(MethodCall.invoke(RpgTileImpl.class.getMethod("getInstance", TileState.class)).withThis()).withAllArguments()
//                                            )
//
//                                    ;
//                        } catch (NoSuchMethodException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                })
//                .type(named("org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemFactory"))
//                .transform(new AgentBuilder.Transformer() {
//                    @Override
//                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
//                        return builder.method(not(returns(void.class)).and(not(named("instance"))).and(not(named("hashcode"))).and(not(isToString()))).intercept(Advice.to(TestAdvice.class));
//                    }
//                })
//                .type(named("org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack"))
//                .transform(new AgentBuilder.Transformer() {
//                    @Override
//                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
//                        System.out.print("MAGIC GO");
////                        return builder.visit(Advice.to(TestAdvice.class))
//                        return builder.method(not(returns(void.class)).and(not(named("hashcode"))).and(not(isToString()))).intercept(Advice.to(TestAdvice.class));
//                    }
//                })
//                .type(named("org.bukkit.craftbukkit.v1_19_R3.inventory.CraftMetaItem"))
//                .transform(new AgentBuilder.Transformer() {
//                    @Override
//                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
//                        return builder.method(not(returns(void.class)).and(not(named("hashcode"))).and(not(isToString()))).intercept(Advice.to(TestAdvice.class));
//                    }
//                })
                .installOn(instrumentation);

    }

//    public static StackTraceElement getCaller() {
//        StackTraceElement ste = Thread.currentThread().getStackTrace()[3];
////        String callerClass = ste.getClassName();
////        String callerMethod = ste.getMethodName();
//        return ste;
//    }
    public static class TestAdvice {


        @Advice.OnMethodEnter(suppress = Throwable.class)
        static long enter(@Advice.Origin String origin,
                          @Advice.AllArguments Object[] ary){

            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
            StackTraceElement stackTraceElement2 = Thread.currentThread().getStackTrace()[3];
            System.out.print("FROM "+stackTraceElement2.getClassName()+"."+stackTraceElement2.getMethodName()+" ====> "+stackTraceElement.getClassName()+"."+stackTraceElement.getMethodName()+"\n");
            System.out.print("RUNNING {"+origin+"} WITH ");
//            for(StackTraceElement stackTraceElement: Thread.currentThread().getStackTrace()){
//                System.out.print("FROM "+stackTraceElement.getClassName()+"."+stackTraceElement.getMethodName()+"\n");
//            }
            if(ary != null) {
                System.out.print("[");
//                System.out.print("{"+((net.minecraft.world.item.ItemStack)ary[0]).getTag().getAsString()+"},");
//                if(ary.length>1){
//                    System.out.print("{"+((net.minecraft.world.item.ItemStack)ary[1]).getTag().getAsString()+"},");
//                }
                int i=0;

                try {
                    Class<?> clazz = Class.forName("org.bukkit.craftbukkit.v1_19_R3.inventory.CraftMetaItem");
                    Field field = clazz.getDeclaredField("persistentDataContainer");
                    field.setAccessible(true);
                    Method method = clazz.getDeclaredMethod("serialize");
                    method.setAccessible(true);
                    for (Object o : ary) {
                        if(o==null){
                            System.out.print("ARG "+(i++)+"=\""+o+"\", ");
                            continue;
                        }
                        if(clazz.isAssignableFrom(o.getClass())){

                            System.out.print("ARG "+(i++)+"=\""+o+"\"\n");
                            Map<String,Object> things = (Map<String, Object>) method.invoke(o);
                            System.out.print("  things in the craftmeta\n");
                            for(Map.Entry<String,Object> thing : things.entrySet()){
                                System.out.print("    "+thing.getKey()+":"+thing.getValue()+"\n");
                            }
                            System.out.print("  things in the persistent datacontainer:\n");
                            for(Map.Entry<String, Tag> entry : ((CraftPersistentDataContainer)field.get(o)).getRaw().entrySet()){
                                System.out.print("    "+entry.getKey()+":"+entry.getValue().getAsString()+"\n");
                            }
                            System.out.print("  END PERSISTENT DATACONTAINER LOG\n");
                        }else if(o instanceof net.minecraft.world.item.ItemStack itemStack){
                            System.out.print("ARG "+(i++)+"=\""+o+"\"\n");
                            if(itemStack.getTag()!=null) {
                                System.out.print("    tag of the itemstack: " +itemStack.getTag().getAsString()+"\n");
                            }else{
                                System.out.print("    itemstack had no tag :(\n");
                            }
                        }else{
                            System.out.print("ARG "+(i++)+"=\""+o+"\", ");
                        }
                    }
                    System.out.print("]");
                } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException |
                         NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.print("\n");

            return System.nanoTime();
        }

        @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
        static void exit(@Advice.Enter long time
                , @Advice.Origin String origin
                , @Advice.Return Object returnValue
        ){
            System.out.print("ENDING  {"+origin+"} RETURNING "+returnValue+"\n");
//            System.out.println("Method Execution Time: " + (System.nanoTime() - time) + " nano seconds");
        }
    }

    static class AgentBuilderListenerThing implements AgentBuilder.Listener{

        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
//            System.out.println("WE FOUND "+typeName);
        }

        @Override
        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
            System.out.println("Transformed "+typeDescription.getActualName());
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
//            System.out.println("WE IGNORED "+typeDescription.getTypeName());
        }

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
            System.out.println("Errored on "+typeName);
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
//            System.out.println("WE COMPLETED"+typeName);
        }
    }
}
