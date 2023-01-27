package io.github.kidofcubes;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class RpgRegistry { //why?
    private final static Map<NamespacedKey, Function<LivingEntity,RpgEntity>> registeredEntityTypes = new HashMap<>();

    static void registerEntityType(Function<LivingEntity,RpgEntity> generatorFunction, NamespacedKey identifier){
        registeredEntityTypes.put(identifier,generatorFunction);
    }
    static boolean containsEntityType(NamespacedKey identifier){
        return registeredEntityTypes.containsKey(identifier);
    }
    @NotNull
    static Function<LivingEntity,RpgEntity> getEntityType(NamespacedKey identifier){ //wow look at me not nulling all over the place
        return Objects.requireNonNull(registeredEntityTypes.get(identifier), "Specified type doesn't exist in map");
    }
    static Iterable<NamespacedKey> getLoadedEntityTypes(){
        return registeredEntityTypes.keySet();
    }

    private final static Map<NamespacedKey, Function<ItemStack,RpgItem>> registeredItemTypes = new HashMap<>();

    static void registerItemType(Function<ItemStack,RpgItem> generatorFunction, NamespacedKey identifier){
        registeredItemTypes.put(identifier,generatorFunction);
    }
    static boolean containsItemType(NamespacedKey identifier){
        return registeredItemTypes.containsKey(identifier);
    }
    @NotNull
    static Function<ItemStack,RpgItem> getItemType(NamespacedKey identifier){
        return Objects.requireNonNull(registeredItemTypes.get(identifier), "Specified type doesn't exist in map");
    }
    static Iterable<NamespacedKey> getLoadedItemTypes(){
        return registeredItemTypes.keySet();
    }

}
