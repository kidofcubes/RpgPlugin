package io.github.kidofcubes.events;

import io.github.kidofcubes.RpgEntity;
import io.github.kidofcubes.RpgObject;
import io.github.kidofcubes.Stat;
import io.github.kidofcubes.types.DamageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RpgEntityDamageByObjectEvent extends RpgEntityDamageEvent {

    @NotNull
    private RpgObject cause;

    public RpgEntityDamageByObjectEvent(@NotNull RpgEntity victim, DamageType type, double damage, @NotNull RpgObject cause) {
        super(victim, type, damage);
        this.cause = cause;
    }
    public RpgEntityDamageByObjectEvent(@NotNull RpgEntity victim, DamageType type, double damage, @NotNull RpgObject cause, @Nullable List<Stat> activationStats) {
        super(victim, type, damage, activationStats);
        this.cause = cause;
    }

    public RpgEntityDamageByObjectEvent(@NotNull RpgEntity victim, Map<DamageType, Double> damage, @NotNull RpgObject cause) {
        super(victim, damage, new ArrayList<>());
        this.cause = cause;
    }
    public RpgEntityDamageByObjectEvent(@NotNull RpgEntity victim, Map<DamageType, Double> damage, @NotNull RpgObject cause, @Nullable List<Stat> activationStats) {
        super(victim, damage,activationStats);
        this.cause = cause;
    }

    public @NotNull
    RpgObject getCause() {
        return cause;
    }

    public void setCause(@NotNull RpgObject cause) {
        this.cause = cause;
    }
}
