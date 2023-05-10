package io.github.kidofcubes.rpgplugin;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.v1_19_R3.persistence.CraftPersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class RpgTileImpl extends RpgObjectImpl implements RpgTile {
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
    public static RpgObjectTag getHolder(TileState tileState){
        if(tileState.isSnapshot()) throw new IllegalArgumentException("Snapshot TileState unsupported!");
        CraftPersistentDataContainer persistentDataContainer = (CraftPersistentDataContainer) tileState.getPersistentDataContainer();
        if(!persistentDataContainer.getRaw().containsKey(RpgObjectTag.RpgObjectTagKey.asString())){
            persistentDataContainer.getRaw().put(RpgObjectTag.RpgObjectTagKey.asString(),new CompoundTag());
        }
        if (!(persistentDataContainer.getRaw().get(RpgObjectTag.RpgObjectTagKey.toString()) instanceof RpgObjectTag)) {
            persistentDataContainer.getRaw().put(RpgObjectTag.RpgObjectTagKey.toString(), RpgObjectTag.fromCompoundTag((CompoundTag) persistentDataContainer.getRaw().get(RpgObjectTag.RpgObjectTagKey.toString())));
        }
        return (RpgObjectTag) persistentDataContainer.getRaw().get(RpgObjectTag.RpgObjectTagKey.toString());
    }

    @NotNull
    public static RpgTile getInstance(TileState tileState) { //if the tile has a type already, init that type instead, if not, init default
        RpgObjectTag holder = getHolder(tileState);

        if (holder.getRpgObject() == null) {
            //init object if not found
            NamespacedKey type = RpgObject.defaultTypeKey;
            if(!holder.getString(typeKey).equals("")&&NamespacedKey.fromString(holder.getString(typeKey))!=null) type=NamespacedKey.fromString(holder.getString(typeKey));
            if (!RpgRegistry.containsTypeConstructor(RpgTile.class, type)) type = RpgObject.defaultTypeKey;
            holder.setRpgObject(RpgRegistry.getTypeConstructor(RpgTile.class, type).apply(tileState));
        }
        return (RpgTile) holder.getRpgObject();
    }

    public static void unloadInstance(TileState tileState) {
        getHolder(tileState).unload();
    }


    protected final TileState tileState;

    public RpgTileImpl(TileState tileState){
        this.tileState=tileState;
    }

    public TileState getTileState(){
        return tileState;
    }
}
