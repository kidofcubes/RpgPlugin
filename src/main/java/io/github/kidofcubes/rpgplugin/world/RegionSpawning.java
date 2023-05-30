package io.github.kidofcubes.rpgplugin.world;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import io.github.kidofcubes.rpgplugin.RpgPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.function.BiFunction;

public class RegionSpawning implements Listener {
    private final RegionContainer container;
    private final RpgPlugin pluginInstance;

    private static final Map<String, Set<Spawner>> spawners = new HashMap<>();

    private record Spawner(NamespacedKey key, BiFunction<Location, NamespacedKey, Entity> spawnFunction, float perChunkMax){};

    public RegionSpawning(RpgPlugin pluginInstance){
        this.pluginInstance=pluginInstance;
        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        Bukkit.getScheduler().runTaskTimer(pluginInstance, this::attemptSpawns, 0,20);
    }

    public static void registerSpawning(ProtectedRegion region, NamespacedKey id, BiFunction<Location, NamespacedKey, Entity> spawner, float perChunkMax){
        System.out.println("registered a spawning for "+region.getId());
        spawners.putIfAbsent(region.getId(),new HashSet<>());
        spawners.get(region.getId()).add(new Spawner(id,spawner,perChunkMax));
    }

    private final Random random = new Random();
    private final static Map<String, Integer> loadedChunks = new HashMap<>();

    private final static int chunkColumns = 5;

    //todo group spawning
    //todo hitbox checking
    private void attemptSpawns(){



//        System.out.println("I AM GOING TO ATTEMPT SPAWNS GAMERS");
        for(World world : Bukkit.getWorlds()){
            RegionManager manager = container.get(BukkitAdapter.adapt(world));
//            System.out.println("GOT REGION MANAGER "+manager);
            if(manager==null) continue;


            HashMap<String,Integer> tagCounts = new HashMap<>();
            for(LivingEntity livingEntity : world.getLivingEntities()){
                for(String tag : livingEntity.getScoreboardTags()){
                    tagCounts.putIfAbsent(tag,0);
                    tagCounts.put(tag, tagCounts.get(tag) + 1);
                }
            }

            Chunk[] chunks = world.getLoadedChunks();




//            System.out.println("ATTEMPTING SPAWNS IN WORLD "+world);



            List<Block> spawnableGrounds = new ArrayList<>();
            for(int chunkAttempt=0;chunkAttempt<chunks.length;chunkAttempt++) {
//                System.out.println("CHUNK ATTEMPT "+chunkAttempt);
//                Chunk chunk = chunks[random.nextInt(0, chunks.length)];
                Chunk chunk = chunks[chunkAttempt];
                for (int i = 0; i < chunkColumns; i++) {
                    if(chunk.getX()!=25) continue;
                    if(chunk.getZ()!=0) continue;
                    int x = random.nextInt(0, 16);
                    int z = random.nextInt(0, 16);
                    int y = world.getMinHeight();
                    int maxY = Math.min(world.getHighestBlockYAt((chunk.getX()*16)+x, (chunk.getZ()*16)+z, HeightMap.WORLD_SURFACE) + 3, world.getMaxHeight());
//                    System.out.println("Y is "+y+" MAX Y IS "+maxY);
                    int openSpace = 0;
                    while (y < maxY) {
                        Block block = chunk.getBlock(x, y, z);
                        if (block.getType().isOccluding()) {
                            openSpace = 0;
                        } else {
                            openSpace++;
                            if (openSpace == 2) {
//                                System.out.println("found a spawnable place at "+y);
                                spawnableGrounds.add(chunk.getBlock(x, y - 2, z));
                            }
                        }
                        y++;
                    }

                }


                for(int spawningAttempt=0;spawningAttempt<100;spawningAttempt++){
                    if(spawnableGrounds.size()==0) break;
                    int index = random.nextInt(0, spawnableGrounds.size());
                    Block block = spawnableGrounds.get(index);
//                    System.out.println("random index was "+index+" out of "+spawnableGrounds.size()+" and the block y is "+block.getY());
                    ApplicableRegionSet set = manager.getApplicableRegions(BlockVector3.at(block.getX()+0.5d,block.getY()+1, block.getZ()+0.5d), RegionQuery.QueryOption.COMPUTE_PARENTS);
                    List<Spawner> eligibleSpawners = new ArrayList<>();
                    for(ProtectedRegion region : set.getRegions()){
                        Set<Spawner> map = spawners.get(region.getId());
                        if(map!=null) eligibleSpawners.addAll(map);
                    }
//                    System.out.println("SPAWNING ATTEMPT "+spawningAttempt);
                    while(true){
                        if(eligibleSpawners.size()==0) break;

                        int i = random.nextInt(0,eligibleSpawners.size());
                        Spawner spawner = eligibleSpawners.get(i);
                        if(tagCounts.getOrDefault(spawner.key.asString(),0)>=spawner.perChunkMax){
                            eligibleSpawners.remove(i);
                            continue;
                        }else{
                            System.out.println("the tagcounts of "+spawner.key.asString()+" is "+tagCounts.getOrDefault(spawner.key.asString(),0));
                        }
                        Entity entity = spawner.spawnFunction.apply(new Location(world,block.getX()+0.5d,block.getY()+1, block.getZ()+0.5d),spawner.key);

                        if(entity!=null){
                            break;
                        }
                        else eligibleSpawners.remove(i);
                    }
                    spawnableGrounds.remove(index);
                }

                spawnableGrounds.clear();
            }


        }
    }


//    @EventHandler
//    public void onMobSpawn(CreatureSpawnEvent event){
//
//    }

//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onChunkLoad(ChunkLoadEvent event){
//        RegionManager manager = container.get(BukkitAdapter.adapt(event.getWorld()));
//
//    }
//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onChunkUnLoad(ChunkUnloadEvent event){
//    }

}
