package io.github.kidofcubes.types;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public enum DamageType {
    PHYSICAL(new DamageCause[]{
            DamageCause.ENTITY_ATTACK,
            DamageCause.PROJECTILE,
            DamageCause.CONTACT,
            DamageCause.CRAMMING,
            DamageCause.ENTITY_EXPLOSION,
            DamageCause.BLOCK_EXPLOSION,
            DamageCause.FALLING_BLOCK,
            DamageCause.FALL,
            DamageCause.ENTITY_SWEEP_ATTACK,
            DamageCause.FLY_INTO_WALL
    }),
    MAGICAL(new DamageCause[]{

            DamageCause.MAGIC,
            DamageCause.CUSTOM,
            DamageCause.SUICIDE,
            DamageCause.DRAGON_BREATH,
    }),
    LIGHT(new DamageCause[]{}),
    DARKNESS(new DamageCause[]{
            DamageCause.WITHER,
            DamageCause.VOID
    }),
    EARTH(new DamageCause[]{
            DamageCause.POISON,
            DamageCause.STARVATION,
            DamageCause.THORNS,
    }),
    THUNDER(new DamageCause[]{
            DamageCause.LIGHTNING
    }),
    WATER(new DamageCause[]{
            DamageCause.FREEZE,
            DamageCause.DROWNING

    }),
    FIRE(new DamageCause[]{
            DamageCause.LAVA,
            DamageCause.MELTING,
            DamageCause.HOT_FLOOR,
            DamageCause.FIRE,
            DamageCause.FIRE_TICK

    }),
    AIR(new DamageCause[]{
            DamageCause.SUFFOCATION,
            DamageCause.DRYOUT
    });
    private final DamageCause[] fromDamageCauses;

    DamageType(final DamageCause[] damageCauses) {
        fromDamageCauses = damageCauses;
    }

    public static DamageType fromDamageCause(DamageCause cause) {
        for (DamageType type : DamageType.values()) {
            if (type.containsDamageCause(cause)) {
                return type;
            }
        }
        return MAGICAL;
    }

    public boolean containsDamageCause(DamageCause cause) {
        for (DamageCause fromDamageCause : fromDamageCauses) {
            if (fromDamageCause == cause) return true;
        }
        return false;
    }
    public final static DamageType[] values = DamageType.values();
}
