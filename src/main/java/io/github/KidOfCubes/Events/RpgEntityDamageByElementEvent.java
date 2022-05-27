package io.github.KidOfCubes.Events;

import io.github.KidOfCubes.RpgElement;
import io.github.KidOfCubes.RpgEntity;
import io.github.KidOfCubes.Types.DamageType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RpgEntityDamageByElementEvent extends RpgEntityDamageEvent{

    @NotNull
    private RpgElement cause;

    public RpgEntityDamageByElementEvent(@NotNull RpgEntity victim, DamageType type,double damage, @NotNull RpgElement cause) {
        super(victim, type, damage);
        this.cause = cause;
    }

    public RpgEntityDamageByElementEvent(@NotNull RpgEntity victim, Map<DamageType,Double> damage, @NotNull RpgElement cause) {
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
