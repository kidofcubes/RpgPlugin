package io.github.kidofcubes.rpgplugin;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R3.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.*;


/**
 * Persistent wrapper for an entity
 */
public class RpgEntityImpl extends RPGImpl implements RpgEntity {
    public RpgEntityImpl(Entity entity) {
        super(getHolder(entity));
    }

    public static CompoundTag getHolder(Entity entity){
        CraftPersistentDataContainer persistentDataContainer = (CraftPersistentDataContainer) entity.getPersistentDataContainer();
        if(!persistentDataContainer.getRaw().containsKey(RPG.RPG_TAG_KEY.asString())){
            persistentDataContainer.getRaw().put(RPG.RPG_TAG_KEY.asString(),new CompoundTag());
        }
        return (CompoundTag) persistentDataContainer.getRaw().get(RPG.RPG_TAG_KEY.asString());
    }

    @NotNull
    public static RpgEntity getRpg(Entity entity) { //if livingentity has a type already, init that type instead, if not, init default

        CompoundTag tag = getHolder(entity);
        NamespacedKey type = RPG.DEFAULT_TYPE_KEY;
        String possibleKey = tag.getString(RPG.TYPE_KEY);
        if(NamespacedKey.fromString(possibleKey)!=null) type=NamespacedKey.fromString(possibleKey);

        ((EntityHolder) entity.getPersistentDataContainer()).setRpg(
                RpgRegistry.getTypeConstructor(RpgEntity.class, RpgRegistry.containsTypeConstructor(RpgEntity.class, type) ? type : RPG.DEFAULT_TYPE_KEY).apply(entity)
        );
//        return new RpgEntityImpl((CompoundTag) persistentDataContainer.getRaw().get(RPG.RPG_TAG_KEY.asString()));
        return ((EntityHolder) entity.getPersistentDataContainer()).getRpg();
    }



    public List<RPG> usedThings(){
        return List.of(this);
    }
    public void addUsedStats(NamespacedKey key, Map<RPG,Stat> map){
        super.addUsedStats(key,map);
        usedThings().forEach(rpg -> {
            if(rpg!=this) addUsedStats(key,map);
        });
    }

    public void use(RPG rpg){
        for(Map.Entry<NamespacedKey,Stat> entry : rpg.getStats().entrySet()){
            entry.getValue().onUseStat(this);
        }
    }
    public void stopUsing(RPG rpg){
        for(Map.Entry<NamespacedKey,Stat> entry : rpg.getStats().entrySet()){
            entry.getValue().onStopUsingStat(this);
        }
    }
}
