package io.github.kidofcubes;

import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ItemStackInterceptor {

    //    @RuntimeType
//    public Object intercept(@Origin Method method,
//                            @AllArguments Object[] args) throws Throwable {
//        try {
////            return method.invoke(proxyClient, args);
//        } finally {
//            // do your completion logic here
//        }
//    }
//    public static void load(@SuperCall Runnable zuper, @This Object self, @AllArguments Object[] args){
//        System.out.println("LOAD OKA FKLJ SA DHFLKJAH sdadasdadsa");
//    }
//    public static void applyToItem(@SuperMethod Method method, @This Object dis, @AllArguments Object[] args){
//        System.out.println("sdfasdfasfafdasdfasfasfdasdfsdasdasdwdaasdwa");
//        System.out.println("asdgm naksdjfnkal");
//        try {
//            method.setAccessible(true);
//            method.invoke(dis,args[0]);
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("OIASHFDIASLHFLSAKF HIALSKFLKJSDFKJAHLDFKA DSKJLFAF DA");
//        System.out.println("OIASHFDIASLHFLSAKF HIALSKFLKJSDFKJAHLDFKA DSKJLFAF DA");
//        System.out.println("OIASHFDIASLHFLSAKF HIALSKFLKJSDFKJAHLDFKA DSKJLFAF DA");
//        ((CompoundTag)args[0]).putString("whentheimposterissuspicious","butcookiesaredelicious");
//        for (Object obj : args) {
//            System.out.println("theres a arg "+obj);
//        }
//    //                                                    itemTag.putString("whentheimposterissuspicious","butcookiesaredelicious");
//    }
//    public static Object save(@SuperMethod Method method, @This Object dis, @AllArguments Object[] args){
//        System.out.println("this is SAVING");
//        System.out.println("asdgm naksdjfnkal");
//        try {
//            method.setAccessible(true);
//            method.invoke(dis,args[0]);
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("READRELALKASJKLJSAHDFKJASHDFKJLASHD DA");
//        System.out.println("READRELALKASJKLJSAHDFKJASHDFKJLASHD DA");
//        for (Object obj : args) {
//            System.out.println("theres a arg "+obj);
//        }
//    //        compoundTag.putString("SUS","REAL");
//        return args[0];
//    //                                                    itemTag.putString("whentheimposterissuspicious","butcookiesaredelicious");
//    }
    @RuntimeType
    public static Object save(@This ItemStack zhis, @Argument(0) Object arg){
        ResourceLocation minecraftkey = BuiltInRegistries.ITEM.getKey(zhis.getItem());
        CompoundTag nbt = (CompoundTag)arg;
        nbt.putString("id", minecraftkey == null ? "minecraft:air" : minecraftkey.toString());
        nbt.putByte("Count", (byte) zhis.getCount());
        if (zhis.getTag() != null) {
            nbt.put("tag", zhis.getTag().copy());
        }


        return nbt;
//        for (Object obj : args) {
//            System.out.println("theres a arg "+obj);
//        }
//        compoundTag.putString("SUS","REAL");
//        return arg;
//        return args[0];
//                                                    itemTag.putString("whentheimposterissuspicious","butcookiesaredelicious");
    }
    //getAsString
}
