package io.github.kidofcubes;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface ActivateStats {
    Map<Class<? extends Stat>, Stat> getActivationStats();
}
