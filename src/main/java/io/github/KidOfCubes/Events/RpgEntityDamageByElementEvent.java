package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import org.jetbrains.annotations.NotNull;

public class RpgEntityDamageByElementEvent extends RpgEntityDamageEvent{

    @NotNull
    private RpgElement cause;

    public RpgEntityDamageByElementEvent(@NotNull RpgEntity victim, double damage, @NotNull RpgElement cause) {
        super(victim, damage);
        this.cause = cause;
    }

    public @NotNull RpgElement getCause(){
        return cause;
    }
    public void setCause(@NotNull RpgElement cause){
        this.cause = cause;
    }
}
