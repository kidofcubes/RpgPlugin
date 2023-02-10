package io.github.kidofcubes;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public interface RpgItem extends RpgObject{
    default RpgItem loadFromJson(String json){
        return (RpgItem) RpgObject.super.loadFromJson(json);
    }
    default RpgItem loadFromJson(@NotNull JsonObject json){
        return (RpgItem) RpgObject.super.loadFromJson(json);
    }
    default RpgItem self(){
        return this;
    }
}