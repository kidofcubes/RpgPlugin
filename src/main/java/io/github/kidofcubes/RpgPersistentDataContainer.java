package io.github.kidofcubes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Map;

public abstract class RpgPersistentDataContainer {

//    public static CompoundTag toTagCompound(CompoundTag tag, Object holderr) {
//        System.out.println("HOLDER "+holderr.getClass().getName()+" is "+holderr.hashCode());
//        if(holderr instanceof RpgObjectHolder holder) {
//            System.out.println("is a holder");
//            System.out.println("the holder has "+holder.getObject());
//            if (holder.getObject() != null) {
//                System.out.println("a non empty holder");
//                tag.putString("weird", "kindasus " + holder.getObject().getAsJSON());
////                tag.put("RPG_OBJECT", new RpgObjectTag(holder.getObject()));
//            }
//        }
//        return tag;
//    }
//
//    public void put(String key, Tag base) {
//        this.customDataTags.put(key, base);
//    }
//
//    public void putAll(Map<String, Tag> map) {
//        this.customDataTags.putAll(map);
//    }
//
//    public void putAll(CompoundTag compound) {
//        for (String key : compound.getAllKeys()) {
//            this.customDataTags.put(key, compound.get(key));
//        }
//    }
//
//    public Map<String, Tag> getRaw() {
//        return this.customDataTags;
//    }
//
//    public CraftPersistentDataTypeRegistry getDataTagTypeRegistry() {
//        return this.registry;
//    }
//
//    @Override
//    public int hashCode() {
//        int hashCode = 3;
//        hashCode += this.customDataTags.hashCode(); // We will simply add the maps hashcode
//        return hashCode;
//    }
//
//    public Map<String, Object> serialize() {
//        return (Map<String, Object>) CraftNBTTagConfigSerializer.serialize(this.toTagCompound());
//    }
//
//    // Paper start
//    public void clear() {
//        this.customDataTags.clear();
//    }
//
//    @Override
//    public boolean has(NamespacedKey key) {
//        Validate.notNull(key, "The provided key for the custom value was null");
//
//        return this.customDataTags.containsKey(key.toString());
//    }
//
//    @Override
//    public byte[] serializeToBytes() throws java.io.IOException {
//        net.minecraft.nbt.CompoundTag root = this.toTagCompound();
//        java.io.ByteArrayOutputStream byteArrayOutput = new java.io.ByteArrayOutputStream();
//        try (java.io.DataOutputStream dataOutput = new java.io.DataOutputStream(byteArrayOutput)) {
//            net.minecraft.nbt.NbtIo.write(root, dataOutput);
//            return byteArrayOutput.toByteArray();
//        }
//    }
//
//    @Override
//    public void readFromBytes(byte[] bytes, boolean clear) throws java.io.IOException {
//        if (clear) {
//            this.clear();
//        }
//        try (java.io.DataInputStream dataInput = new java.io.DataInputStream(new java.io.ByteArrayInputStream(bytes))) {
//            net.minecraft.nbt.CompoundTag compound = net.minecraft.nbt.NbtIo.read(dataInput);
//            this.putAll(compound);
//        }
//    }
//
//
//
//    private TestInterface thingy;
//
//    @Override
//    public void setObject(TestInterface thing) {
//        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//        thingy=thing;
//    }
//
//    @Override
//    public TestInterface getObject() {
//        return thingy;
//    }
}
