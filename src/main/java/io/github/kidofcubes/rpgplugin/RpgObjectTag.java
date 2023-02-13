package io.github.kidofcubes.rpgplugin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static io.github.kidofcubes.rpgplugin.DynamicallySavedTag.getCallerMethodName;

public class RpgObjectTag extends CompoundTag {

    public static final NamespacedKey RpgObjectTagKey = new NamespacedKey("rpg_plugin", "rpg_object");
    public static final String dataKey = ("data");
    public static final String typeKey = ("type");

//    public RpgObjectTag(RpgObject rpgObject){
//        super();
//        setObject(rpgObject);
//        if(rpgObject.getRpgType()!=RpgObject.defaultTypeKey){
//            setSavedType(rpgObject.getRpgType());
//        }
//    }
    private RpgObjectTag(){
        super();
    }
    DynamicallySavedTag<RpgObject> rpgDataTag;

    @Nullable
    public RpgObject getObject(){
        if(rpgDataTag==null){
            return null;
        }
        return rpgDataTag.getObject();
    }

    public void setObject(RpgObject object){
        if(rpgDataTag==null){
            rpgDataTag = new DynamicallySavedTag<RpgObject>(object);
            put(dataKey,rpgDataTag);
        }
        rpgDataTag.setObject(object);
    }

    public void unload(){
        if(contains(dataKey)){
            ((DynamicallySavedTag<?>) Objects.requireNonNull(get(dataKey))).unload();
        }
    }


    public String getJson(){
        if(!contains(dataKey)){
            return "";
        }
        return ((DynamicallySavedTag<?>)(Objects.requireNonNull(get(dataKey)))).getJsonData();
    }

    public void setSavedType(NamespacedKey key){
        putString(typeKey,key.asString());
    }
    public NamespacedKey getSavedType(){
        if(contains(typeKey)){
            return NamespacedKey.fromString(getString(typeKey));
        }
        return RpgObject.defaultTypeKey;
    }

    public static RpgObjectTag fromCompoundTag(@Nullable CompoundTag compoundTag){
        if(compoundTag instanceof RpgObjectTag rpgObjectTag){
            return rpgObjectTag;
        }
        if(compoundTag!=null) {
            RpgObjectTag instance = new RpgObjectTag();
            if (compoundTag.contains(typeKey)) {
                instance.putString(typeKey, compoundTag.getString(typeKey));
            }
            if (compoundTag.contains(dataKey)) {
                if(compoundTag.get(dataKey) instanceof DynamicallySavedTag<?> dynamicallySavedTag) {
                    instance.put(dataKey, dynamicallySavedTag);
                }else {
                    instance.put(dataKey, new DynamicallySavedTag<RpgObject>(compoundTag.getByteArray(dataKey)));
                }
            }
            return instance;
        }
        return new RpgObjectTag();
    }
    private RpgObjectTag(Map<String,Tag> tagz){
        super(tagz);

    }

    @Override
    public CompoundTag copy() {
        // Paper start - reduce memory footprint of NBTTagCompound
        it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<String, Tag> ret = new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(this.tags.size(), 0.8f);
        java.util.Iterator<java.util.Map.Entry<String, Tag>> iterator = (this.tags instanceof it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap) ? ((it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap)this.tags).object2ObjectEntrySet().fastIterator() : this.tags.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Tag> entry = iterator.next();
            ret.put(entry.getKey(), entry.getValue().copy());
        }

        return new RpgObjectTag(ret);
        // Paper end - reduce memory footprint of NBTTagCompound
    }
}
