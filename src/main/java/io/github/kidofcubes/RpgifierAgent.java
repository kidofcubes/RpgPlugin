package io.github.kidofcubes;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Collections;

import static net.bytebuddy.implementation.MethodDelegation.to;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class RpgifierAgent {
    public static void premain(String arguments, Instrumentation instrumentation) {
        try {
            JarURLConnection connection = (JarURLConnection)
                    RpgifierAgent.class.getResource("RpgItem.class").openConnection();
//            instrumentation.appendToBootstrapClassLoaderSearch(connection.getJarFile());
//            instrumentation.appendToSystemClassLoaderSearch(connection.getJarFile());
            System.out.println("SJLKASDJFLKAJFD my classloader is "+RpgifierAgent.class.getClassLoader());
            System.out.println("SJLKASDJFLKAJFD my classloader parent is "+RpgifierAgent.class.getClassLoader().getParent());
//            URLClassLoader child = new URLClassLoader(
//                    new URL[] {new File("/HDD2TB/Testing Version Servers/1.19.3Paper/plugins/RpgPlugin-1.1-SNAPSHOT.jar").toURI().toURL()},
//                    RpgifierAgent.class.getClassLoader()
//            );
//
//            Class rpgItemClass = Class.forName("io.github.kidofcubes.RpgItem", true, child);
//            URLClassLoader finalChild = child;
            new AgentBuilder.Default()
                    .type(ElementMatchers.namedOneOf("org.bukkit.inventory.ItemStack","net.minecraft.world.item.ItemStack"))
                    .transform(new AgentBuilder.Transformer() {
                        @Override
                        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
//                            Thread.currentThread().setContextClassLoader(finalChild);
                            System.out.println("TRANSFORMED ");
                            System.out.println("its classloader is "+classLoader);
                            System.out.println("its classloader parent is "+classLoader.getParent());

                            System.out.println("HELP MEEEEEEEEEEEEEEEEE");
                            new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgItem.class), ClassFileLocator.ForClassLoader.read(RpgItem.class)));
                            System.out.println("HELP EEEEEEEEEEEEEEEEEEE");
                            new ClassInjector.UsingReflection(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgItem.class), ClassFileLocator.ForClassLoader.read(RpgItem.class)));
                            System.out.println("HELP HHHHHHHHHHHHHHHHHHHH");
//                                new ClassInjector.UsingInstrumentation(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgItem.class), ClassFileLocator.ForClassLoader.read(RpgItem.class)));
                            new ClassInjector.UsingJna(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgItem.class), ClassFileLocator.ForClassLoader.read(RpgItem.class)));
//                                new ClassInjector.UsingLookup(classLoader).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(RpgItem.class), ClassFileLocator.ForClassLoader.read(RpgItem.class)));
                            System.out.println("ADDED URL sdfhakjldfhasklfdhjkaldhlksakf");
//                                Field classPathField = classLoader.getClass().getDeclaredField("ucp");
//                                System.out.println("ADDED URL help neeeeee");
//                                classPathField.setAccessible(true);
//                                System.out.println("ADDED URL help ahklsdhfalk");
//                                classPathField.get(classLoader).getClass().getDeclaredMethod("addURL",URL.class).invoke(connection.getJarFileURL());
//                            URLClassPath classPath = (URLClassPath) classPathField.get(classLoader);

//                            classPath.addURL();
                            System.out.println("ADDED URL HKJASLTHLKAHDFLAHDFSADJ");
                            //                            classLoader=child;
                            return builder.implement(RpgItem.class).method(named("getDamageValue")).intercept(FixedValue.value(0));
//                            return builder.method(named("getDamageValue")).intercept(FixedValue.value(1));
                        }
                    })
                    .type(nameContains("URLClassLoader"))
                    .transform(new AgentBuilder.Transformer() {

                        @Override
                        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
                            return builder.constructor(takesArguments(1)).intercept(to(new Object() {
                                public void construct(@Argument(0) Object urlz) throws Exception {
                                    URL[] urls = (URL[])urlz;
                                    System.out.println("before constructor arguments are");
                                    for (URL url: urls) {
                                        System.out.println("url: "+url.toString());
                                    }
                                }
                            }).andThen(SuperMethodCall.INSTANCE)).method(named("addURL")).intercept(to(new Object(){
                                public void addURL(@Argument(0) URL url){
                                    System.out.println("URL WAS "+url.toString());
                                }
                            }));
                        }
                    })
                    .type(nameContains("URLClassPath")).transform(new AgentBuilder.Transformer() {
                        @Override
                        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
                            return builder.method(nameContains("addURL")).intercept(to(new Object(){
                                public synchronized void addURL(@Argument(0) Object urll){
                                    System.out.println("URLCLASSPATH "+((URL)urll).toString());
                                }
                            }));
                        }
                    })


                    .installOn(instrumentation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
