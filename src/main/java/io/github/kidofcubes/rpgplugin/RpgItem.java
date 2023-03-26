package io.github.kidofcubes.rpgplugin;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public interface RpgItem extends RpgObject{
    default RpgItem getRpgInstance(){
        return this;
    }
}