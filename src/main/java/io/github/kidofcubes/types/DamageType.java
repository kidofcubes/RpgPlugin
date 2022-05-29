package io.github.kidofcubes.types;


import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public enum DamageType {
    Physical(new DamageCause[]{
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
    Magical(new DamageCause[]{

            DamageCause.MAGIC,
            DamageCause.CUSTOM,
            DamageCause.SUICIDE,
            DamageCause.DRAGON_BREATH,
    }),
    Light(new DamageCause[]{}),
    Darkness(new DamageCause[]{
            DamageCause.WITHER,
            DamageCause.VOID
    }),
    Earth(new DamageCause[]{
            DamageCause.POISON,
            DamageCause.STARVATION,
            DamageCause.THORNS,
    }),
    Thunder(new DamageCause[]{
            DamageCause.LIGHTNING
    }),
    Water(new DamageCause[]{
            DamageCause.FREEZE,
            DamageCause.DROWNING

    }),
    Fire(new DamageCause[]{
            DamageCause.LAVA,
            DamageCause.MELTING,
            DamageCause.HOT_FLOOR,
            DamageCause.FIRE,
            DamageCause.FIRE_TICK

    }),
    Air(new DamageCause[]{
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
        return Magical;
    }

    public boolean containsDamageCause(DamageCause cause) {
        for (int i = 0; i < fromDamageCauses.length; i++) {
            if (fromDamageCauses[i] == cause) return true;
        }
        return false;
    }
}
