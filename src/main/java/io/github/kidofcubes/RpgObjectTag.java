package io.github.kidofcubes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;


//I don't know what im doing, there's probably unnecessary code in here, but it works and im not changing it
public class RpgObjectTag extends ByteArrayTag implements RpgObjectHolder{
    public static final TagType<ByteArrayTag> TYPE = new TypeThing();

    @Override
    public void setObject(RpgObject thing) {
        reference = thing;
    }

    @Override
    public RpgObject getObject() {
        return reference;
    }


    static class TypeThing implements TagType.VariableSize<ByteArrayTag> {
        @Override
        public @NotNull RpgObjectTag load(DataInput input, int depth, NbtAccounter tracker) throws IOException {
            tracker.accountBytes(24L);
            int j = input.readInt();
            com.google.common.base.Preconditions.checkArgument( j < 1 << 24); // Spigot

            tracker.accountBytes(j);
            byte[] abyte = new byte[j];

            input.readFully(abyte);
            return new RpgObjectTag(new String(abyte,StandardCharsets.UTF_8));
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
            return "RpgHolderTag";
        }

        @Override
        public @NotNull String getPrettyName() {
            return "Rpg object holder tag";
        }
    }

    RpgObject reference;
    String datum;
    public byte[] getAsBytes(){
        if(getJsonData()==null){
            return new byte[]{};
        }
        return getJsonData().getBytes(StandardCharsets.UTF_8);
    }
    @Override
    public String getJsonData(){
        if(reference!=null){
            return reference.toJson();
        }else{
            return datum;
        }
    }
    public RpgObjectTag(ByteArrayTag orig){
        super(new byte[]{});
        datum=new String(orig.getAsByteArray(),StandardCharsets.UTF_8);
    }

    public RpgObjectTag(RpgObject reference) {
        super(new byte[]{});
        this.reference=reference;

    }
    public RpgObjectTag(String json) {
        super(new byte[]{});
        datum=json;
    }
    public RpgObjectTag() {
        super(new byte[]{});

    }

    @Override
    public @NotNull Tag copy() {

        if(reference!=null){
            return new RpgObjectTag(reference.toJson());
        }
        return new RpgObjectTag(new String(getAsByteArray()));
    }
    @Override
    public byte @NotNull [] getAsByteArray() {
        return getAsBytes();
    }

    @Override
    public @NotNull TagType<ByteArrayTag> getType() {
        return RpgObjectTag.TYPE;
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
        return "RPG_TAG {"+super.toString()+"}";
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof RpgObjectTag && Arrays.equals(this.getAsBytes(), ((RpgObjectTag) object).getAsBytes());
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
