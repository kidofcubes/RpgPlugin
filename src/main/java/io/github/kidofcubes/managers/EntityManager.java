package io.github.kidofcubes.managers;

import io.github.kidofcubes.events.RpgEntityDamageByObjectEvent;
import io.github.kidofcubes.events.RpgEntityDamageEvent;
import io.github.kidofcubes.RpgEntity;
import io.github.kidofcubes.types.EntityRelation;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static io.github.kidofcubes.ExtraFunctions.damageToString;

public class EntityManager implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageMoniter(RpgEntityDamageEvent event) {
        if (event instanceof RpgEntityDamageByObjectEvent rpgEntityDamageByObjectEvent) {
            if (rpgEntityDamageByObjectEvent.getCause() instanceof RpgEntity attacker) {
                attacker.setRelation(event.getEntity().getUUID(), EntityRelation.Enemy);
            }
        }
        if (event.getTotalDamage() > 0) {
            long startTime = System.nanoTime();
            ServerLevel nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
            ArmorStand armorstand = new ArmorStand(EntityType.ARMOR_STAND, nmsWorld);
            armorstand.setInvisible(true);
            armorstand.setCustomNameVisible(true);
            armorstand.setSmall(true);
            armorstand.setNoBasePlate(true);
            armorstand.setCustomName(new TextComponent(damageToString(event.getDamage())));

            Location eyeLocation = event.getEntity().livingEntity.getEyeLocation();
            Vector spawnpos = eyeLocation.add(Math.random() - 0.5, Math.random() + 0.5, Math.random() - 0.5).toVector();
            armorstand.setPos(spawnpos.getX(), spawnpos.getY(), spawnpos.getZ());
            ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(armorstand);
            ClientboundSetEntityDataPacket entityDataPacket = new ClientboundSetEntityDataPacket(armorstand.getId(), armorstand.getEntityData(), false);

            List<ServerPlayerConnection> connections = new ArrayList<>();
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.getLocation().distance(eyeLocation) <= 32) {
                    ServerPlayerConnection connection = ((CraftPlayer) other).getHandle().connection;
                    connection.send(packet);
                    connection.send(entityDataPacket);
                    connections.add(connection);

                }
            }
            new java.util.Timer().schedule( //optimise
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
        }


    }


}
