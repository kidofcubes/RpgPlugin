package io.github.kidofcubes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.kidofcubes.managers.StatManager;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.StringTag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



public class RpgLivingEntity implements RpgEntity {


    public static void setRpgEntityInstance(LivingEntity livingEntity, RpgEntity rpgEntity){
        System.out.println("START SETENTITYINSTANCE");
        ((RpgObjectHolder) livingEntity.getPersistentDataContainer()).setObject(rpgEntity);
        System.out.println("MID SETENTITYINSTANCE");
        ((CraftPersistentDataContainer) livingEntity.getPersistentDataContainer()).getRaw().put(RpgObject.metadataKey.asString(), new RpgObjectTag(rpgEntity));
        System.out.println("END SETENTITYINSTANCE");
    }

    public static void setRpgEntityType(LivingEntity livingEntity, NamespacedKey identifier) throws ClassNotFoundException{
        CraftPersistentDataContainer persistentDataContainer = (CraftPersistentDataContainer) livingEntity.getPersistentDataContainer();
        StringTag originalType = ((StringTag)persistentDataContainer.getRaw().get(RpgObject.typeStorageKey.asString()));
        if(originalType!=null){
            if(originalType.getAsString().equals(identifier.asString())){
                return;
            }
        }
        if(RpgRegistry.containsEntityType(identifier)){
            persistentDataContainer.getRaw().put(RpgObject.typeStorageKey.asString(),StringTag.valueOf(identifier.asString()));

            if(((RpgObjectHolder) persistentDataContainer).getObject()!=null){ //if its already loaded, reload it
                System.out.println("LOAD FRON JSON IN TYPE");
                setRpgEntityInstance(livingEntity, (RpgEntity) RpgRegistry.getEntityType(identifier).apply(livingEntity).loadFromJson(((RpgObjectHolder) persistentDataContainer).getObject().toJson()));
            }
        }else{
            throw new ClassNotFoundException("Couldn't load RpgEntity type "+identifier.asString()+" from registry");
        }
    }

    public static RpgEntity getRpgEntityInstance(LivingEntity livingEntity) throws ClassNotFoundException { //if livingentity has a type already, init that type instead, if not, init default
        System.out.println("GOT THE INSTANCE OF "+livingEntity.getName());
        CraftPersistentDataContainer persistentDataContainer = (CraftPersistentDataContainer) livingEntity.getPersistentDataContainer();
        if(((RpgObjectHolder)persistentDataContainer).getObject()==null){ //init object if not found
            if(persistentDataContainer.getRaw().containsKey(RpgObject.typeStorageKey.toString())){ //has a special type
                NamespacedKey type=NamespacedKey.fromString(persistentDataContainer.getRaw().get(RpgObject.typeStorageKey.toString()).getAsString());
                if(RpgRegistry.containsEntityType(type)){
                    if(persistentDataContainer.getRaw().containsKey(RpgObject.metadataKey.toString())) {
                        System.out.println("WE HAVE THE TYPE THATS "+type.asString()+" JSON IS "+new String(((ByteArrayTag)((persistentDataContainer).getRaw().get(RpgObject.metadataKey.toString()))).getAsByteArray(), StandardCharsets.UTF_8));
                        setRpgEntityInstance(livingEntity, (RpgEntity) RpgRegistry.getEntityType(type).apply(livingEntity).loadFromJson(
                                new String(((ByteArrayTag)((persistentDataContainer).getRaw().get(RpgObject.metadataKey.toString()))).getAsByteArray(), StandardCharsets.UTF_8)));
                    }else{
                        setRpgEntityInstance(livingEntity,RpgRegistry.getEntityType(type).apply(livingEntity));
                    }
                }else{
                    throw new ClassNotFoundException("Couldn't load RpgEntity type "+type+" from registry");
                }
            }else{
                if(persistentDataContainer.getRaw().containsKey(RpgObject.metadataKey.toString())) {
                    System.out.println("WE DONT HAVE THE TYPE AND JSON IS "+new String(((ByteArrayTag)((persistentDataContainer).getRaw().get(RpgObject.metadataKey.toString()))).getAsByteArray(), StandardCharsets.UTF_8));
                    setRpgEntityInstance(livingEntity, (RpgEntity) new RpgLivingEntity(livingEntity).loadFromJson(
                            new String(((ByteArrayTag)((persistentDataContainer).getRaw().get(RpgObject.metadataKey.toString()))).getAsByteArray(), StandardCharsets.UTF_8)));
                }else{
                    setRpgEntityInstance(livingEntity,new RpgLivingEntity(livingEntity));
                }
            }
        }
        RpgEntity temp = (RpgEntity) ((RpgObjectHolder)persistentDataContainer).getObject();
        System.out.println("END OF GET INSTANCE OF "+livingEntity.getName());
        return temp;
    }

    protected final LivingEntity livingEntity;

    public RpgLivingEntity(LivingEntity livingEntity){
        this.livingEntity=livingEntity;
    }

    private int level=0;
    private double mana=0;

    Map<String,RpgClass> rpgClasses = new HashMap<>();

    Map<String,Stat> stats = new HashMap<>();

//    private List<Stat,>

    @Override
    public String getName() {
        return "RpgLivingEntity";
    }
    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public UUID getUUID() {
        return livingEntity.getUniqueId();
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level=level;
    }

    @Override
    public double getMana() {
        return mana;
    }

    @Override
    public void setMana(double mana) {
        this.mana=mana;
    }

    @Override
    public List<RpgClass> getRpgClasses() {
        return rpgClasses.values().stream().toList();
    }

    @Override
    public void addRpgClass(RpgClass rpgClass) {
        rpgClasses.put(rpgClass.getFullName(),rpgClass);
    }

    @Override
    public RpgClass getRpgClass(String rpgClass) {
        return rpgClasses.get(rpgClass);
    }

    @Override
    public void removeRpgClass(String rpgClass) {
        rpgClasses.remove(rpgClass);
    }

    @Override
    public boolean hasRpgClass(String rpgClass) {
        return rpgClasses.containsKey(rpgClass);
    }

    @Override
    public void use(RpgObject rpgObject) {

    }

    @Override
    public void stopUsing(RpgObject rpgObject) {

    }

    @Override
    public boolean usedBy(RpgObject rpgObject) {
        return false;
    }

    @Nullable
    @Override
    public RpgObject getParent() {
        return null;
    }

    @Override
    public void setParent(RpgObject parent) {

    }



    @Override
    public void addStat(Stat stat, boolean force) {
        stat.onAddStat(this);
        stat.onUseStat(this);
        stats.put(stat.getName(),stat);
    }

    @Override
    public boolean hasStat(String stat) {
        return stats.containsKey(stat);
    }

    @Override
    public @NotNull Stat getStat(String stat) {
        return stats.get(stat);
    }

    @Override
    public List<Stat> getStats() {
        return stats.values().stream().toList();
    }

    @Override
    public void removeStat(String stat) {
        Stat statInstance = stats.remove(stat);
        if(statInstance!=null){
            statInstance.onRemoveStat();
            statInstance.onStopUsingStat();
        }
    }

    @Override
    public @NotNull Map<Class<? extends Stat>, List<Stat>> getUsedStats() {
        return Map.of();
    }

}
