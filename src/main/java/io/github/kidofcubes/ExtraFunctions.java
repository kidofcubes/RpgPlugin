package io.github.kidofcubes;

import io.github.kidofcubes.types.DamageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ExtraFunctions {
    private static final TreeMap<Integer, String> map = new TreeMap<>();

    static {

        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");

    }

    public static boolean isEmpty(ItemStack itemStack) {
        if (itemStack != null) {
            return itemStack.getType().isAir();
        } else {
            return true;
        }
    }

    public static String toRoman(int number) {
        int l = map.floorKey(number);
        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number - l);
    }

    public final static String damageToString(Map<DamageType, Double> damage) {
        TextComponent output = Component.text("");
        if (damage.get(DamageType.PHYSICAL) != 0) {
            output = output.append(Component.text("-" + round(damage.get(DamageType.PHYSICAL), 1) + "❤ ", TextColor.fromCSSHexString("#AA0000")));
        }
        if (damage.get(DamageType.LIGHT) != 0) {
            output = output.append(Component.text("-" + round(damage.get(DamageType.LIGHT), 1) + "❤ ", TextColor.fromCSSHexString("#FFFFFF")));
        }
        if (damage.get(DamageType.MAGICAL) != 0) {
            output = output.append(Component.text("-" + round(damage.get(DamageType.MAGICAL), 1) + "❤ ", TextColor.fromCSSHexString("#0000AA")));
        }
        if (damage.get(DamageType.DARKNESS) != 0) {
            output = output.append(Component.text("-" + round(damage.get(DamageType.DARKNESS), 1) + "❂ ", TextColor.fromCSSHexString("#555555")));
        }
        if (damage.get(DamageType.EARTH) != 0) {
            output = output.append(Component.text("-" + round(damage.get(DamageType.EARTH), 1) + "✤ ", TextColor.fromCSSHexString("#00AA00")));
        }
        if (damage.get(DamageType.THUNDER) != 0) {
            output = output.append(Component.text("-" + round(damage.get(DamageType.THUNDER), 1) + "✦ ", TextColor.fromCSSHexString("#FFFF55")));
        }
        if (damage.get(DamageType.WATER) != 0) {
            output = output.append(Component.text("-" + round(damage.get(DamageType.WATER), 1) + "✽ ", TextColor.fromCSSHexString("#55FFFF")));
        }
        if (damage.get(DamageType.FIRE) != 0) {
            output = output.append(Component.text("-" + round(damage.get(DamageType.FIRE), 1) + "✹ ", TextColor.fromCSSHexString("#FF5555")));
        }
        if (damage.get(DamageType.AIR) != 0) {
            output = output.append(Component.text("-" + round(damage.get(DamageType.AIR), 1) + "❋ ", TextColor.fromCSSHexString("#FFFFFF")));
        }
        return LegacyComponentSerializer.legacySection().serialize(output);
    }

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public static Map<String,Stat> joinStatMaps(Map<String,Stat> map1, Map<String,Stat> map2){
        Map<String,Stat> returnvalue = new HashMap<>(map1);
        for (Map.Entry<String, Stat> entry :
                map2.entrySet()) {
            Stat orig = returnvalue.getOrDefault(entry.getKey(),null);
            if(orig!=null){
                if(orig.getLevel()<entry.getValue().getLevel()){
                    orig.setLevel(entry.getValue().getLevel());
                }
            }else{
                returnvalue.put(entry.getKey(),entry.getValue());
            }

        }
        return returnvalue;
    }
}
