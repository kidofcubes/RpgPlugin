package io.github.kidofcubes.rpgplugin.world;

import io.github.kidofcubes.rpgplugin.EntityHolder;
import io.github.kidofcubes.rpgplugin.RpgEntityImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class RpgEntityLoader implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntitySpawn(EntitySpawnEvent event){
        ((EntityHolder)event.getEntity()).setRpg(RpgEntityImpl.getRpg(event.getEntity()));
    }
}
