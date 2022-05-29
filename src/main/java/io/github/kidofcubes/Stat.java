package io.github.kidofcubes;


import io.github.kidofcubes.managers.StatManager;
import io.github.kidofcubes.types.StatType;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class Stat implements Listener {

    public static String description;

    public static StatType statType;

    @Nullable
    public static Class<? extends Stat> fromText(String name) {
        for (Class<? extends Stat> stat : StatManager.getRegisteredStats()) {
            if (stat.getSimpleName().equalsIgnoreCase(name)) {
                return stat;
            }
        }
        return null;
    }

    public String getName() {
        return this.getClass().getName();
    }

    public String getShortName() {
        return this.getClass().getSimpleName();
    }

    public String getDescription() {
        return description;
    }

    public StatType getStatType() {
        return statType;
    }

    public void trigger(Event event, int level) {
        run(event, level);
    }

    public abstract RpgObject elementToStatCheck(Event event);

    /**
     * @param event an event that's an instanceof one of the events you asked for
     * @param level the stat level, for things like more damage on sharpness 5 than sharpness 1 (will not give 0)
     */
    public abstract void run(Event event, int level);


    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (other.getClass() != this.getClass()) {
            return false;
        }

        final Stat otherStat = (Stat) other;
        return Objects.equals(getName(), otherStat.getName());
    }
}
