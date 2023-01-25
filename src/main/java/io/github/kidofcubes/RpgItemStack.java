package io.github.kidofcubes;

import org.bukkit.craftbukkit.v1_19_R2.persistence.CraftPersistentDataContainer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
public class RpgItemStack {
    public static TestInterface getRpgItemInstance(ItemMeta itemMeta, ItemStack thing){
        if(itemMeta==null){
            itemMeta=thing.getItemMeta();
            thing.setItemMeta(itemMeta);
        }
        if(((RpgObjectHolder)itemMeta.getPersistentDataContainer()).getObject()==null){
            if(((CraftPersistentDataContainer)itemMeta.getPersistentDataContainer()).getRaw().containsKey(TestInterface.key.toString())){ //contains data, init rpgitem from data
                RpgObjectTag tag = (RpgObjectTag)(((CraftPersistentDataContainer)itemMeta.getPersistentDataContainer()).getRaw().get(TestInterface.key.toString()));
                ((RpgObjectHolder) itemMeta.getPersistentDataContainer()).setObject(new BaseRpgItem().loadFromJson(tag.getLoadedText()));
            }else{ //no previous data, init new rpgitem

                ((RpgObjectHolder)itemMeta.getPersistentDataContainer()).setObject(new BaseRpgItem());
                ((CraftPersistentDataContainer)itemMeta.getPersistentDataContainer()).getRaw().put(TestInterface.key.toString(),new RpgObjectTag(((RpgObjectHolder)itemMeta.getPersistentDataContainer()).getObject()));
            }
        }
        return ((RpgObjectHolder)itemMeta.getPersistentDataContainer()).getObject();
    }
}