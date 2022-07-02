package io.github.kidofcubes;

import io.github.kidofcubes.events.RpgEntityDamageByObjectEvent;
import io.github.kidofcubes.events.RpgEntityDamageEvent;
import io.github.kidofcubes.events.RpgEntityHealByObjectEvent;
import io.github.kidofcubes.events.RpgEntityHealEvent;
import io.github.kidofcubes.managers.RpgManager;
import io.github.kidofcubes.types.DamageType;
import io.github.kidofcubes.types.EntityRelation;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.github.kidofcubes.ExtraFunctions.isEmpty;
import static io.github.kidofcubes.ExtraFunctions.joinStatMaps;
import static io.github.kidofcubes.RpgPlugin.*;

public class RpgEntity extends RpgObject {
    private final Map<EntityRelation, List<UUID>> relations = new HashMap<>();

    

    private LivingEntity livingEntity;

    private boolean extension=false;

    private double maxHealth;
    private double health;

    //region constructors
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
        }else{
            if(getLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)==null) getLivingEntity().registerAttribute(Attribute.GENERIC_MAX_HEALTH);
            setMaxHealth(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            health = livingEntity.getHealth();

        }
        setUUID(livingEntity.getUniqueId());
        for (EntityRelation relation :
                EntityRelation.values()) {
            relations.put(relation, new ArrayList<>());
        }
        if (parent != null) {
            setParent(parent);
            relations.get(EntityRelation.Ally).add(parentUUID);
        }
        RpgManager.addRpgEntity(getUUID(), this);

    }
    //endregion

    //region healthfunctions
    public RpgEntityDamageEvent damage(DamageType type, double amount) {
        RpgEntityDamageEvent event = new RpgEntityDamageEvent(this, type, amount);
        event.callEvent();
        setHealth(getHealth()-event.getTotalDamage());
        return event;
    }

    public RpgEntityDamageByObjectEvent damage(DamageType type, double amount, RpgObject attacker, List<Stat> extraStats) {
        if(attacker!=null) {
            RpgEntityDamageByObjectEvent event = new RpgEntityDamageByObjectEvent(this, type, amount, attacker, extraStats);
            event.callEvent();
            setHealth(getHealth() - event.getTotalDamage());
            return event;
        }else{
            return null;
        }
    }

    public RpgEntityHealEvent heal(double amount) {
        RpgEntityHealEvent event = new RpgEntityHealEvent(this, amount);
        event.callEvent();
        setHealth(getHealth()+event.getAmount());
        return event;
    }

    public RpgEntityHealByObjectEvent heal(double amount, RpgObject healer) {
        if(healer!=null) {
            RpgEntityHealByObjectEvent event = new RpgEntityHealByObjectEvent(this, amount, healer);
            event.callEvent();
            setHealth(getHealth() + event.getAmount());
            return event;
        }
        return null;

    }
    //endregion

    private void cleanRelations() {
        for (Map.Entry<EntityRelation, List<UUID>> entry : relations.entrySet()) {
            List<UUID> list = entry.getValue();
            for (int i = list.size() - 1; i > -1; i--) {
                if (!RpgManager.checkEntityExists(list.get(i))) list.remove(i);
            }
        }
    }

    public void checkEquipment(){

    }

    //region gettersetters

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
        if(uuid.equals(getUUID())){
            return EntityRelation.Ally;
        }
        for (Map.Entry<EntityRelation, List<UUID>> entry : relations.entrySet()) {
            if (entry.getValue().contains(uuid)) {
                return entry.getKey();
            }
        }
        if(getParent()!=null) {
            if(getParent() instanceof RpgEntity rpgEntity) {
                return rpgEntity.getRelation(uuid);
            }
        }
        return EntityRelation.Neutral;
    }


    @Override
    public String getName() {
        return getLivingEntity().getName();
    }

    @Override
    public void setMana(double mana) {
        super.setMana(mana);
        if(ManaDisplayMethod== RpgPlugin.ManaDisplayType.level) {
            if (livingEntity instanceof Player player) {
                player.setLevel((int) Math.floor(getMana()));
            }
        }
    }

    /**
     * Gets the livingentity this RpgEntity is linked to
     * Do not use the health functions on the living entity, use {@link #setHealth(double)} and {@link #getHealth()}
     * @return
     */
    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    public double getMaxHealth() {
        if(maxHealth<=0){
            if(getLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)==null) getLivingEntity().registerAttribute(Attribute.GENERIC_MAX_HEALTH);
            setMaxHealth(getLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        }
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
        if(getLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)==null) getLivingEntity().registerAttribute(Attribute.GENERIC_MAX_HEALTH);
        getLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        if(health<this.health){//fake dmg
            livingEntity.damage(0);
        }else if(health>this.health){
            //hearts particles maybe later
        }
        this.health = Math.max(Math.min(health,maxHealth),0);
        livingEntity.setHealth(this.health);
        //this.health = health; //overheal tf2 mechanic later maybe?
    }




    //endregion

    public void updateInventoryStats(){
        if(cachedEquipment.values().size()==0){
            cachedEquipment.put(EquipmentSlot.HEAD,null);
            cachedEquipment.put(EquipmentSlot.CHEST,null);
            cachedEquipment.put(EquipmentSlot.LEGS,null);
            cachedEquipment.put(EquipmentSlot.FEET,null);
            cachedEquipment.put(EquipmentSlot.HAND,null);
            cachedEquipment.put(EquipmentSlot.OFF_HAND,null);
        }
        EntityEquipment entityEquipment = livingEntity.getEquipment();

        if(entityEquipment!=null){
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                ItemStack itemStack = entityEquipment.getItem(equipmentSlot);
                if(!itemStack.getType().isAir()){
                    RpgItem rpgItem = RpgManager.getItem(itemStack);
                    if(!rpgItem.equals(cachedEquipment.get(equipmentSlot))){
                        removeUsedObject(cachedEquipment.get(equipmentSlot));
                        addUsedObject(rpgItem);
                        cachedEquipment.put(equipmentSlot,rpgItem);
                    }
                }
            }
        }
    }

    private final Map<EquipmentSlot,RpgItem> cachedEquipment = new HashMap<>();

    @Override
    public List<RpgObject> getUsedObjects() {
        List<RpgObject> temp = super.getUsedObjects();

        return temp;
    }

    /**
     * Not the same as kill()
     */
    @Override
    public void remove() {
        super.remove();
        getLivingEntity().remove();
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





    public boolean exists() {
        return livingEntity.isValid() && !livingEntity.isDead() && livingEntity.getHealth() > 0;
    }

    public static boolean exists(LivingEntity livingEntity) {
        return livingEntity.isValid() && !livingEntity.isDead() && livingEntity.getHealth() > 0;
    }

    @Override
    public void save() {
        livingEntity.getPersistentDataContainer().set(key, PersistentDataType.STRING, toJson());
    }

    @Override
    protected void loadFromJson(String json) {
        super.loadFromJson(json);
        RpgEntityJsonContainer container = gson.fromJson(json, RpgEntityJsonContainer.class);
        setMaxHealth(container.maxHealth);
        setHealth(container.health);
    }

    @Override
    public RpgObjectJsonContainer toContainer() {
        RpgEntityJsonContainer entityJsonContainer = gson.fromJson(gson.toJson(super.toContainer()),RpgEntityJsonContainer.class);
        entityJsonContainer.health = getHealth();
        entityJsonContainer.maxHealth = getMaxHealth();
        return entityJsonContainer;
    }

    public static class RpgEntityJsonContainer extends RpgObjectJsonContainer{
        public double maxHealth=20;
        public double health=20;
    }
}
