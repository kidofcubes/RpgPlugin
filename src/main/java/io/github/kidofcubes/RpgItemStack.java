package io.github.kidofcubes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.kidofcubes.managers.StatManager;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static io.github.kidofcubes.RpgObjectTag.RpgObjectTagKey;

public class RpgItemStack implements RpgItem{

    private static final Field itemMetaField;

    static {
        try {
            itemMetaField = ItemStack.class.getDeclaredField("meta");
            itemMetaField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setRpgType(NamespacedKey namespacedKey) {
        getHolder(itemStack).setSavedType(namespacedKey);
    }

    @Override
    public @NotNull NamespacedKey getRpgType() {
        return getHolder(itemStack).getSavedType();
    }

    @NotNull
    public static RpgObjectTag getHolder(ItemStack itemstack){
        if(itemstack instanceof CraftItemStack craftItemStack){
            if(craftItemStack.handle.getTag()==null){
                craftItemStack.handle.setTag(new CompoundTag());
            }
            CompoundTag tag = craftItemStack.handle.getTag();
            if(!(tag.get(RpgObjectTagKey.asString()) instanceof RpgObjectTag)){
                tag.put(RpgObjectTagKey.asString(), new RpgObjectTag((CompoundTag) tag.get(RpgObjectTagKey.asString())));
            }
            return (RpgObjectTag) Objects.requireNonNull(tag.get(RpgObjectTagKey.asString()));
        }else{ //assume its default itemstack
            try {
                ItemMeta itemMeta = (ItemMeta) itemMetaField.get(itemstack);
                if(itemMeta==null){
                    itemMeta=itemstack.getItemMeta();
                    itemstack.setItemMeta(itemMeta);
                }
                Map<String, Tag> tag = ((CraftPersistentDataContainer)itemMeta.getPersistentDataContainer()).getRaw();
                if(!(tag.get(RpgObjectTagKey.asString()) instanceof RpgObjectTag)){
                    tag.put(RpgObjectTagKey.asString(), new RpgObjectTag((CompoundTag) tag.get(RpgObjectTagKey.asString())));
                }
                return (RpgObjectTag) Objects.requireNonNull(tag.get(RpgObjectTagKey.asString()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @NotNull
    public static RpgItem getInstance(ItemStack itemStack) throws ClassNotFoundException { //if livingentity has a type already, init that type instead, if not, init default
        RpgObjectTag holder = getHolder(itemStack);
        if(holder.getObject()==null){ //init object if not found
            NamespacedKey type = holder.getSavedType();
            if(RpgRegistry.containsTypeConstructor(RpgItem.class,type)){
                holder.setObject(RpgRegistry.getTypeConstructor(RpgItem.class,type).apply(itemStack));
            }else{ //didnt find type function then init default
                holder.setObject(RpgRegistry.getTypeConstructor(RpgItem.class,RpgObject.defaultTypeKey).apply(itemStack));
            }
        }
        return (RpgItem) holder.getObject();
    }

    public static void unloadInstance(ItemStack itemstack) {
        getHolder(itemstack).unload();
    }

    private final ItemStack itemStack;
    private final UUID uuid;
    public RpgItemStack(ItemStack itemStack){
        this.itemStack=itemStack;
        uuid=UUID.randomUUID();
    }

    private int level=0;
    private double mana=0;

    Map<String,RpgClass> rpgClasses = new HashMap<>();

    Map<String,Stat> stats = new HashMap<>();

    @Override
    public String getName() {
        return "RPGOBJECT";
    }
    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level=level;
    }

    @Override
    public double getMana() {
        return mana;
    }

    @Override
    public void setMana(double mana) {
        this.mana=mana;
    }

    @Override
    public List<RpgClass> getRpgClasses() {
        return rpgClasses.values().stream().toList();
    }

    @Override
    public void addRpgClass(RpgClass rpgClass) {
        rpgClasses.put(rpgClass.getFullName(),rpgClass);
    }

    @Override
    public RpgClass getRpgClass(String rpgClass) {
        return rpgClasses.get(rpgClass);
    }

    @Override
    public void removeRpgClass(String rpgClass) {
        rpgClasses.remove(rpgClass);
    }

    @Override
    public boolean hasRpgClass(String rpgClass) {
        return rpgClasses.containsKey(rpgClass);
    }

    @Override
    public void use(RpgObject rpgObject) {

    }

    @Override
    public void stopUsing(RpgObject rpgObject) {

    }

    @Override
    public boolean usedBy(RpgObject rpgObject) {
        return false;
    }

    @Nullable
    @Override
    public RpgObject getParent() {
        return null;
    }

    @Override
    public void setParent(RpgObject parent) {

    }



    @Override
    public void addStat(Stat stat, boolean force) {
        stat.onAddStat(this);
        stat.onUseStat(this);
        stats.put(stat.getName(),stat);
    }

    @Override
    public boolean hasStat(String stat) {
        return stats.containsKey(stat);
    }

    @Override
    public @NotNull Stat getStat(String stat) {
        return stats.get(stat);
    }

    @Override
    public List<Stat> getStats() {
        return stats.values().stream().toList();
    }

    @Override
    public void removeStat(String stat) {
        System.out.println("REMOVING ITEM STAT");
        Stat statInstance = stats.remove(stat);
        if(statInstance!=null){
            statInstance.onRemoveStat();
            statInstance.onStopUsingStat();
        }
    }

    @Override
    public @NotNull Map<Class<? extends Stat>, List<Stat>> getUsedStats() {
        return Map.of();
    }

    @Override
    public RpgItem self() {
        return this;
    }
}