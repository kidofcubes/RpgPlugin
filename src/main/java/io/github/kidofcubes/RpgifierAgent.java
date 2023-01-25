package io.github.kidofcubes;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.implementation.*;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.JavaModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer;
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
import java.util.Map;
import java.util.jar.JarFile;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.implementation.MethodDelegation.toMethodReturnOf;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class RpgifierAgent {
    public static void premain(String arguments, Instrumentation instrumentation) {
        try {
            JarURLConnection connection = (JarURLConnection)
                    RpgifierAgent.class.getResource("RpgItem.class").openConnection();
//            instrumentation.appendToBootstrapClassLoaderSearch(connection.getJarFile());
//            instrumentation.appendToSystemClassLoaderSearch(connection.getJarFile());
//            instrumentation.appendToBootstrapClassLoaderSearch(new JarFile("/HDD2TB/Testing Version Servers/1.19.3Paper/versions/1.19.3/paper-1.19.3.jar"));
//            instrumentation.appendToSystemClassLoaderSearch(new JarFile("/HDD2TB/Testing Version Servers/1.19.3Paper/versions/1.19.3/paper-1.19.3.jar"));
//            instrumentation.appendToBootstrapClassLoaderSearch(new JarFile("/HDD2TB/Testing Version Servers/1.19.3Paper/libraries/io/papermc/paper/paper-mojangapi/1.19.3-R0.1-SNAPSHOT/paper-mojangapi-1.19.3-R0.1-SNAPSHOT.jar"));
            System.out.println("SJLKASDJFLKAJFD my classloader is "+RpgifierAgent.class.getClassLoader());
            System.out.println("SJLKASDJFLKAJFD my classloader parent is "+RpgifierAgent.class.getClassLoader().getParent());
            System.out.println("SJLKASDJFLKAJFD my classloader parent is "+RpgifierAgent.class.getClassLoader().getParent());
//            URLClassLoader child = new URLClassLoader(
//                    new URL[] {new File("/HDD2TB/Testing Version Servers/1.19.3Paper/plugins/RpgPlugin-1.1-SNAPSHOT.jar").toURI().toURL()},
//                    RpgifierAgent.class.getClassLoader()
//            );
//
//            Class rpgItemClass = Class.forName("io.github.kidofcubes.RpgItem", true, child);
//            URLClassLoader finalChild = child;
//            RpgifierAgent.class.getClassLoader().loadClass(PersistentDataContainer.class.getName());


            //its been 5 days and i dont know how to do the thing, so im going to just hard copy paste code
            //update i am doing things
            new AgentBuilder.Default()
                    .with(new AgentBuilderListenerThing())
//                    .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) //woah these mean things better not touch them until i need them
//                    .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
//                    .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                    .type(named("org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer"))

                    .transform(new AgentBuilder.Transformer() {


                        @Override
                        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {


                            System.out.println("KSHKDJA NEW THING AHKFJHADFKJLA");

//                            return builder.implement(RpgObjectHolder.class);

//                            new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(TestInterface.class), ClassFileLocator.ForClassLoader.read(TestInterface.class)));
//                            new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(CraftPersistentDataContainer.class), ClassFileLocator.ForClassLoader.read(RpgPersistentDataContainer.class)));
//                            new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgPersistentDataContainer.class), ClassFileLocator.ForClassLoader.read(RpgPersistentDataContainer.class)));
                            builder=builder.implement(RpgObjectHolder.class);
//                            return new ByteBuddy().redefine(RpgPersistentDataContainer.class).name("org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer");
//                            return new ByteBuddy().redefine(RpgPersistentDataContainer.class).name("org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer");
//                            builder=new ByteBuddy().rebase(CraftPersistentDataContainer.class).implement(TestInterface.class).method(isDeclaredBy(RpgObjectHolder.class))
//                                    .intercept(to(RpgObjectHolder.class));
//                            builder=builder.method(any()).intercept()
//                            for(Method method:RpgPersistentDataContainer.class.getDeclaredMethods()){
//                                builder.method(named(method.getName())).intercept(MethodCall.invoke(method));
//                            }
                            //instance
//                            try {
//                                builder=builder.constructor(takesArguments(2)).intercept(SuperMethodCall.INSTANCE.andThen(MethodCall.invoke(RpgPersistentDataContainer.class.getMethod("onConstructor", Map.class)).withField("customDataTags")));
//                                builder=builder.method(named("toTagCompound")).intercept(MethodCall.invoke(RpgPersistentDataContainer.class.getMethod("toTagCompound", CompoundTag.class,Object.class))
//                                        .withMethodCall(MethodCall.invokeSuper())
//                                        .withThis());
//                            } catch (NoSuchMethodException e) {
//                                throw new RuntimeException(e);
//                            }
                            builder=builder.defineField("instance",RpgObject.class);
                            builder=builder.method(named("getObject")).intercept(FieldAccessor.ofField("instance"));
                            builder=builder.method(named("setObject")).intercept(FieldAccessor.ofField("instance"));
//                            builder=builder.method(named("setObject")).intercept(FieldAccessor.setField("instance"));
//                                builder=builder.method(named("getObject")).intercept(MethodCall.invoke(RpgPersistentDataContainer.class.getDeclaredMethod("getObject")));
//                                builder=builder.method(named("setObject")).intercept(MethodCall.invoke(RpgPersistentDataContainer.class.getDeclaredMethod("setObject", TestInterface.class)).withAllArguments());
                            return builder;
                        }
                    })
                    .type(named("org.bukkit.inventory.ItemStack"))
                    .transform(new AgentBuilder.Transformer() {
                        @Override
                        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
                            try {
                                for(URL url: ((URLClassLoader)classLoader).getURLs()){
                                    instrumentation.appendToSystemClassLoaderSearch((new JarFile(new File(url.toURI()))));
                                }
                            } catch (URISyntaxException | IOException e) {
                                throw new RuntimeException(e);
                            }
                            new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgObject.class), ClassFileLocator.ForClassLoader.read(RpgObject.class)));
                            new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgItem.class), ClassFileLocator.ForClassLoader.read(RpgItem.class)));
                            new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgObjectHolder.class), ClassFileLocator.ForClassLoader.read(RpgObjectHolder.class)));
                            new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgItemStack.class), ClassFileLocator.ForClassLoader.read(RpgItemStack.class)));
//                            new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgPlugin.class), ClassFileLocator.ForClassLoader.read(RpgPlugin.class)));
                            new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgObjectTag.class), ClassFileLocator.ForClassLoader.read(RpgObjectTag.class)));
                            new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgObjectTag.TypeThing.class), ClassFileLocator.ForClassLoader.read(RpgObjectTag.TypeThing.class)));




                            try {
                                return
                                        builder
                                        .implement(RpgItem.class)
                                        .defineMethod("getRpgItemInstance",RpgItem.class).intercept(MethodCall.invoke(RpgItemStack.class.getMethod("getRpgItemInstance", ItemMeta.class, ItemStack.class))
                                                        .withField("meta").withThis())
                                        .method(isDeclaredBy(RpgItem.class).or(isDeclaredBy(RpgObject.class)))
                                                .intercept(toMethodReturnOf("getRpgItemInstance"))
                                        ;
                            } catch (NoSuchMethodException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })


                    .installOn(instrumentation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    static class AgentBuilderListenerThing implements AgentBuilder.Listener{

        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
//            System.out.println("WE FOUND "+typeName);
        }

        @Override
        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
            System.out.println("WE TRANSFORMED "+typeDescription.getActualName());
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
//            System.out.println("WE IGNORED "+typeDescription.getTypeName());
        }

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
            System.out.println("WE ERRORED "+typeName);
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
//            System.out.println("WE COMPLETED"+typeName);
        }
    }
}
