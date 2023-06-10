package io.github.kidofcubes.rpgplugin;

import net.minecraft.nbt.*;

import javax.annotation.Nullable;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TagWrapper {
    private final CompoundTag tag;
    public TagWrapper(CompoundTag tag){
        this.tag = tag;
    }
    public TagWrapper(){
        this.tag = new CompoundTag();
    }

    public void write(DataOutput output) throws IOException {
        tag.write(output);
    }

    public int sizeInBytes() {
        return tag.sizeInBytes();
    }

    public Set<String> getAllKeys() {
        return tag.getAllKeys();
    }

    public byte getId() {
        return tag.getId();
    }

    public int size() {
        return tag.size();
    }

    @Nullable
    public Tag put(String key, Tag element) {
        return tag.put(key, element);
    }

    public void put(String key, TagWrapper element) {
        tag.put(key, element.tag);
    }

    public void putByte(String key, byte value) {
        tag.putByte(key, value);
    }

    public void putShort(String key, short value) {
        tag.putShort(key, value);
    }

    public void putInt(String key, int value) {
        tag.putInt(key, value);
    }

    public void putLong(String key, long value) {
        tag.putLong(key, value);
    }

    public void putUUID(String key, UUID value) {
        tag.putUUID(key, value);
    }

    public UUID getUUID(String key) {
        return tag.getUUID(key);
    }

    public boolean hasUUID(String key) {
        return tag.hasUUID(key);
    }

    public void putFloat(String key, float value) {
        tag.putFloat(key, value);
    }

    public void putDouble(String key, double value) {
        tag.putDouble(key, value);
    }

    public void putString(String key, String value) {
        tag.putString(key, value);
    }

    public void putByteArray(String key, byte[] value) {
        tag.putByteArray(key, value);
    }

    public void putByteArray(String key, List<Byte> value) {
        tag.putByteArray(key, value);
    }

    public void putIntArray(String key, int[] value) {
        tag.putIntArray(key, value);
    }

    public void putIntArray(String key, List<Integer> value) {
        tag.putIntArray(key, value);
    }

    public void putLongArray(String key, long[] value) {
        tag.putLongArray(key, value);
    }

    public void putLongArray(String key, List<Long> value) {
        tag.putLongArray(key, value);
    }

    public void putBoolean(String key, boolean value) {
        tag.putBoolean(key, value);
    }

    @Nullable
    public Tag get(String key) {
        return tag.get(key);
    }

    public byte getTagType(String key) {
        return tag.getTagType(key);
    }

    public boolean contains(String key) {
        return tag.contains(key);
    }

    public boolean contains(String key, int type) {
        return tag.contains(key, type);
    }

    public byte getByte(String key) {
        return tag.getByte(key);
    }

    public short getShort(String key) {
        return tag.getShort(key);
    }

    public int getInt(String key) {
        return tag.getInt(key);
    }

    public long getLong(String key) {
        return tag.getLong(key);
    }

    public float getFloat(String key) {
        return tag.getFloat(key);
    }

    public double getDouble(String key) {
        return tag.getDouble(key);
    }

    public String getString(String key) {
        return tag.getString(key);
    }

    public byte[] getByteArray(String key) {
        return tag.getByteArray(key);
    }

    public int[] getIntArray(String key) {
        return tag.getIntArray(key);
    }

    public long[] getLongArray(String key) {
        return tag.getLongArray(key);
    }

    public TagWrapper getCompound(String key) {
        return new TagWrapper(tag.getCompound(key));
    }

    public ListTag getList(String key, int type) {
        return tag.getList(key, type);
    }

    public boolean getBoolean(String key) {
        return tag.getBoolean(key);
    }

    public void remove(String key) {
        tag.remove(key);
    }

    public boolean isEmpty() {
        return tag.isEmpty();
    }

    public TagWrapper copy() {
        return new TagWrapper(tag.copy());
    }

    public TagWrapper merge(TagWrapper source) {
        return new TagWrapper(tag.merge(source.tag));
    }

    public void accept(TagVisitor visitor) {
        tag.accept(visitor);
    }

    public StreamTagVisitor.ValueResult accept(StreamTagVisitor visitor) {
        return tag.accept(visitor);
    }

    public String getAsString() {
        return tag.getAsString();
    }

    public void acceptAsRoot(StreamTagVisitor visitor) {
        tag.acceptAsRoot(visitor);
    }
}
