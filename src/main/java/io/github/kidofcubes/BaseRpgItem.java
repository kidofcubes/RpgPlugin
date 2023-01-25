package io.github.kidofcubes;

import com.google.gson.Gson;
import net.bytebuddy.implementation.bind.annotation.*;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.persistence.PersistentDataContainer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;


//NOTE
//ALL ITEMMETAS ARE A CRAFTITEMMETA
public class BaseRpgItem implements TestInterface{ //is a itemstack addon

    //rpgPersistentDataContainer.setThing(this)
    //addstat()


    public List<String> list=new ArrayList<>();

    @Override
    public void addTest(String test) {
        list.add(test);
        System.out.println("added test "+test);
    }
    @Override

    public String getAsJSON() {
        return new Gson().toJson(list);
    }

    @Override
    public BaseRpgItem loadFromJson(String json) {
        list = new Gson().fromJson(json,List.class);
        return this;
    }


//    StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
//
//
//    @NotNull
//    public static ItemStack deserialize(@NotNull Map<String, Object> args) {
//        System.out.println("DESERIALZE ");
//        System.out.println("DESERIALZE ");
//        System.out.println("DESERIALZE ");
//        System.out.println("DESERIALZE ");
//        int version = (args.containsKey("v")) ? ((Number) args.get("v")).intValue() : -1;
//        short damage = 0;
//        int amount = 1;
//
//        if (args.containsKey("damage")) {
//            damage = ((Number) args.get("damage")).shortValue();
//        }
//
//        Material type;
//        if (version < 0) {
//            type = Material.getMaterial(Material.LEGACY_PREFIX + (String) args.get("type"));
//
//            byte dataVal = (type != null && type.getMaxDurability() == 0) ? (byte) damage : 0; // Actually durable items get a 0 passed into conversion
//            type = Bukkit.getUnsafe().fromLegacy(new MaterialData(type, dataVal), true);
//
//            // We've converted now so the data val isn't a thing and can be reset
//            if (dataVal != 0) {
//                damage = 0;
//            }
//        } else {
//            type = Bukkit.getUnsafe().getMaterial((String) args.get("type"), version);
//        }
//
//        if (args.containsKey("amount")) {
//            amount = ((Number) args.get("amount")).intValue();
//        }
//
//        //if(args.containsKey("rpg"))
//        ItemStack result = new ItemStack(type, amount, damage);
//
//        if (args.containsKey("enchantments")) { // Backward compatiblity, @deprecated
//            Object raw = args.get("enchantments");
//
//            if (raw instanceof Map) {
//                Map<?, ?> map = (Map<?, ?>) raw;
//
//                for (Map.Entry<?, ?> entry : map.entrySet()) {
//                    Enchantment enchantment = Enchantment.getByName(entry.getKey().toString());
//
//                    if ((enchantment != null) && (entry.getValue() instanceof Integer)) {
//                        result.addUnsafeEnchantment(enchantment, (Integer) entry.getValue());
//                    }
//                }
//            }
//        } else if (args.containsKey("meta")) { // We cannot and will not have meta when enchantments (pre-ItemMeta) exist
//            Object raw = args.get("meta");
//            if (raw instanceof ItemMeta) {
//                ((ItemMeta) raw).setVersion(version);
//                result.setItemMeta((ItemMeta) raw);
//            }
//        }
//
//        if (version < 0) {
//            // Set damage again incase meta overwrote it
//            if (args.containsKey("damage")) {
//                result.setDurability(damage);
//            }
//        }
//        System.out.println("DESERIALZE ");
//        return result.ensureServerConversions(); // Paper
//    }
//
//    public static void intercept(@RuntimeType Object value) {
//        System.out.println("WHAT THE HELL IS THIS ");
//        System.out.println("THERES A "+value.getClass().getName());
////        for (Object obj :
////                args) {
////            System.out.println("THERES A "+obj.getClass().getName());
////        }
//    }
//    private void load(CompoundTag nbttagcompound){
//        System.out.println("THIS IS STUPID "+nbttagcompound.getAsString());
//    }
}
