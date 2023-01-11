package io.github.kidofcubes;

import com.google.gson.JsonObject;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class RpgItemTagType implements PersistentDataType<byte[], RpgItem> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<RpgItem> getComplexType() {
        return RpgItem.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull RpgItem complex, @NotNull PersistentDataAdapterContext context) {
        return null;
//        return (RpgPlugin.gson.toJson(complex.toJson())).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public @NotNull RpgItem fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        return null;
//        return (RpgItem) new BaseRpgItem().loadFromJson(RpgPlugin.gson.fromJson(new String(primitive), JsonObject.class));
    }
}
