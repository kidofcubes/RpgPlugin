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
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.github.kidofcubes.RpgPlugin.*;

public class RpgEntity extends RpgObject {
    private final Map<EntityRelation, List<UUID>> relations = new HashMap<>();



    private LivingEntity livingEntity;

    private boolean extension=false;

    private double maxHealth;
    private double health;

    //region constructors
    public RpgEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
        if (livingEntity.getPersistentDataContainer().has(key)) {
            loadFromJson(livingEntity.getPersistentDataContainer().get(key, PersistentDataType.STRING));
        }else{
            if(getLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)==null){
                System.out.println("generic health was null");
                getLivingEntity().registerAttribute(Attribute.GENERIC_MAX_HEALTH);
            }
            System.out.println("generic health is now "+livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            setMaxHealth(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            health = livingEntity.getHealth();

        }
        setUUID(livingEntity.getUniqueId());
        relations.put(EntityRelation.ALLY, new ArrayList<>());
        relations.put(EntityRelation.ENEMY, new ArrayList<>());
        relations.put(EntityRelation.NEUTRAL, new ArrayList<>());
        RpgManager.addRpgEntity(getUUID(), this);
        updateInventoryStats();
    }

//    public RpgEntity(LivingEntity livingEntity, boolean tempEntity) {
//        this(livingEntity, null, tempEntity);
//    }
//
//    public RpgEntity(LivingEntity livingEntity, RpgEntity parent, boolean tempEntity) {
//        this.livingEntity = livingEntity;
//        temporary = tempEntity;
//        if (livingEntity.getPersistentDataContainer().has(key)) {
//            loadFromJson(livingEntity.getPersistentDataContainer().get(key, PersistentDataType.STRING));
//        }else{
//            if(getLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)==null) getLivingEntity().registerAttribute(Attribute.GENERIC_MAX_HEALTH);
//            setMaxHealth(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
//            health = livingEntity.getHealth();
//
//        }
//        setUUID(livingEntity.getUniqueId());
//        for (EntityRelation relation :
//                EntityRelation.values()) {
//            relations.put(relation, new ArrayList<>());
//        }
//        if (parent != null) {
//            setParent(parent);
//            relations.get(EntityRelation.Ally).add(parentUUID);
//        }
//        RpgManager.addRpgEntity(getUUID(), this);
//
//    }
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
        if (relation != EntityRelation.NEUTRAL) {
            relations.get(relation).add(uuid);
        }
    }

    public EntityRelation getRelation(UUID uuid) {
        cleanRelations();
        if(uuid.equals(getUUID())){
            return EntityRelation.ALLY;
        }
        for (Map.Entry<EntityRelation, List<UUID>> entry : relations.entrySet()) {
            if (entry.getValue().contains(uuid)) {
                return entry.getKey();
            }
        }
        if(getParent()!=null) {
            if(getParent() instanceof RpgEntity rpgEntity) {
                return rpgEntity.getRelation(uuid); //time to seperate whos using and parent :pensive:
            }
        }
        return EntityRelation.NEUTRAL;
    }

    @NotNull
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
            //setMaxHealth(getLivingEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        }
        return maxHealth;
    }

    public double getEffectiveMaxHealth() {
        return Math.max(getMaxHealth(),1);
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
        System.out.println("MAX HEALTH IS NOW "+maxHealth);
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
        this.health = Math.max(Math.min(health,getMaxHealth()),0);
        livingEntity.setHealth(this.health);
        //this.health = health; //overheal tf2 mechanic later maybe?
    }




    //endregion

    public void updateInventoryStats(){
        EntityEquipment entityEquipment = livingEntity.getEquipment();

        if(entityEquipment!=null){
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                RpgItem newItem = null;
                if(!entityEquipment.getItem(equipmentSlot).getType().isAir()) newItem = RpgManager.getItem(entityEquipment.getItem(equipmentSlot));
                RpgItem oldItem = cachedEquipment.getOrDefault(equipmentSlot,null);
                if(newItem==oldItem) continue;
                if(oldItem!=null) stopUsing(cachedEquipment.getOrDefault(equipmentSlot,null));
                if(newItem!=null) use(newItem);
                cachedEquipment.put(equipmentSlot,newItem);
            }
        }
    }

    private final Map<EquipmentSlot,RpgItem> cachedEquipment = new HashMap<>();

    /**
     * Not the same as kill()
     */
    @Override
    public void remove() {
        super.remove();
        if(!(getLivingEntity() instanceof Player)){
            getLivingEntity().remove();
        }
    }

    @Nullable
    public UUID currentTarget() {
        if (relations.get(EntityRelation.ENEMY).size() != 0) {
            cleanRelations();
            if (relations.get(EntityRelation.ENEMY).size() != 0) {
                return relations.get(EntityRelation.ENEMY).get(relations.get(EntityRelation.ENEMY).size() - 1);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }





    public boolean exists() {
        //System.out.println("isvalid: "+getLivingEntity().isValid()+" isdead: "+livingEntity.isDead()+" health: "+livingEntity.getHealth());
        return exists(getLivingEntity());
    }

    public static boolean exists(LivingEntity livingEntity) {
        return livingEntity.isValid() && !livingEntity.isDead() && livingEntity.getHealth() > 0;
    }

    @Override
    public void save() {
        System.out.println("SAVING RPGENTITY "+getName());
        livingEntity.getPersistentDataContainer().set(key, PersistentDataType.STRING, toJson());
    }

    @Override
    protected void loadFromJson(String json) {
        super.loadFromJson(json);
        RpgEntityJsonContainer container = gson.fromJson(json, RpgEntityJsonContainer.class);
        System.out.println("LOADING MAX HEALTH WAS "+container.maxHealth);
        setMaxHealth(container.maxHealth);
        System.out.println("LOADING HEALTH WAS "+container.health);
        setHealth(container.health);
    }

    @Override
    public RpgObjectJsonContainer toContainer() {
        RpgEntityJsonContainer entityJsonContainer = gson.fromJson(gson.toJson(super.toContainer()),RpgEntityJsonContainer.class);
        System.out.println("SAVING HEALTH WAS "+getHealth());
        entityJsonContainer.health = getHealth();
        System.out.println("SAVING MAX HEALTH WAS "+getMaxHealth());
        entityJsonContainer.maxHealth = getMaxHealth();
        return entityJsonContainer;
    }

    public static class RpgEntityJsonContainer extends RpgObjectJsonContainer{
        public double maxHealth=20;
        public double health=20;
    }
}
