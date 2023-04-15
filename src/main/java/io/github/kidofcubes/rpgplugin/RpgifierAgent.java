package io.github.kidofcubes.rpgplugin;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.JavaModule;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.Collections;
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
                    try {
                        classes = new Class[]{RpgObject.class, RpgClass.class, Class.forName(RpgObject.class.getName()+"$1"), //y
                                Stat.class, RpgRegistry.RegisteredStatListener.class, Stat.ModifiableFunction.class,
                                RpgObjectImpl.class,
                                RpgItem.class, RpgItemStack.class,
                                RpgEntity.class, RpgLivingEntity.class,
                                RpgRegistry.class, RpgObjectTag.class,RpgObjectTag.class};
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
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
                                        .method((isDeclaredBy(RpgObject.class)).or(isDeclaredBy(RpgItem.class)))
                                        .intercept(MethodCall.invokeSelf()
                                                .onMethodCall(MethodCall.invoke(RpgItemStack.class.getMethod("getInstance", ItemStack.class)).withThis()).withAllArguments()
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
                                                    .onMethodCall(MethodCall.invoke(RpgLivingEntity.class.getMethod("getInstance", LivingEntity.class)).withThis()).withAllArguments()
                                            )

                                    ;
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
//                .type(named("net.minecraft.world.item.ItemStack"))
//                .transform(new AgentBuilder.Transformer() {
//                    @Override
//                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
//                        return new ByteBuddy()
//                                .redefine(net.minecraft.world.item.ItemStack.class)
//                                .visit(Advice.to(TestAdvice.class).on(isMethod().and(not(returns(void.class))).and(not(takesArguments(0))).and(takesArgument(0,named("net.minecraft.world.item.ItemStack")))));
//                    }
//                })

                .installOn(instrumentation);

    }

    public class TestAdvice {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        static long enter(@Advice.Origin String origin,
                          @Advice.AllArguments Object[] ary){

            System.out.print("RUNNING {"+origin+"} WITH ");

            if(ary != null) {
                System.out.print("[");
                System.out.print("{"+((net.minecraft.world.item.ItemStack)ary[0]).getTag().getAsString()+"},");
                if(ary.length>1){
                    System.out.print("{"+((net.minecraft.world.item.ItemStack)ary[1]).getTag().getAsString()+"},");
                }

//                for (Object o : ary) {
//                }
                System.out.print("]");
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
