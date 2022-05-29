package io.github.KidOfCubes;

import io.github.KidOfCubes.Types.DamageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.TreeMap;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class ExtraFunctions {
    public static boolean isEmpty(ItemStack itemStack){
        if(itemStack!=null){
            return itemStack.getType().isAir();
        }else{
            return true;
        }
    }

    private final static TreeMap<Integer, String> map = new TreeMap<Integer, String>();

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

    public final static String toRoman(int number) {
        int l =  map.floorKey(number);
        if ( number == l ) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number-l);
    }

    public final static String  damageToString(Map<DamageType,Double> damage){
        TextComponent output = Component.text("");
        if (damage.get(DamageType.Physical)!=0) {
            output = output.append(Component.text("-"+round(damage.get(DamageType.Physical),1)+"❤ ",TextColor.fromCSSHexString("#AA0000")));
        }
        if (damage.get(DamageType.Light)!=0) {
            output = output.append(Component.text("-"+round(damage.get(DamageType.Light),1)+"❤ ",TextColor.fromCSSHexString("#FFFFFF")));
        }
        if (damage.get(DamageType.Magical)!=0) {
            output = output.append(Component.text("-"+round(damage.get(DamageType.Magical),1)+"❤ ",TextColor.fromCSSHexString("#0000AA")));
        }
        if (damage.get(DamageType.Darkness)!=0) {
            output = output.append(Component.text("-"+round(damage.get(DamageType.Darkness),1)+"❂ ",TextColor.fromCSSHexString("#555555")));
        }
        if (damage.get(DamageType.Earth)!=0) {
            output = output.append(Component.text("-"+round(damage.get(DamageType.Earth),1)+"✤ ",TextColor.fromCSSHexString("#00AA00")));
        }
        if (damage.get(DamageType.Thunder)!=0) {
            output = output.append(Component.text("-"+round(damage.get(DamageType.Thunder),1)+"✦ ",TextColor.fromCSSHexString("#FFFF55")));
        }
        if (damage.get(DamageType.Water)!=0) {
            output = output.append(Component.text("-"+round(damage.get(DamageType.Water),1)+"✽ ",TextColor.fromCSSHexString("#55FFFF")));
        }
        if (damage.get(DamageType.Fire)!=0) {
            output = output.append(Component.text("-"+round(damage.get(DamageType.Fire),1)+"✽ ",TextColor.fromCSSHexString("#FFFF55")));
        }
        if (damage.get(DamageType.Air)!=0) {
            output = output.append(Component.text("-"+round(damage.get(DamageType.Air),1)+"✽ ",TextColor.fromCSSHexString("#FFFFFF")));
        }
        StringBuilder outputString = new StringBuilder();
        for (Component component : output.children()) {
            outputString.append(((TextComponent) component).content());
        }
        return LegacyComponentSerializer.legacySection().serialize(output);
    }
    public static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
