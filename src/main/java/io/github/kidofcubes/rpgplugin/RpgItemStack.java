package io.github.kidofcubes.rpgplugin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.persistence.CraftPersistentDataContainer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.*;


public class RpgItemStack extends RPGImpl implements RpgItem{

    private static final Field itemMetaField;

    static {
        try {
            itemMetaField = ItemStack.class.getDeclaredField("meta");
            itemMetaField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public RpgItemStack(CompoundTag base) {
        super(base);
    }

    public static RpgItemStack getRpg(ItemStack itemstack){

        if(itemstack instanceof CraftItemStack craftItemStack){
            if(craftItemStack.handle.getTag()==null){
                craftItemStack.handle.setTag(new CompoundTag());
            }
            if(!craftItemStack.handle.getTag().contains("PublicBukkitValues")){ //persistentDataContainer
                craftItemStack.handle.getTag().put("PublicBukkitValues",new CompoundTag());
            }
            CompoundTag tag = (CompoundTag) craftItemStack.handle.getTag().get("PublicBukkitValues");
//            System.out.println("WE ARE PUTTING IT IN PUBLIC BUKKIT VALUES");
            assert tag != null;
            if(tag.get(RPG_TAG_KEY.asString())==null){
                tag.put(RPG_TAG_KEY.asString(),new CompoundTag());
            }

            System.out.println("GETTING THE THING FROM craftITEMSTACK IS "+tag.get(RPG.RPG_TAG_KEY.asString()).getAsString());
            return new RpgItemStack((CompoundTag) tag.get(RPG_TAG_KEY.asString()));
        }else{ //assume its default itemstack
            try {
//                System.out.println("THERE IS A ITEMSTACK WTF");
                ItemMeta itemMeta = (ItemMeta) itemMetaField.get(itemstack);
                if(itemMeta==null){
//                    System.out.println("IT HAD NO ITEMMETA OH SHIT");
                    itemMeta=itemstack.getItemMeta();
                    itemstack.setItemMeta(itemMeta);
                }
                Map<String, Tag> tag = ((CraftPersistentDataContainer)itemMeta.getPersistentDataContainer()).getRaw();

                if(tag.get(RPG.RPG_TAG_KEY.asString())==null){
                    tag.put(RPG.RPG_TAG_KEY.asString(),new CompoundTag());
                }
                System.out.println("GETTING THE THING FROM ITEMSTACK IS "+tag.get(RPG.RPG_TAG_KEY.asString()).getAsString());
                return new RpgItemStack((CompoundTag) tag.get(RPG.RPG_TAG_KEY.asString()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}