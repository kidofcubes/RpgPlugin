package io.github.kidofcubes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import net.minecraft.nbt.*;


//i dont know what im doing, theres probably unnecessary code in here but it works and im not changing it
public class RpgObjectTag extends ByteArrayTag {
    public static final TagType<ByteArrayTag> TYPE = new TypeThing();


    static class TypeThing implements TagType.VariableSize<ByteArrayTag> {
        @Override
        public RpgObjectTag load(DataInput input, int depth, NbtAccounter tracker) throws IOException {
            tracker.accountBytes(24L);
            int j = input.readInt();
            com.google.common.base.Preconditions.checkArgument( j < 1 << 24); // Spigot

            tracker.accountBytes(1L * (long) j);
            byte[] abyte = new byte[j];

            input.readFully(abyte);
            return new RpgObjectTag(new String(abyte,StandardCharsets.UTF_8));
        }

        @Override
        public StreamTagVisitor.ValueResult parse(DataInput input, StreamTagVisitor visitor) throws IOException {
            int i = input.readInt();
            byte[] abyte = new byte[i];

            input.readFully(abyte);
            return visitor.visit(abyte);
        }

        @Override
        public void skip(DataInput input) throws IOException {
            input.skipBytes(input.readInt() * 1);
        }

        @Override
        public String getName() {
            return "RPGBYTE[]";
        }

        @Override
        public String getPrettyName() {
            return "TAG_RPGByte_Array";
        }
    };

    RpgObject reference;
    String datum;
    public byte[] getAsBytes(){
        if(reference!=null){
            return reference.toJson().getBytes(StandardCharsets.UTF_8);
        }else{
            return datum.getBytes(StandardCharsets.UTF_8);
        }
    };
//    String text;


    public RpgObjectTag(RpgObject reference) {
//        super(reference.getAsJSON().getBytes(StandardCharsets.UTF_8));
        super(new byte[]{});
        this.reference=reference;

    }
    public RpgObjectTag(String json) {
//        super(reference.getAsJSON().getBytes(StandardCharsets.UTF_8));
//        super(json.getBytes(StandardCharsets.UTF_8));
        super(new byte[]{});
        datum=json;
//        this.text=json;

    }

    @Override
    public Tag copy() {

        if(reference!=null){
            System.out.println("copied a tag with a reference");
            return new RpgObjectTag(reference.toJson());
        }
        System.out.println("copied a tag with no referece but data "+new String(getAsByteArray()));
        return new RpgObjectTag(new String(getAsByteArray()));
    }
//    public RpgObjectTag(byte[] value) {
//        super(value);
//    }
//
//    public RpgObjectTag(List<Byte> value) {
//        super(value);
//    }

    @Override
    public byte[] getAsByteArray() {
        if(reference!=null) {
            System.out.println("pulled a fast one on ya, gave you the json instead");
            return getAsBytes();
        }else{
            System.out.println("THAT WASNT SUPPOSED TO HAPPEN===========================================================================");
            return getAsBytes();
        }
    }

    @Override
    public TagType<ByteArrayTag> getType() {
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
    public String toString() {
        return "ITS ME THE RPGTAG "+super.toString();
    }

    @Override
    public boolean equals(Object object) {
        return this == object ? true : object instanceof RpgObjectTag && Arrays.equals(this.getAsBytes(), ((RpgObjectTag) object).getAsBytes());
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
        throw new UnsupportedOperationException("THIS IS A RPGOBJECTTAG");
    }

    @Override
    public ByteTag set(int i, ByteTag nbttagbyte) {
        throw new UnsupportedOperationException("THIS IS A RPGOBJECTTAG");
    }

    @Override
    public void add(int i, ByteTag nbttagbyte) {
        throw new UnsupportedOperationException("THIS IS A RPGOBJECTTAG");
    }

    @Override
    public boolean setTag(int index, Tag element) {
        throw new UnsupportedOperationException("THIS IS A RPGOBJECTTAG");
    }

    @Override
    public boolean addTag(int index, Tag element) {
        throw new UnsupportedOperationException("THIS IS A RPGOBJECTTAG");
    }

    @Override
    public ByteTag remove(int i) {
        throw new UnsupportedOperationException("THIS IS A RPGOBJECTTAG");
    }

    @Override
    public byte getElementType() {
        return super.getElementType();
    }

    @Override
    public StreamTagVisitor.ValueResult accept(StreamTagVisitor visitor) {
        return visitor.visit(getAsBytes());
    }
}
