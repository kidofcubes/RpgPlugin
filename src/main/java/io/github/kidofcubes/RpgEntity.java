package io.github.kidofcubes;

import io.github.kidofcubes.events.RpgEntityDamageByObjectEvent;
import io.github.kidofcubes.events.RpgEntityDamageEvent;
import io.github.kidofcubes.events.RpgEntityHealByObjectEvent;
import io.github.kidofcubes.events.RpgEntityHealEvent;
import io.github.kidofcubes.managers.RpgManager;
import io.github.kidofcubes.types.DamageType;
import io.github.kidofcubes.types.EntityRelation;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.github.kidofcubes.ExtraFunctions.isEmpty;
import static io.github.kidofcubes.RpgPlugin.ManaDisplayMethod;
import static io.github.kidofcubes.RpgPlugin.key;

public class RpgEntity extends RpgObject {
    private final Map<EntityRelation, List<UUID>> relations = new HashMap<>();
    public LivingEntity livingEntity;

    public RpgEntity(LivingEntity livingEntity) {
        this(livingEntity, false);
    }

    public RpgEntity(LivingEntity livingEntity, boolean tempEntity) {
        this(livingEntity, null, tempEntity);
    }

    public RpgEntity(LivingEntity livingEntity, RpgEntity parent, boolean tempEntity) {
        this.livingEntity = livingEntity;
        level = 0;
        temporary = tempEntity;
        if (livingEntity.getPersistentDataContainer().has(key)) {
            loadFromJson(livingEntity.getPersistentDataContainer().get(key, PersistentDataType.STRING));
        }
        setUUID(livingEntity.getUniqueId());
        for (EntityRelation relation :
                EntityRelation.values()) {
            relations.put(relation, new ArrayList<>());
        }
        if (parent != null) {
            setParent(parent);
        }
        RpgManager.addRpgEntity(getUUID(), this);
    }

    @Override
    public void setMana(float mana) {
        super.setMana(mana);
        if(ManaDisplayMethod== RpgPlugin.ManaDisplayType.level) {
            if(Math.floor(mana)>=0) {
                if (livingEntity instanceof Player player) {
                    player.setLevel((int) Math.floor(mana));
                }
            }
        }
    }

    public void damage(DamageType type, double amount) {
        RpgEntityDamageEvent event = new RpgEntityDamageEvent(this, type, amount);
        event.callEvent();
        livingEntity.damage(event.getTotalDamage());
    }

    public RpgEntityDamageEvent damage(DamageType type, double amount, RpgObject attacker) {
        RpgEntityDamageEvent event = new RpgEntityDamageByObjectEvent(this, type, amount, attacker);
        event.callEvent();
        livingEntity.damage(event.getTotalDamage());
        return event;
    }

    public RpgEntityHealEvent heal(double amount) {
        RpgEntityHealEvent event = new RpgEntityHealEvent(this, amount);
        event.callEvent();
        livingEntity.setHealth(livingEntity.getHealth() + event.getAmount());
        return event;
    }

    public RpgEntityHealByObjectEvent heal(double amount, @NotNull RpgObject healer) {

        RpgEntityHealByObjectEvent event = new RpgEntityHealByObjectEvent(this, amount, healer);
        event.callEvent();
        livingEntity.setHealth(livingEntity.getHealth() + event.getAmount());
        return event;
    }

    private void cleanRelations() {
        for (Map.Entry<EntityRelation, List<UUID>> entry : relations.entrySet()) {
            List<UUID> list = entry.getValue();
            for (int i = list.size() - 1; i > -1; i--) {
                if (!RpgManager.checkEntityExists(list.get(i))) list.remove(i);
            }
        }
    }

    public void setRelation(UUID uuid, EntityRelation relation) {
        for (Map.Entry<EntityRelation, List<UUID>> entry : relations.entrySet()) {
            entry.getValue().remove(uuid);
        }
        if (relation != EntityRelation.Neutral) {
            relations.get(relation).add(uuid);
        }
    }

    public EntityRelation getRelation(UUID uuid) {
        cleanRelations();
        for (Map.Entry<EntityRelation, List<UUID>> entry : relations.entrySet()) {
            if (entry.getValue().contains(uuid)) {
                return entry.getKey();
            }
        }
        return EntityRelation.Neutral;
    }

    @Nullable
    public UUID currentTarget() {
        if (relations.get(EntityRelation.Enemy).size() != 0) {
            cleanRelations();
            if (relations.get(EntityRelation.Enemy).size() != 0) {
                return relations.get(EntityRelation.Enemy).get(relations.get(EntityRelation.Enemy).size() - 1);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    Map<String, Stat> effectiveStatsCache = new HashMap<>();
    long effectiveStatsLastUpdate = 0;

    @Override
    public Map<String, Stat> getEffectiveStatsMap() {
        long now = System.currentTimeMillis();
        if (now - effectiveStatsLastUpdate > 250) {
            effectiveStatsLastUpdate = now;

            effectiveStatsCache = new HashMap<>(getStatsMap());
            if (livingEntity.getEquipment() != null) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack item = livingEntity.getEquipment().getItem(slot);
                    if (!isEmpty(item)) {
                        effectiveStatsCache.putAll(RpgManager.getItem(item).getEffectiveStatsMap());
                    }
                }
            }
        }
        return effectiveStatsCache;

    }

    public boolean exists() {
        return livingEntity.isValid() && !livingEntity.isDead() && livingEntity.getHealth() > 0;
    }

    @Override
    public void save() {
        livingEntity.getPersistentDataContainer().set(key, PersistentDataType.STRING, toJson());
    }
}
