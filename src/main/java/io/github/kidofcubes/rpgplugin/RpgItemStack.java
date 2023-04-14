package io.github.kidofcubes.rpgplugin;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;


public class RpgItemStack extends RpgObjectImpl implements RpgItem{

    private static final Field itemMetaField;

    static {
        try {
            itemMetaField = ItemStack.class.getDeclaredField("meta");
            itemMetaField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static RpgObjectTag getHolder(ItemStack itemstack){
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
            if(!(tag.get(RpgObjectTag.RpgObjectTagKey.asString()) instanceof RpgObjectTag)){
                System.out.println("IT WAS NOT A RPGOBJECTTAG");
                tag.put(RpgObjectTag.RpgObjectTagKey.asString(), RpgObjectTag.fromCompoundTag((CompoundTag) tag.get(RpgObjectTag.RpgObjectTagKey.asString())));
            }
//            System.out.println("THE TAGS NOW LOOK LIKE "+craftItemStack.handle.getTag().getAsString());
            return (RpgObjectTag) Objects.requireNonNull(tag.get(RpgObjectTag.RpgObjectTagKey.asString()));
        }else{ //assume its default itemstack
            try {
//                System.out.println("IT WAS NOT A CRAFTITEMSTACK");
                ItemMeta itemMeta = (ItemMeta) itemMetaField.get(itemstack);
                if(itemMeta==null){
                    itemMeta=itemstack.getItemMeta();
                    itemstack.setItemMeta(itemMeta);
                }
                Map<String, Tag> tag = ((CraftPersistentDataContainer)itemMeta.getPersistentDataContainer()).getRaw();
                if(!tag.containsKey(RpgObjectTag.RpgObjectTagKey.asString())){
                    tag.put(RpgObjectTag.RpgObjectTagKey.asString(), new CompoundTag());
                }
                if(!(tag.get(RpgObjectTag.RpgObjectTagKey.asString()) instanceof RpgObjectTag)){
                    tag.put(RpgObjectTag.RpgObjectTagKey.asString(), RpgObjectTag.fromCompoundTag((CompoundTag) tag.get(RpgObjectTag.RpgObjectTagKey.asString())));
                }
                return (RpgObjectTag) Objects.requireNonNull(tag.get(RpgObjectTag.RpgObjectTagKey.asString()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @NotNull
    public static RpgItem getInstance(ItemStack itemStack) throws ClassNotFoundException { //if livingentity has a type already, init that type instead, if not, init default
        if(itemStack.getType().isEmpty()){
            throw new RuntimeException("Cant attach a RpgItemStack on "+itemStack.getType());
        }
        RpgObjectTag holder = getHolder(itemStack);
        if(holder.getRpgObject()==null){ //init object if not found
            NamespacedKey type = RpgObject.defaultTypeKey;
            if(!holder.getString(typeKey).equals("")&&NamespacedKey.fromString(holder.getString(typeKey))!=null) type=NamespacedKey.fromString(holder.getString(typeKey));
            if (!RpgRegistry.containsTypeConstructor(RpgItem.class, type)) type = RpgObject.defaultTypeKey;
            RpgItem item = (RpgItem) RpgRegistry.getTypeConstructor(RpgItem.class,type).apply(itemStack);
            System.out.println("WE INITED A NEW ITEM ON "+itemStack.getType()+" ON A HOLDER THAT "+holder.isClone());
            holder.setRpgObject(item);
        }
        return (RpgItem) holder.getRpgObject();
    }

    public static void unloadInstance(ItemStack itemstack) {
        getHolder(itemstack).unload();
    }

    protected final ItemStack itemStack;
    private final UUID uuid;
    public RpgItemStack(ItemStack itemStack){
        if(itemStack.getType().isEmpty()){
            throw new RuntimeException("Cant init a RpgItemStack on "+itemStack.getType());
        }
        this.itemStack=itemStack;
        uuid=UUID.randomUUID();
    }

    public ItemStack getItemStack(){
        return itemStack;
    }


//    @Override
//    public JsonObject toJson() {
//        JsonObject jsonObject = RpgItem.super.toJson();
//        jsonObject.remove("type");
//        if(getLevel()==0){
//            jsonObject.remove("level");
//        }
//        if(getMana()==0){
//            jsonObject.remove("mana");
//        }
//        return jsonObject;
//    }
//
//    @Override
//    public RpgItem loadFromJson(@NotNull JsonObject jsonObject) {
//        jsonObject.remove("type");
//        return RpgItem.super.loadFromJson(jsonObject);
//    }

    @Override
    public RpgItem getRpgInstance() {
        return this;
    }
}