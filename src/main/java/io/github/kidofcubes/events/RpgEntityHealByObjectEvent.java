package io.github.kidofcubes.events;

import io.github.kidofcubes.RpgEntity;
import io.github.kidofcubes.RpgObject;
import org.jetbrains.annotations.NotNull;

public class RpgEntityHealByObjectEvent extends RpgEntityHealEvent {//custom causeenum later?

    @NotNull
    private RpgObject cause;

    public RpgEntityHealByObjectEvent(@NotNull RpgEntity entity, double change, @NotNull RpgObject cause) {
        super(entity, change);
        setCause(cause);
    }


    public @NotNull
    RpgObject getCause() {
        return cause;
    }

    public void setCause(@NotNull RpgObject cause) {
        this.cause = cause;
    }
}
