package io.github.KidOfCubes.Managers;

import io.github.KidOfCubes.Events.RpgEntityDamageByElementEvent;
import io.github.KidOfCubes.Events.RpgEntityDamageEvent;
import io.github.KidOfCubes.Events.RpgTickEvent;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.*;

import static io.github.KidOfCubes.RpgPlugin.key;
import static io.github.KidOfCubes.RpgPlugin.logger;

public class EntityManager implements Listener {


    public static Map<UUID, RpgEntity> RpgEntities = new HashMap<>();
    public static Map<UUID, RpgEntity> TempRpgEntities = new HashMap<>();



/*    @EventHandler
    public void onTick(RpgTickEvent tickEvent){
        for (RpgEntity rpgEntity : RpgEntities.values()) {
            rpgEntity.activateStat(tickEvent);
        }
        for (RpgEntity rpgEntity : TempRpgEntities.values()) {
            rpgEntity.activateStat(tickEvent);
        }
    }*/

    public static void close(){
        for (RpgEntity rpgEntity :
                TempRpgEntities.values()) {
            rpgEntity.livingEntity.remove();
            RpgEntities.remove(rpgEntity.livingEntity.getUniqueId());
        }
        for (RpgEntity rpgEntity :
                RpgEntities.values()) {
            rpgEntity.livingEntity.getPersistentDataContainer().set(key, PersistentDataType.STRING,rpgEntity.toJson());
        }

    }


    public static RpgEntity getRpgEntity(LivingEntity livingEntity){
        if(RpgEntities.containsKey(livingEntity.getUniqueId())){
            return RpgEntities.get(livingEntity.getUniqueId());
        }else{
            if(livingEntity.getPersistentDataContainer().has(key)){
                RpgEntity newEntity = new RpgEntity(livingEntity, RpgEntity.fromJson(livingEntity.getPersistentDataContainer().get(key,PersistentDataType.STRING)));
                RpgEntities.put(livingEntity.getUniqueId(),newEntity);
                return newEntity;
            }
            RpgEntity newEntity = new RpgEntity(livingEntity);
            RpgEntities.put(livingEntity.getUniqueId(),newEntity);
            return newEntity;
        }
    }
    public static RpgEntity getRpgEntity(UUID uuid){
        if(RpgEntities.containsKey(uuid)){
            return RpgEntities.get(uuid);
        }else{
            if(Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity) {
                if (livingEntity.getPersistentDataContainer().has(key)) {
                    RpgEntity newEntity = new RpgEntity(livingEntity, RpgEntity.fromJson(livingEntity.getPersistentDataContainer().get(key, PersistentDataType.STRING)));
                    RpgEntities.put(livingEntity.getUniqueId(), newEntity);
                    return newEntity;
                }
                RpgEntity newEntity = new RpgEntity(livingEntity);
                RpgEntities.put(livingEntity.getUniqueId(), newEntity);
                return newEntity;
            }else{
                throw new IllegalArgumentException("UUID "+uuid+" was either not a livingentity or was invalid");
            }
        }
    }


/*    @EventHandler
    public void onDamageByEntity(RpgEntityDamageByEntityEvent event){
        if(event.getAttacker() instanceof RpgEntity entity){
            entity.addTarget(event.getEntity());
        }
        event.getAttacker().activateStat(event);
    }*/

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamage(RpgEntityDamageEvent event){
        //event.getEntity().activateStat(event);

    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageMoniter(RpgEntityDamageEvent event) {
        if(event instanceof RpgEntityDamageByElementEvent rpgEntityDamageByElementEvent){
            if(rpgEntityDamageByElementEvent.getCause() instanceof RpgEntity attacker){
                attacker.addTarget(event.getEntity());
            }
        }
/*                ArmorStand test = (ArmorStand) livingEntity.getWorld().spawnEntity(
                livingEntity.getEyeLocation().add(
                        (Math.random() - 0.5),
                        (Math.random() - 0.5),
                        (Math.random() - 0.5)
                ), EntityType.ARMOR_STAND);
        test.setCanTick(false);
        test.setVisible(false);
        test.setCustomNameVisible(true);
        test.customName(Component.text("-" + event.getDamage()).color(TextColor.color(255, 0, 0)).append(Component.text("\u2764").color(TextColor.color(255, 0, 0))));
        Bukkit.getScheduler().runTaskLater(RpgPlugin.plugin, test::remove, 20);*/
        long startTime = System.nanoTime();
        ServerLevel nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
        ArmorStand armorstand = new ArmorStand(EntityType.ARMOR_STAND, nmsWorld);
        armorstand.setInvisible(true);
        armorstand.setCustomNameVisible(true);
        armorstand.setSmall(true);
        armorstand.setNoBasePlate(true);
        armorstand.setCustomName(new TextComponent(ChatColor.RED+ "-" + event.getDamage()));

        Location eyeLocation = event.getEntity().livingEntity.getEyeLocation();
        Vector spawnpos = eyeLocation.add(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).toVector();
        armorstand.setPos(spawnpos.getX(), spawnpos.getY(), spawnpos.getZ());
        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(armorstand);
        ClientboundSetEntityDataPacket entityDataPacket = new ClientboundSetEntityDataPacket(armorstand.getId(),armorstand.getEntityData(),false);

        List<ServerPlayerConnection> connections = new ArrayList<>();
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.getLocation().distance(eyeLocation) <= 32) {
                //logger.info("found a player "+other.name());
                ServerPlayerConnection connection = ((CraftPlayer) other).getHandle().connection;
                connection.send(packet);
                connection.send(entityDataPacket);
                connections.add(connection);

            }
        }
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(armorstand.getId());
                        for (ServerPlayerConnection connection :
                                connections) {
                            connection.send(packet);
                        }
                    }
                },
                1500
        );
        long endTime = System.nanoTime();
        double duration = (endTime - startTime)/1000000.0;
        //logger.info("health tag took "+duration);


    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(RpgEntities.containsKey(event.getEntity().getUniqueId())){
            RpgEntities.remove(event.getEntity().getUniqueId());
        }
        if(TempRpgEntities.containsKey(event.getEntity().getUniqueId())){
            TempRpgEntities.remove(event.getEntity().getUniqueId());
        }
        //logger.info("ENTITY DIED "+event.getEntity().getName()+" AND IS CANCELLED "+event.isCancelled());
        //event.getEntity().setHealth(0);
        //event.getEntity().remove();
    }




}
