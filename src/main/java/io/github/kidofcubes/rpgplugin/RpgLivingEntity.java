package io.github.kidofcubes.rpgplugin;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R3.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;


public class RpgLivingEntity extends RpgObjectImpl implements RpgEntity {

    private NamespacedKey type = RpgObject.defaultTypeKey;

    @Override
    public void setRpgType(@NotNull NamespacedKey namespacedKey) {
        type=namespacedKey;
    }

    @Override
    public @NotNull NamespacedKey getRpgType() {
        return type;
    }

    @NotNull
    public static RpgObjectTag getHolder(LivingEntity livingEntity){

        CraftPersistentDataContainer persistentDataContainer = (CraftPersistentDataContainer) livingEntity.getPersistentDataContainer();
        if(!persistentDataContainer.getRaw().containsKey(RpgObjectTag.RpgObjectTagKey.asString())){
            persistentDataContainer.getRaw().put(RpgObjectTag.RpgObjectTagKey.asString(),new CompoundTag());
        }
        if (!(persistentDataContainer.getRaw().get(RpgObjectTag.RpgObjectTagKey.toString()) instanceof RpgObjectTag)) {
            persistentDataContainer.getRaw().put(RpgObjectTag.RpgObjectTagKey.toString(), RpgObjectTag.fromCompoundTag((CompoundTag) persistentDataContainer.getRaw().get(RpgObjectTag.RpgObjectTagKey.toString())));
        }
        return (RpgObjectTag) persistentDataContainer.getRaw().get(RpgObjectTag.RpgObjectTagKey.toString());
    }

    @NotNull
    public static RpgEntity getInstance(LivingEntity livingEntity) { //if livingentity has a type already, init that type instead, if not, init default
        RpgObjectTag holder = getHolder(livingEntity);

        if (holder.getRpgObject() == null) {
            //init object if not found
            NamespacedKey type = RpgObject.defaultTypeKey;
            if(!holder.getString(typeKey).equals("")&&NamespacedKey.fromString(holder.getString(typeKey))!=null) type=NamespacedKey.fromString(holder.getString(typeKey));
            if (!RpgRegistry.containsTypeConstructor(RpgEntity.class, type)) type = RpgObject.defaultTypeKey;
            holder.setRpgObject(RpgRegistry.getTypeConstructor(RpgEntity.class, type).apply(livingEntity));
        }
        return (RpgEntity) holder.getRpgObject();
    }

    public static void unloadInstance(LivingEntity livingEntity) {
        getHolder(livingEntity).unload();
    }


    protected final LivingEntity livingEntity;

    public RpgLivingEntity(LivingEntity livingEntity){
        this.livingEntity=livingEntity;
    }

    public LivingEntity getLivingEntity(){
        return livingEntity;
    }


    @Override
    public String getName() {
        return "RpgLivingEntity";
    }
}
