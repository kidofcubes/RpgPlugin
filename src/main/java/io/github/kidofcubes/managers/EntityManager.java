package io.github.kidofcubes.managers;

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
import net.minecraft.util.datafix.fixes.EntityEquipmentToArmorAndHandFix;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.inventory.InventoryMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import java.util.*;

import static io.github.kidofcubes.ExtraFunctions.damageToString;

public class EntityManager implements Listener {

    public static void init(){
        Bukkit.getScheduler().runTaskTimer(RpgPlugin.plugin, () -> {

            for (RpgEntity rpgEntity :
                    RpgManager.getAllRpgEntities().values()) {
                rpgEntity.updateInventoryStats();
            }
        },0,5);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageMoniter(RpgEntityDamageEvent event) {
        if(!event.isCancelled()) {
            if (event instanceof RpgEntityDamageByObjectEvent rpgEntityDamageByObjectEvent) {
                if (rpgEntityDamageByObjectEvent.getCause() instanceof RpgEntity attacker) {
                    attacker.setRelation(event.getEntity().getUUID(), EntityRelation.Enemy);
                }
            }


            //MAKE DAMAGE THINGS
            if (event.getTotalDamage() > 0) {
                long startTime = System.nanoTime();
                ServerLevel nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
                ArmorStand armorstand = new ArmorStand(EntityType.ARMOR_STAND, nmsWorld);
                armorstand.setInvisible(true);
                armorstand.setCustomNameVisible(true);
                armorstand.setSmall(true);
                armorstand.setNoBasePlate(true);
                armorstand.setCustomName(Component.literal(damageToString(event.getDamage())));

                Location eyeLocation = event.getEntity().getLivingEntity().getEyeLocation();
                Vector spawnpos = eyeLocation.add(Math.random() - 0.5, Math.random() + 0.5, Math.random() - 0.5).toVector(); //high up because players could accidentally hit the armorstand
                armorstand.setPos(spawnpos.getX(), spawnpos.getY(), spawnpos.getZ());
                ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(armorstand);
                ClientboundSetEntityDataPacket entityDataPacket = new ClientboundSetEntityDataPacket(armorstand.getId(), armorstand.getEntityData(), false);
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
                        connection.send(removeEntitiesPacket);
                    }
                }, (long)(1.5*20));
            }
        }


    }
    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event){
        Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, () -> {
            System.out.println("initiated a entity which spawned "+event.getEntity());
            RpgManager.getRpgEntity(event.getEntity());
        }, 1);
    }

    @EventHandler
    public void onSwitchItem(PlayerItemHeldEvent event){
        Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, () -> {
            updateInventoryStats(event.getPlayer());
        }, 1);

    }
    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event){

        Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, () -> {
            updateInventoryStats(event.getEntity());
        }, 1);

    }


    private static void updateInventoryStats(LivingEntity livingEntity){
        RpgManager.getRpgEntity(livingEntity).updateInventoryStats();
    }

/*    private Map<UUID,Integer> equipmentHashes = new HashMap<>();
    public void updateInventories(){
        for (RpgEntity rpgEntity : RpgManager.getAllRpgEntities().values()) {
            Integer original = equipmentHashes.getOrDefault(rpgEntity.getUUID(),Integer.MIN_VALUE);
            if(original==Integer.MIN_VALUE){
                rpgEntity
            }
            rpgEntity.getLivingEntity().getEquipment()
        }
    }*/


}
