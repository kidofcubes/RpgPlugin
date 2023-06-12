package io.github.kidofcubes.rpgplugin.world;

import io.github.kidofcubes.rpgplugin.RPG;
import io.github.kidofcubes.rpgplugin.RpgBlockImpl;
import io.github.kidofcubes.rpgplugin.RpgItem;
import io.github.kidofcubes.rpgplugin.RpgItemImpl;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class BlockSaving implements Listener {
    private static final NamespacedKey chunkBlocksKey = new NamespacedKey("rpgplugin","blocksdata");
    private final Plugin plugin;

    public BlockSaving(Plugin plugin){
        this.plugin=plugin;
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event){
        if(event.isCancelled()) return;
        if(event.getPlayer().getGameMode()!=GameMode.CREATIVE&&event.isDropItems()) {
            Block origBlock = event.getBlock();
            if (!RpgBlockImpl.hasData(origBlock)) return;
            Collection<ItemStack> items = (origBlock.getDrops(event.getPlayer().getEquipment().getItemInMainHand(), event.getPlayer()));
            boolean dropsSelf = false;

            for (ItemStack itemStack : items) {
                if (itemStack.getType().equals(origBlock.getType())) {
                    dropsSelf = true;
                    System.out.println("WE ARE MERGINGN "+RpgBlockImpl.getHolder(origBlock).copy()+" INTO "+RpgItemImpl.getHolder(itemStack));
                    RpgItemImpl.getHolder(itemStack).merge(RpgBlockImpl.getHolder(origBlock).copy());
                }
            }
            if (dropsSelf) {
                event.setDropItems(false);

                for (ItemStack itemStack : items) {
                    origBlock.getWorld().dropItemNaturally(origBlock.getLocation(), itemStack);
                }
            }
        }
        RpgBlockImpl.removeData(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event){
        if(event.isCancelled()) return;
        ItemStack itemStack = event.getItemInHand();
        Block block = event.getBlock();
        if(!itemStack.getType().equals(block.getType())) return;
        if(!RpgItemImpl.hasData(itemStack)) return;
        System.out.println("putting data "+RpgItemImpl.getHolder(itemStack).copy()+" in "+RpgBlockImpl.getHolder(block));
        RpgBlockImpl.getHolder(block).merge(RpgItemImpl.getHolder(itemStack).copy());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreakBlock(BlockBreakBlockEvent event){
        if(!RpgBlockImpl.hasData(event.getBlock())) return;
        for (ItemStack itemStack : event.getDrops()) {
//            System.out.println("IT "+event.getBlock()+" WILL DROP "+itemStack);
            if (itemStack.getType().equals(event.getBlock().getType())) {
//                System.out.println(" INTO "+itemStack+" WHICH IS "+RpgItemImpl.getHolder(itemStack));
                RpgItemImpl.getHolder(itemStack).merge(RpgBlockImpl.getHolder(event.getBlock()).copy());
//                System.out.println(" INTO "+itemStack+" WHICH IS "+RpgItemImpl.getHolder(itemStack));
            }
        }
    }

//    @EventHandler
//    public void onChunkLoad(ChunkLoadEvent event){
//        System.out.println("LOADING CHUNK "+event.getChunk());
//        Chunk chunk = event.getChunk();
//        CraftPersistentDataContainer persistentDataContainer = (CraftPersistentDataContainer) chunk.getPersistentDataContainer();
//        if(!persistentDataContainer.getRaw().containsKey(chunkBlocksKey.asString())) return;
//        CompoundTag tag = (CompoundTag) persistentDataContainer.getRaw().get(chunkBlocksKey.asString());
//        for(String key : tag.getAllKeys()){
//            String[] positions = key.split("_");
//            chunk.getBlock(Integer.parseInt(positions[0]),Integer.parseInt(positions[1]),Integer.parseInt(positions[2]))
//                    .setMetadata(RPG.RPG_TAG_KEY.asString(), new LazyMetadataValue(plugin, LazyMetadataValue.CacheStrategy.CACHE_AFTER_FIRST_EVAL,() -> tag.getCompound(key)));
//        }
//    }
//
//    @EventHandler
//    public void onChunkUnload(ChunkUnloadEvent event){
//        System.out.println("SAVING CHUNK "+event.getChunk());
//        Chunk chunk = event.getChunk();
//        CraftPersistentDataContainer persistentDataContainer = (CraftPersistentDataContainer) chunk.getPersistentDataContainer();
//        persistentDataContainer.getRaw().putIfAbsent(chunkBlocksKey.asString(), new CompoundTag());
//        CompoundTag tag = (CompoundTag) persistentDataContainer.getRaw().get(chunkBlocksKey.asString());
//        for(int x=0;x<16;x++){
//            for(int z=0;z<16;z++){
//                for(int y=chunk.getWorld().getMinHeight();y<chunk.getWorld().getMaxHeight();y++){
//                    Block block = chunk.getBlock(x,y,z);
//                    if(block.hasMetadata(RPG.RPG_TAG_KEY.asString())){
//                        tag.put(x+"_"+y+"_"+z, (CompoundTag) Objects.requireNonNull(block.getMetadata(RPG.RPG_TAG_KEY.asString()).get(0).value()));
//                    }
//                }
//            }
//        }
//    }
}
