package io.github.kidofcubes.managers;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import io.github.kidofcubes.RpgPlugin;
import io.github.kidofcubes.events.RpgEntityDamageByObjectEvent;
import io.github.kidofcubes.events.RpgEntityDamageEvent;
import io.github.kidofcubes.RpgEntity;
import io.github.kidofcubes.types.EntityRelation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import java.util.*;

import static io.github.kidofcubes.ExtraFunctions.damageToString;

public class EntityManager implements Listener {

    public static void init(){
        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity livingEntity: world.getLivingEntities()) {
                RpgManager.getRpgEntity(livingEntity);
            }
        }

        Bukkit.getScheduler().runTaskTimer(RpgPlugin.plugin, () -> {

            for (RpgEntity rpgEntity :
                    RpgManager.getAllRpgEntities().values()) {

                rpgEntity.updateInventoryStats();
            }
        },0,5);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageMonitor(RpgEntityDamageEvent event) {
        if(!event.isCancelled()) {
            if (event instanceof RpgEntityDamageByObjectEvent rpgEntityDamageByObjectEvent) {
                if (rpgEntityDamageByObjectEvent.getCause() instanceof RpgEntity attacker) {
                    attacker.setRelation(event.getEntity().getUUID(), EntityRelation.ENEMY);
                }
            }


            //MAKE DAMAGE THINGS
            if (event.getTotalDamage() > 0) {
                ServerLevel nmsWorld = ((CraftWorld)event.getEntity().getLivingEntity().getWorld()).getHandle();
                ArmorStand armorstand = new ArmorStand(EntityType.ARMOR_STAND, nmsWorld);
                armorstand.setInvisible(true);
                armorstand.setCustomNameVisible(true);
                armorstand.setSmall(true);
                armorstand.setNoBasePlate(true);
                armorstand.setMarker(true);
                armorstand.setCustomName(Component.literal(damageToString(event.getDamage())));

                Location eyeLocation = event.getEntity().getLivingEntity().getEyeLocation();
                Vector spawnpos = eyeLocation.add(Math.random() - 0.5, Math.random() + 0.5, Math.random() - 0.5).toVector();
                armorstand.setPos(spawnpos.getX(), spawnpos.getY(), spawnpos.getZ());
                ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(armorstand);
                ClientboundSetEntityDataPacket entityDataPacket = new ClientboundSetEntityDataPacket(armorstand.getId(), Objects.requireNonNullElse(armorstand.getEntityData().getNonDefaultValues(),List.of()));
                //why am i using packets?????????????? dunno so the armorstand doesnt exist server side maybe?

                List<ServerPlayerConnection> connections = new ArrayList<>();
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (other.getLocation().distance(eyeLocation) <= 32) {
                        ServerPlayerConnection connection = ((CraftPlayer) other).getHandle().connection;
                        connection.send(packet);
                        connection.send(entityDataPacket);
                        connections.add(connection);
                    }
                }

                Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, () -> {
                    ClientboundRemoveEntitiesPacket removeEntitiesPacket = new ClientboundRemoveEntitiesPacket(armorstand.getId());
                    for (ServerPlayerConnection connection :
                            connections) {
                        try {
                            connection.send(removeEntitiesPacket);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, (long)(1.5*20));
            }
        }


    }
    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event){
        Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, () -> RpgManager.getRpgEntity(event.getEntity()), 1);
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, () -> RpgManager.getRpgEntity(event.getPlayer()), 1);
    }

    @EventHandler
    public void onEntityRemoved(EntityRemoveFromWorldEvent event){
        if(event.getEntity() instanceof LivingEntity livingEntity) {
            RpgEntity rpgEntity = RpgManager.getRpgEntity(livingEntity.getUniqueId());
            if (rpgEntity != null) {
                rpgEntity.remove();
            }
        }
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(event.getEntity() instanceof Player player) {
            RpgEntity rpgEntity = RpgManager.getRpgEntity(player.getUniqueId());
            if (rpgEntity != null) {
                rpgEntity.remove();
            }
        }
    }

    @EventHandler
    public void onSwitchItem(PlayerItemHeldEvent event){
        Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, () -> updateInventoryStats(event.getPlayer()), 1);

    }
    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event){
        Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, () -> updateInventoryStats(event.getEntity()), 1);

    }


    private static void updateInventoryStats(LivingEntity livingEntity){
        RpgEntity rpgEntity = RpgManager.getRpgEntity(livingEntity);
        if(rpgEntity!=null){
            rpgEntity.updateInventoryStats();
        }
    }



}
