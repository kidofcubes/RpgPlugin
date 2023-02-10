//package io.github.kidofcubes;
//
//import org.bukkit.NamespacedKey;
//import org.bukkit.inventory.ItemStack;
//import org.jetbrains.annotations.NotNull;
//
//public interface RpgAttachment<Attached,T extends RpgAttachment<?,?>> extends RpgObject{
//
////    static <Attached,T extends RpgAttachment<Attached,?>> void setInstance(Attached thing, T zhis){
////        zhis.getHolder(thing).setObject(zhis);
////    }
////
////
////    T getInstance(Attached thing);
//
//    RpgObjectTag getHolder(Attached thing);
//
//
//    @Override
//    default void setType(NamespacedKey namespacedKey){
//        getHolder().setSavedType(namespacedKey);
//    }
//
//    /**
//     * if no type can be found, returns defaultTypeKey
//     *
//     * @return
//     */
//    @Override
//    @NotNull NamespacedKey getType();
//}
