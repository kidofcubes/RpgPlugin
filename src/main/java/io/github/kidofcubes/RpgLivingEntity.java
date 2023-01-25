package io.github.kidofcubes;

import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RpgLivingEntity {
//    public static TestInterface getRpgEntityInstance(LivingEntity livingEntity){
//        CraftPersistentDataContainer persistentDataContainer = (CraftPersistentDataContainer) livingEntity.getPersistentDataContainer();
//        if(((RpgObjectHolder)persistentDataContainer).getObject()==null){
//            if(persistentDataContainer.getRaw().containsKey(TestInterface.key.toString())){ //contains data, init rpgitem from data
//                RpgObjectTag tag = (RpgObjectTag)persistentDataContainer.getRaw().get(TestInterface.key.toString());
//                ((RpgObjectHolder) persistentDataContainer).setObject(new BaseRpgEntity().loadFromJson(tag.getLoadedText()));
//            }else{ //no previous data, init new rpgitem
//
//                ((RpgObjectHolder)itemMeta.getPersistentDataContainer()).setObject(new BaseRpgItem());
//                ((CraftPersistentDataContainer)itemMeta.getPersistentDataContainer()).getRaw().put(TestInterface.key.toString(),new RpgObjectTag(((RpgObjectHolder)itemMeta.getPersistentDataContainer()).getObject()));
//            }
//        }
//        return ((RpgObjectHolder)itemMeta.getPersistentDataContainer()).getObject();
//    }
}
