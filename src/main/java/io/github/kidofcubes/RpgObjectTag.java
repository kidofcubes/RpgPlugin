package io.github.kidofcubes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.nbt.*;

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
//    String text;


    public RpgObjectTag(RpgObject reference) {
//        super(reference.getAsJSON().getBytes(StandardCharsets.UTF_8));
        super(new byte[]{});
        this.reference=reference;

    }
    public RpgObjectTag(String json) {
//        super(reference.getAsJSON().getBytes(StandardCharsets.UTF_8));
        super(json.getBytes(StandardCharsets.UTF_8));
//        this.text=json;

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
            return reference.toJson().getBytes(StandardCharsets.UTF_8);
        }else{
            System.out.println("THAT WASNT SUPPOSED TO HAPPEN");
            return new byte[]{};
        }
    }

    public String getLoadedText(){
        return new String(getAsByteArray(),StandardCharsets.UTF_8);
    }

    @Override
    public void clear() {
        reference=null;
    }
    @Override
    public byte getId() {
        return 69;
    }
}
