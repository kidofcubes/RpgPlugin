package io.github.kidofcubes;


import io.github.kidofcubes.events.RpgActivateStatEvent;
import io.github.kidofcubes.events.RpgEntityDamageByObjectEvent;
import io.github.kidofcubes.events.RpgEntityDamageEvent;
import io.github.kidofcubes.managers.RpgManager;
import io.github.kidofcubes.types.DamageType;

import java.util.HashMap;
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

    public boolean isTemporary() {
        return temporary;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }


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


    public void addStat(String stat, int level) {
        stats.put(stat, level);
    }

    public void removeStat(String stat) {
        stats.remove(stat);
    }

    public Map<String, Integer> getStats() {
        return stats;
    }

    public Map<String, Integer> getEffectiveStats() {
        return getStats();
    }

    public boolean hasStat(String name) {
        return getEffectiveStats().containsKey(name);
    }

    public void attack(double amount, RpgEntity victim) {
        RpgEntityDamageEvent event = new RpgEntityDamageByObjectEvent(victim, DamageType.Physical, amount, this);
        event.callEvent();
        victim.livingEntity.damage(event.getTotalDamage());
    }

    public void activateStat(String name) {
        getActivateStatEvent(name).callEvent();
    }

    public RpgActivateStatEvent getActivateStatEvent(String name) {
        return new RpgActivateStatEvent().parent(this).addTriggerStat(name);
    }

    public String toJson() {
        RpgObjectJsonContainer container = new RpgObjectJsonContainer();
        container.name = name;
        container.level = level;
        Map<String, Integer> allStats = getStats();
        container.stats = new HashMap<>();
        container.stats.putAll(allStats);
        container.parent = parentUUID.toString();
        return gson.toJson(container);
    }

    void loadFromJson(String json) {
        RpgObjectJsonContainer container = gson.fromJson(json, RpgObjectJsonContainer.class);
        level = container.level;
        name = container.name;
        stats.putAll(container.stats);
        parentUUID = UUID.fromString(container.parent);
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

    public abstract void save();

    public static class RpgObjectJsonContainer {
        public String name;
        public int level;
        public String parent;
        public Map<String, Integer> stats;
    }
}
