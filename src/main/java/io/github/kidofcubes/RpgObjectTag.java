package io.github.kidofcubes;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class RpgObjectTag extends CompoundTag {

    public static final NamespacedKey RpgObjectTagKey = new NamespacedKey("rpg_plugin", "rpg_object");
    public static final NamespacedKey dataKey = new NamespacedKey("rpg_plugin", "data");
    public static final NamespacedKey typeKey = new NamespacedKey("rpg_plugin", "type");

    public RpgObjectTag(RpgObject rpgObject){
        super();
        setObject(rpgObject);
        if(rpgObject.getRpgType()!=RpgObject.defaultTypeKey){
            setSavedType(rpgObject.getRpgType());
        }
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
            put(dataKey.asString(),rpgDataTag);
        }
        rpgDataTag.setObject(object);
    }

    public void unload(){
        if(contains(dataKey.asString())){
            ((DynamicallySavedTag<?>) Objects.requireNonNull(get(dataKey.asString()))).unload();
        }
    }


    public String getJson(){
        if(!contains(dataKey.asString())){
            return "";
        }
        return new String(getByteArray(dataKey.asString()), StandardCharsets.UTF_8);
    }

    public void setSavedType(NamespacedKey key){
        putString(typeKey.asString(),key.asString());
    }
    public NamespacedKey getSavedType(){
        if(contains(typeKey.asString())){
            return NamespacedKey.fromString(getString(typeKey.asString()));
        }
        return RpgObject.defaultTypeKey;
    }

    public RpgObjectTag(@Nullable CompoundTag compoundTag){
        super();
        if(compoundTag!=null) {
            if (compoundTag.contains(typeKey.asString())) {
                putString(typeKey.asString(), compoundTag.getString(typeKey.asString()));
            }
            if (compoundTag.contains(dataKey.asString())) {
                put(dataKey.asString(), new DynamicallySavedTag<RpgObject>(compoundTag.getByteArray(dataKey.asString())));
            }
        }
    }
}
