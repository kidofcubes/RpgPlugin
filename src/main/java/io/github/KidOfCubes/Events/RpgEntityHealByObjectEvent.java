package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgObject;
import io.github.KidOfCubes.RpgEntity;
import org.jetbrains.annotations.NotNull;

public class RpgEntityHealByObjectEvent extends RpgEntityHealEvent {//custom causeenum later?

    @NotNull
    private RpgObject cause;

    public RpgEntityHealByObjectEvent(@NotNull RpgEntity entity, double change, @NotNull RpgObject cause) {
        super(entity, change);
        setCause(cause);
    }


    public @NotNull RpgObject getCause(){
        return cause;
    }
    public void setCause(@NotNull RpgObject cause){
        this.cause = cause;
    }
}
