package io.github.kidofcubes.rpgplugin;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R3.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;


public class RpgLivingEntity extends RPGImpl implements RpgEntity {
    public RpgLivingEntity(CompoundTag base) {
        super(base);
    }

    @NotNull
    public static RpgLivingEntity getRpg(LivingEntity livingEntity) { //if livingentity has a type already, init that type instead, if not, init default
        CraftPersistentDataContainer persistentDataContainer = (CraftPersistentDataContainer) livingEntity.getPersistentDataContainer();
        if(!persistentDataContainer.getRaw().containsKey(RPG.RPG_TAG_KEY.asString())){
            persistentDataContainer.getRaw().put(RPG.RPG_TAG_KEY.asString(),new CompoundTag());
        }
        System.out.println("GETTING THE THING FROM ITEMSTACK IS "+((CompoundTag) persistentDataContainer.getRaw().get(RPG.RPG_TAG_KEY.asString())).getAsString());
        return new RpgLivingEntity((CompoundTag) persistentDataContainer.getRaw().get(RPG.RPG_TAG_KEY.asString()));
    }
}
