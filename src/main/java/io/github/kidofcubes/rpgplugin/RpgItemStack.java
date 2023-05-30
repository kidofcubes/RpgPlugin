package io.github.kidofcubes.rpgplugin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.persistence.CraftPersistentDataContainer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

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
//        System.out.println("START OF GET HOLDER ON A "+itemstack+"=====================================================");
//        try { throw new NullPointerException(); }catch (NullPointerException exception){exception.printStackTrace();}

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
//            System.out.println("THE BUKKITVALUES WERE "+tag.getAsString());
            if(!(tag.get(RpgObjectTag.RpgObjectTagKey.asString()) instanceof RpgObjectTag)){

                tag.put(RpgObjectTag.RpgObjectTagKey.asString(), RpgObjectTag.fromCompoundTag((CompoundTag) tag.get(RpgObjectTag.RpgObjectTagKey.asString())));

            }
//            System.out.println("THE TAGS NOW LOOK LIKE "+craftItemStack.handle.getTag().getAsString());
//            System.out.println("ENDDDDDDDD OF GET HOLDER ON A CRAFTITEMSTACK WHICH HAS META? "+craftItemStack.handle.getTag().isEmpty()+
//                    " WHICH IS HOLDING A "+((RpgObjectTag) Objects.requireNonNull(((CompoundTag) craftItemStack.handle.getTag().get("PublicBukkitValues")).get(RpgObjectTag.RpgObjectTagKey.asString()))).getRpgObject()+
//                    "==================================================================================");
            return (RpgObjectTag) Objects.requireNonNull(tag.get(RpgObjectTag.RpgObjectTagKey.asString()));
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
//                System.out.println("tags: ");
                for(Map.Entry<String,Tag> entry : tag.entrySet()){
//                    System.out.println("TAG: "+entry.getKey()+":"+entry.getValue().getAsString());
                }
//                System.out.println("end of tags");

                if(!(tag.get(RpgObjectTag.RpgObjectTagKey.asString()) instanceof RpgObjectTag)){
//                    System.out.println("IT WAS NOT A RPGOBJECTTAG ITEMSTACK IT WAS "+tag.get(RpgObjectTag.RpgObjectTagKey.asString()).getClass()+" AND "+tag.get(RpgObjectTag.RpgObjectTagKey.asString()).getAsString());
//                    System.out.println("IT WAS NOT A RPGOBJECTTAG ITEMSTACK");
                    tag.put(RpgObjectTag.RpgObjectTagKey.asString(), RpgObjectTag.fromCompoundTag((CompoundTag) tag.get(RpgObjectTag.RpgObjectTagKey.asString())));
                }else{
//                    System.out.println("IT WAS A RPGOBJECT TAG LETS GO "+(RpgObjectTag) Objects.requireNonNull(tag.get(RpgObjectTag.RpgObjectTagKey.asString())));
                }
//                System.out.println("ENDDDDDDDD OF GET HOLDER WITH RpgObjectTag@"+Objects.requireNonNull(tag.get(RpgObjectTag.RpgObjectTagKey.asString())).hashCode()+" HOLDING A "+
//                        ((RpgObjectTag) Objects.requireNonNull(tag.get(RpgObjectTag.RpgObjectTagKey.asString()))).getRpgObject()+"----------------------------------------------------");
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
//        System.out.println("so the holder is RpgObjectTag@"+holder.hashCode()+" and is holding "+holder.getRpgObject());
        if(holder.getRpgObject()==null){ //init object if not found
            NamespacedKey type = RpgObject.defaultTypeKey;
            if(!holder.getString(typeKey).equals("")&&NamespacedKey.fromString(holder.getString(typeKey))!=null) type=NamespacedKey.fromString(holder.getString(typeKey));
            if (!RpgRegistry.containsTypeConstructor(RpgItem.class, type)) type = RpgObject.defaultTypeKey;
            RpgItem item = (RpgItem) RpgRegistry.getTypeConstructor(RpgItem.class,type).apply(itemStack);
            System.out.println("WE INITED A NEW ITEM ON "+itemStack+" ON A HOLDER THAT "+holder.isClone());
//            System.out.println("WAS IT OKAY ITEM? "+(item!=null)+" and "+item.getClass()+" and "+item);
//            try {
//                throw new NullPointerException();
//            }catch (NullPointerException exception){
//                exception.printStackTrace();
//            }

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