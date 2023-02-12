package io.github.kidofcubes.rpgplugin;

import com.google.gson.Gson;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
            System.out.println("WE ARE LOADING FROM THING");
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
            System.out.println("WE ARE PARSING A THING "+new String(abyte,StandardCharsets.UTF_8));
            return visitor.visit(new String(abyte,StandardCharsets.UTF_8));
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

    T reference = null;
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
            System.out.println("REFERENCE IS NOT NULL "+reference);
            if(reference instanceof RpgObject rpgObject){
                return gson.toJson(rpgObject.toJson());
            }
            return gson.toJson(reference);
        }else{
            System.out.println("RETURNED DATUM");
            return datum;
        }
    }

//    public DynamicallySavedTag(ByteArrayTag orig){
//        this(new String(orig.getAsByteArray(),StandardCharsets.UTF_8));
//    }
    public DynamicallySavedTag(byte[] json) {
        this(new String(json,StandardCharsets.UTF_8));
    }
    public DynamicallySavedTag(T reference) {
        super(new byte[]{});
        this.reference=reference;
        datum=null;

    }
    public DynamicallySavedTag(String json) {
        super(new byte[]{});
        reference=null;
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
//        throw new UnsupportedOperationException("wtf");
        System.out.println("COPIED FROM "+getCallerMethodName());
        DynamicallySavedTag<T> copy = new DynamicallySavedTag<T>(datum);
        copy.setObject(reference);
        return copy;
    }
    private static StackWalker.StackFrame getCallerMethodName()
    {
        return StackWalker.
                getInstance().
                walk(stream -> stream.skip(1).findFirst().get());
    }
    @Override
    public byte @NotNull [] getAsByteArray() {
        System.out.println("asked for byte array from "+getCallerMethodName().getDescriptor()+" "+getCallerMethodName().getMethodName()+" "+getCallerMethodName().getClassName()+" "+getCallerMethodName().getFileName()+" "+getCallerMethodName().getLineNumber());
        if(reference!=null) {
            return ("DyNaMiCaLlYsAvEdTaG<"+reference.getClass()+">").getBytes();
        }else{
            return ("DyNaMiCaLlYsAvEdTaG<?>").getBytes();
        }
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
        System.out.println("asked for write (save??)");
    }

    @Override
    public int sizeInBytes() {
        System.out.println("asked for size");
        return 24 + getAsBytes().length;
    }

    @Override
    public String getAsString() {
        return "DYNAMICALLY_SAVED_TAG{"+reference+"}";
    }

    @Override
    public @NotNull String toString() {
        return getAsString();
    }

    @Override
    public boolean equals(Object object) {
//        System.out.println("checked my equality against "+"{"+object.getClass()+"}"+object);
        if(object instanceof DynamicallySavedTag<?> other) {
//            System.out.println("this is text");
//            System.out.println("equality is "+this.reference == other.reference||this.hashCode()==other.hashCode());
            return this.reference == other.reference||this.hashCode()==other.hashCode();

        }
//        System.out.println("false");
        return false;
    }

    @Override
    public int hashCode() {
//        System.out.println("asked for hash");
        int hash=17;
        if(datum!=null){
            hash = 37*hash + datum.hashCode();
        }
        if(reference!=null){
            hash = 37*hash + reference.hashCode();
        }
        return hash;
    }

    @Override
    public void accept(TagVisitor visitor) {
//        System.out.println("got visited by tjhe other one at "+getCallerMethodName());
//        visitor.visitByteArray(this);
        visitor.visitString(StringTag.valueOf("dynamicallySAVEDtag{"+reference+"}"));
    }

    @Override
    public int size() {
        System.out.println("asked for size");
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


    //this is like the this ones tostring?
    @Override
    public StreamTagVisitor.@NotNull ValueResult accept(StreamTagVisitor visitor) {
        System.out.println("DYNAMICALLY SAVED TAG GOT VISITED AND RETURNED "+getJsonData());
        return visitor.visit(getJsonData());
//        return visitor.visit("DynamicallySavedTag<"+reference+">");
    }
}
