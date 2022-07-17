package io.github.kidofcubes.events;

import io.github.kidofcubes.RpgEntity;
import io.github.kidofcubes.RpgObject;
import io.github.kidofcubes.Stat;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RpgEntityHealByObjectEvent extends RpgEntityHealEvent {//custom causeenum later?

    @NotNull
    private RpgObject cause;

    public RpgEntityHealByObjectEvent(@NotNull RpgEntity entity, double change, @NotNull RpgObject cause) {
        this(entity, change,cause,null);
    }
    public RpgEntityHealByObjectEvent(@NotNull RpgEntity entity, double change, @NotNull RpgObject cause, List<Stat> activateStats){
        super(entity,change,activateStats);
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
