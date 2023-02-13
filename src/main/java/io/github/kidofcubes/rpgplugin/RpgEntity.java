package io.github.kidofcubes.rpgplugin;


import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

//add entity specific stuff here later
public interface RpgEntity extends RpgObject {

    default RpgEntity loadFromJson(String json){
        return (RpgEntity) RpgObject.super.loadFromJson(json);
    }
    default RpgEntity loadFromJson(@NotNull JsonObject json){
        return (RpgEntity) RpgObject.super.loadFromJson(json);
    }

    default RpgEntity getRpgInstance(){
        return this;
    }
}
