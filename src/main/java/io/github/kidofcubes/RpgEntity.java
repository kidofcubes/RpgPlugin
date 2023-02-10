package io.github.kidofcubes;


import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

//add entity specific stuff here later
public interface RpgEntity extends RpgObject {

    default RpgEntity loadFromJson(String json) {
        RpgObject.super.loadFromJson(json);
        return this;
    }

    default RpgEntity self(){
        return this;
    }
}
