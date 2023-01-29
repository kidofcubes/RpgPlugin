package io.github.kidofcubes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RpgObjectHolder {
    public void setObject(RpgObject thing);

    @Nullable
    public RpgObject getObject();

    @Nullable
    public String getJsonData();
}
