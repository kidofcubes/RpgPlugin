package io.github.kidofcubes;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static io.github.kidofcubes.RpgObject.defaultTypeKey;

public class RpgRegistry { //why?
    private final static Map<Class<? extends RpgObject>,Map<NamespacedKey, Function<?,? extends RpgObject>>> typeConstructors = new HashMap<>();

    public static boolean containsTypeConstructor(Class<? extends RpgObject> clazz, NamespacedKey type){
        if(type==null){
            return false;
        }
        if(!typeConstructors.containsKey(clazz)){
            return false;
        }
        return typeConstructors.get(clazz).containsKey(type);
    }

    @SuppressWarnings("unchecked")
    public static <T,Z extends RpgObject> Function<T, Z> getTypeConstructor(Class<? extends RpgObject> clazz, NamespacedKey type){
        return (Function<T, Z>) Objects.requireNonNull(typeConstructors.get(clazz).get(type), "Specified type doesn't exist in map");
    }
    public static <T,Z extends RpgObject> void registerTypeConstructor(Class<? extends RpgObject> clazz, NamespacedKey type, Function<T,Z> constructor){
        typeConstructors.putIfAbsent(clazz,new HashMap<>());
        typeConstructors.get(clazz).put(type,constructor);
    }

    static {
        registerTypeConstructor(RpgEntity.class,defaultTypeKey,(LivingEntity thing) -> {
            return new RpgLivingEntity(thing).loadFromJson(RpgLivingEntity.getHolder(thing).getJson());
        });
        registerTypeConstructor(RpgItem.class,defaultTypeKey,(ItemStack thing) -> {
            return new RpgItemStack(thing).loadFromJson(RpgItemStack.getHolder(thing).getJson());
        });
    }

}
