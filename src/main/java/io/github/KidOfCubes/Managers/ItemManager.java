package io.github.KidOfCubes.Managers;

import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.RpgItem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemManager implements Listener {


    public static Map<ItemStack, RpgItem> RpgItems = new HashMap<>();

    @EventHandler
    public void onPickup(EntityPickupItemEvent event){
        if(event.getEntity() instanceof Player player){
        }
    }

}
