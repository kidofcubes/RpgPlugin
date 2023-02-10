package io.github.kidofcubes;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public interface RpgItem extends RpgObject{
    default RpgItem loadFromJson(String json) {
        RpgObject.super.loadFromJson(json);
        return this;
    }

    default RpgItem self(){
        return this;
    }
}