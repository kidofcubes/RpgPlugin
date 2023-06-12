package io.github.kidofcubes.rpgplugin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.persistence.CraftPersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RpgBlockImpl extends RPGImpl implements RpgBlock{

    public RpgBlockImpl(Block block){
        super(getHolder(block));
    }
    public static void removeData(Block block){
        ((CraftPersistentDataContainer)block.getChunk().getPersistentDataContainer()).getRaw().remove(block.getX()+"_"+block.getY()+"_"+block.getZ());
    }

    public static boolean hasData(Block block){
        return ((CraftPersistentDataContainer)block.getChunk().getPersistentDataContainer()).getRaw().containsKey(block.getX()+"_"+block.getY()+"_"+block.getZ());
    }
    public static TagWrapper getHolder(Block block){
        Map<String, Tag> map = ((CraftPersistentDataContainer)block.getChunk().getPersistentDataContainer()).getRaw();
        String key=block.getX()+"_"+block.getY()+"_"+block.getZ();
        if(!map.containsKey(key)) map.put(key, new CompoundTag());
        return new TagWrapper((CompoundTag) map.get(key));
//        if(!block.hasMetadata(RPG.RPG_TAG_KEY.asString())) block.setMetadata(RPG.RPG_TAG_KEY.asString(),new LazyMetadataValue(RpgRegistry.pluginInstance, CompoundTag::new));
//        return new TagWrapper((CompoundTag) block.getMetadata(RPG.RPG_TAG_KEY.asString()).get(0).value());
    }

    @NotNull
    public static RpgBlock getRpg(Block block) {

        TagWrapper tag = getHolder(block);
        NamespacedKey type = RPG.DEFAULT_TYPE_KEY;
        String possibleKey = tag.getString(RPG.TYPE_KEY);
        if(!possibleKey.equalsIgnoreCase("")&&NamespacedKey.fromString(possibleKey)!=null) type=NamespacedKey.fromString(possibleKey);

        return RpgRegistry.getTypeConstructor(RpgBlock.class, RpgRegistry.containsTypeConstructor(RpgBlock.class, type) ? type : RPG.DEFAULT_TYPE_KEY).apply(block);
    }
}
