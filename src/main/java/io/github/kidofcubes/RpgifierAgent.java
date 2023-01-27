package io.github.kidofcubes;

import io.github.kidofcubes.managers.StatManager;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.implementation.*;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.JavaModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.*;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.implementation.MethodDelegation.toMethodReturnOf;
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
                    Class<?>[] classes = new Class[]{RpgObject.class, RpgClass.class,
                            Stat.class,Stat.StatContainer.class, StatManager.class, StatRegisteredListener.class, TimedStat.class, ActivateStats.class,
                            RpgItem.class, RpgItemStack.class,
                            RpgEntity.class, RpgLivingEntity.class,
                            RpgObjectHolder.class,RpgObjectTag.class,RpgObjectTag.TypeThing.class,
                            GlobalVariables.class};
//                    Map<TypeDescription,byte[]> loadedData = new HashMap<>();
                    for (Class<?> clazz : classes) {
//                        System.out.println("LOADING "+clazz.getName());
//                        loadedData.put(TypeDescription.ForLoadedType.of(clazz),ClassFileLocator.ForClassLoader.read(clazz));
                        new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(clazz), ClassFileLocator.ForClassLoader.read(clazz)));
                    }
//                    new ClassInjector.UsingUnsafe(classLoader).inject(loadedData);
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgObject.class), ClassFileLocator.ForClassLoader.read(RpgObject.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgClass.class), ClassFileLocator.ForClassLoader.read(RpgClass.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(Stat.class), ClassFileLocator.ForClassLoader.read(Stat.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(Stat.StatContainer.class), ClassFileLocator.ForClassLoader.read(Stat.StatContainer.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgItem.class), ClassFileLocator.ForClassLoader.read(RpgItem.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgItemStack.class), ClassFileLocator.ForClassLoader.read(RpgItemStack.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgEntity.class), ClassFileLocator.ForClassLoader.read(RpgEntity.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgLivingEntity.class), ClassFileLocator.ForClassLoader.read(RpgLivingEntity.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgObjectHolder.class), ClassFileLocator.ForClassLoader.read(RpgObjectHolder.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgObjectTag.class), ClassFileLocator.ForClassLoader.read(RpgObjectTag.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgObjectTag.TypeThing.class), ClassFileLocator.ForClassLoader.read(RpgObjectTag.TypeThing.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(StatManager.class), ClassFileLocator.ForClassLoader.read(StatManager.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(StatRegisteredListener.class), ClassFileLocator.ForClassLoader.read(StatRegisteredListener.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(TimedStat.class), ClassFileLocator.ForClassLoader.read(TimedStat.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(ActivateStats.class), ClassFileLocator.ForClassLoader.read(ActivateStats.class)));
//                    new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(GlobalVariables.class), ClassFileLocator.ForClassLoader.read(GlobalVariables.class)));

                    return builder;
                })


                .type(named("org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer"))

                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> builder.implement(RpgObjectHolder.class)
                    .defineField("instance",RpgObject.class)
                    .method(named("getObject")).intercept(FieldAccessor.ofField("instance"))
                    .method(named("setObject")).intercept(FieldAccessor.ofField("instance")))
                .type(named("org.bukkit.inventory.ItemStack"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                    try {
                        return
                                builder
                                        .implement(RpgItem.class)
                                        .method((isDeclaredBy(RpgObject.class)).or(isDeclaredBy(RpgItem.class)))
                                        .intercept(MethodCall.invokeSelf()
                                                .onMethodCall(MethodCall.invoke(RpgItemStack.class.getMethod("getRpgItemInstance", ItemStack.class)).withThis()).withAllArguments()
                                        )
                                ;
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                })
                .type(named("org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity"))
                .transform(new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
                        try {
                            return
                                    builder
                                            .implement(RpgEntity.class)
                                            .method((isDeclaredBy(RpgObject.class)).or(isDeclaredBy(RpgEntity.class)))
                                            .intercept(MethodCall.invokeSelf()
                                                    .onMethodCall(MethodCall.invoke(RpgLivingEntity.class.getMethod("getRpgEntityInstance", LivingEntity.class)).withThis()).withAllArguments()
                                            )

                                    ;
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })

                .installOn(instrumentation);

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
