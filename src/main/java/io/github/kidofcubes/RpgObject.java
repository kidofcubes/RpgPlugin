package io.github.kidofcubes;


import io.github.kidofcubes.events.RpgActivateStatEvent;
import io.github.kidofcubes.events.RpgEntityDamageByObjectEvent;
import io.github.kidofcubes.events.RpgEntityDamageEvent;
import io.github.kidofcubes.managers.RpgManager;
import io.github.kidofcubes.types.DamageType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.kidofcubes.RpgPlugin.gson;

public abstract class RpgObject {
    private final Map<String, Integer> stats = new HashMap<>();
    String name;
    int level;
    float mana; //todo implement mana
    UUID parentUUID;
    boolean temporary = false;
    private RpgEntity parent;
    private UUID uuid;

    /**
     * Check if this object is temporary (deleted on server restart, doesn't get saved)
     * @return If this object is temporary
     */
    public boolean isTemporary() {
        return temporary;
    }

    /**
     * Gets the UUID of this object
     * @return The UUID of this object
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Sets the UUID of this object
     * @param uuid The new UUID
     */
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }


    /**
     * Gets the parent of this object (inherits relations and will attribute things to parent)
     * @return The parent of this object
     */
    @Nullable
    public RpgEntity getParent() {
        if (parent != null) {
            return parent;
        } else {
            if (parentUUID != null) {
                RpgEntity rpgEntityParent = RpgManager.getRpgEntity(parentUUID);
                if (rpgEntityParent != null) {
                    parent = rpgEntityParent;
                    return parent;
                }
            }
            return null;
        }
    }


    public void setParent(RpgEntity parent) {
        this.parent = parent;
        parentUUID = parent.getUUID();
    }

    /**
     * Adds a stat to the statMap (replaces stat with new level if already exists)
     * @param stat The stat name
     * @param level The level of the stat
     */
    public void addStat(String stat, int level) {
        stats.put(stat, level);
    }

    /**
     * Removes a stat (if the stat is not found, won't do anything)
     * @param stat The stat name
     */
    public void removeStat(String stat) {
        stats.remove(stat);
    }

    /**
     * Gets the stat map
     * @return The stat map
     */
    public Map<String, Integer> getStats() {
        return stats;
    }

    /**
     * Gets this object's effective stats (for example, an RpgEntity's effective stats include stats of items in their inventory)
     * @return This object's effective stats
     */
    public Map<String, Integer> getEffectiveStats() {
        return getStats();
    }


    public boolean hasStat(String name) {
        return getEffectiveStats().containsKey(name);
    }

    /**
     * Makes this object attack a RpgEntity victim with base damage
     * (Makes a RpgEntityDamageByObjectEvent with amount physical base damage,
     * @param amount The base damage of the attack
     * @param victim The victim of the attack
     */
    public void attack(double amount, RpgEntity victim) {
        RpgEntityDamageByObjectEvent event = new RpgEntityDamageByObjectEvent(victim, DamageType.Physical, amount, this);
        event.callEvent();
        victim.livingEntity.damage(event.getTotalDamage());
    }

    /**
     * Calls a RpgActivateStatEvent with this as the parent
     * @param statName The name of the stat to activate
     */
    public void activateStat(String statName) {
        getActivateStatEvent(List.of(statName)).callEvent();
    }

    public RpgActivateStatEvent getActivateStatEvent(List<String> statNames) {
        return new RpgActivateStatEvent(this, statNames);
    }

    public String toJson() {
        RpgObjectJsonContainer container = new RpgObjectJsonContainer();
        container.name = name;
        container.level = level;
        Map<String, Integer> allStats = getStats();
        container.stats = new HashMap<>();
        container.stats.putAll(allStats);
        container.parentUUID = parentUUID.toString();
        return gson.toJson(container);
    }

    protected void loadFromJson(String json) {
        RpgObjectJsonContainer container = gson.fromJson(json, RpgObjectJsonContainer.class);
        level = container.level;
        name = container.name;
        stats.putAll(container.stats);
        parentUUID = UUID.fromString(container.parentUUID);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof RpgObject otherRpgObject) {
            return getUUID().equals(otherRpgObject.getUUID());
        } else {
            return false;
        }
    }

    /**
     * Saves this object to the world
     */
    public abstract void save();

    public static class RpgObjectJsonContainer {
        public String name;
        public int level;
        public String parentUUID;
        public Map<String, Integer> stats;
    }
}
