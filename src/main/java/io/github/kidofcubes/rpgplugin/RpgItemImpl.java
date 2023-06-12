package io.github.kidofcubes.rpgplugin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.persistence.CraftPersistentDataContainer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;


public class RpgItemImpl extends RPGImpl implements RpgItem{

    private static final Field itemMetaField;

    static {
        try {
            itemMetaField = ItemStack.class.getDeclaredField("meta");
            itemMetaField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasData(ItemStack itemstack){
        if(itemstack.getType().isEmpty()) return false;
        if(itemstack instanceof CraftItemStack craftItemStack){
            if(craftItemStack.handle.getTag()==null){
                return false;
            }
            if(!craftItemStack.handle.getTag().contains("PublicBukkitValues")){ //persistentDataContainer
                return false;
            }
            return ((CompoundTag)craftItemStack.handle.getTag().get("PublicBukkitValues")).contains(RPG.RPG_TAG_KEY.asString());
        }else{ //assume its default itemstack
            try {
                ItemMeta itemMeta = (ItemMeta) itemMetaField.get(itemstack);
                if(itemMeta==null){
                    return false;
                }
                return ((CraftPersistentDataContainer)itemMeta.getPersistentDataContainer()).getRaw().containsKey(RPG.RPG_TAG_KEY.asString());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @NotNull
    public static TagWrapper getHolder(ItemStack itemstack){
        if(itemstack.getType().isEmpty()) throw new IllegalArgumentException("Itemstack type cannot be empty");
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
            if(!tag.contains(RPG.RPG_TAG_KEY.asString())) tag.put(RPG.RPG_TAG_KEY.asString(), new CompoundTag());
//            System.out.println("THE TAGS NOW LOOK LIKE "+craftItemStack.handle.getTag().getAsString());
            return new TagWrapper((CompoundTag) Objects.requireNonNull(tag.get(RPG.RPG_TAG_KEY.asString())));
        }else{ //assume its default itemstack
            try {
//                System.out.println("IT WAS NOT A CRAFTITEMSTACK");
                ItemMeta itemMeta = (ItemMeta) itemMetaField.get(itemstack);
                if(itemMeta==null){
                    itemstack.setItemMeta(itemstack.getItemMeta());
                    itemMeta = (ItemMeta) itemMetaField.get(itemstack);
                }
                Map<String, Tag> tag = ((CraftPersistentDataContainer)itemMeta.getPersistentDataContainer()).getRaw();
                if(!tag.containsKey(RPG.RPG_TAG_KEY.asString())){
                    tag.put(RPG.RPG_TAG_KEY.asString(), new CompoundTag());
                }
                return new TagWrapper((CompoundTag) Objects.requireNonNull(tag.get(RPG.RPG_TAG_KEY.asString())));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public RpgItemImpl(ItemStack itemStack) {
        super(getHolder(itemStack));
    }

    public static RpgItem getRpg(ItemStack itemstack){
        TagWrapper tag = getHolder(itemstack);
        NamespacedKey type = RPG.DEFAULT_TYPE_KEY;
        String possibleKey = tag.getString(RPG.TYPE_KEY);
        if(!possibleKey.equalsIgnoreCase("")&&NamespacedKey.fromString(possibleKey)!=null) type=NamespacedKey.fromString(possibleKey);

        return RpgRegistry.getTypeConstructor(RpgItem.class, RpgRegistry.containsTypeConstructor(RpgItem.class, type) ? type : RPG.DEFAULT_TYPE_KEY).apply(itemstack);

    }
}