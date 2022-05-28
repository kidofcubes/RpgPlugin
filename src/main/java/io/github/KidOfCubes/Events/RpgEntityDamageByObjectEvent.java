package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgObject;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.Types.DamageType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RpgEntityDamageByObjectEvent extends RpgEntityDamageEvent{

    @NotNull
    private RpgObject cause;

    public RpgEntityDamageByObjectEvent(@NotNull RpgEntity victim, DamageType type, double damage, @NotNull RpgObject cause) {
        super(victim, type, damage);
        this.cause = cause;
    }

    public RpgEntityDamageByObjectEvent(@NotNull RpgEntity victim, Map<DamageType,Double> damage, @NotNull RpgObject cause) {
        super(victim, damage);
        this.cause = cause;
    }

    public @NotNull RpgObject getCause(){
        return cause;
    }
    public void setCause(@NotNull RpgObject cause){
        this.cause = cause;
    }
}
