package io.github.kidofcubes.rpgplugin;

import com.google.gson.Gson;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DynamicallySavedTag<T> extends ByteArrayTag {
    public static final TagType<ByteArrayTag> TYPE = new DynamicallySavedTag.TypeThing();

    private static final Gson gson = new Gson();

    public void setObject(T thing) {
        reference = thing;
    }

    public T getObject() {
        return reference;
    }


    static class TypeThing implements TagType.VariableSize<ByteArrayTag> {
        @Override
        public @NotNull DynamicallySavedTag load(DataInput input, int depth, NbtAccounter tracker) throws IOException {
            tracker.accountBytes(24L);
            int j = input.readInt();
            com.google.common.base.Preconditions.checkArgument( j < 1 << 24); // Spigot

            tracker.accountBytes(j);
            byte[] abyte = new byte[j];

            input.readFully(abyte);
            return new DynamicallySavedTag(new String(abyte, StandardCharsets.UTF_8));
        }

        @Override
        public StreamTagVisitor.@NotNull ValueResult parse(DataInput input, StreamTagVisitor visitor) throws IOException {
            int i = input.readInt();
            byte[] abyte = new byte[i];

            input.readFully(abyte);
            return visitor.visit(abyte);
        }

        @Override
        public void skip(DataInput input) throws IOException {
            input.skipBytes(input.readInt());
        }

        @Override
        public @NotNull String getName() {
            return "DynamicallySavedTag";
        }

        @Override
        public @NotNull String getPrettyName() {
            return "Dynamic Saved Tag";
        }
    }

    T reference;
    String datum;
    public byte[] getAsBytes(){
        String dataz = getJsonData();
        if(dataz==null){
            return new byte[]{};
        }
        return dataz.getBytes(StandardCharsets.UTF_8);
    }

    public String getJsonData(){
        if(reference!=null){
            if(reference instanceof RpgObject rpgObject){
                return gson.toJson(rpgObject.toJson());
            }
            return gson.toJson(reference);
        }else{
            return datum;
        }
    }

    public DynamicallySavedTag(ByteArrayTag orig){
        this(new String(orig.getAsByteArray(),StandardCharsets.UTF_8));
    }
    public DynamicallySavedTag(byte[] json) {
        this(new String(json,StandardCharsets.UTF_8));
    }
    public DynamicallySavedTag(T reference) {
        super(new byte[]{});
        this.reference=reference;

    }
    public DynamicallySavedTag(String json) {
        super(new byte[]{});
        datum=json;
    }

    public void unload(){
        if(reference!=null){
            if(reference instanceof RpgObject rpgObject){
                datum = gson.toJson(rpgObject.toJson());
            }else {
                datum = gson.toJson(reference);
            }
        }
        reference=null;
    }

    @Override
    public @NotNull Tag copy() {
        return new DynamicallySavedTag<T>(getJsonData());
    }
    @Override
    public byte @NotNull [] getAsByteArray() {
        return getAsBytes();
    }

    @Override
    public @NotNull TagType<ByteArrayTag> getType() {
        return DynamicallySavedTag.TYPE;
    }


    @Override
    public void clear() {
        reference=null;
        datum=null;
    }
    @Override
    public byte getId() {
        return super.getId();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        byte[] datad=getAsBytes();
        output.writeInt(datad.length);
        output.write(datad);
    }

    @Override
    public int sizeInBytes() {
        return 24 + getAsBytes().length;
    }

    @Override
    public @NotNull String toString() {
        return "DYNAMICALLY_SAVED_TAG {"+super.toString()+"}";
    }

    @Override
    public boolean equals(Object object) {
        return this == object ;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getAsBytes());
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitByteArray(this);
    }

    @Override
    public int size() {
        return getAsBytes().length;
    }

    @Override
    public ByteTag get(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ByteTag set(int i, @NotNull ByteTag nbttagbyte) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int i, @NotNull ByteTag nbttagbyte) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setTag(int index, @NotNull Tag element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addTag(int index, @NotNull Tag element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteTag remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte getElementType() {
        return super.getElementType();
    }

    @Override
    public StreamTagVisitor.@NotNull ValueResult accept(StreamTagVisitor visitor) {
        return visitor.visit(getAsBytes());
    }
}
