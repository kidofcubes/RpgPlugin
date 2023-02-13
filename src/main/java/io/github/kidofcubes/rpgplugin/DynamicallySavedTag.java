package io.github.kidofcubes.rpgplugin;

import com.google.gson.Gson;
import io.netty.buffer.ByteBufOutputStream;
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
        public @NotNull DynamicallySavedTag<?> load(DataInput input, int depth, NbtAccounter tracker) throws IOException {
            tracker.accountBytes(24L);
            int j = input.readInt();
            com.google.common.base.Preconditions.checkArgument( j < 1 << 24); // Spigot

            tracker.accountBytes(j);
            byte[] abyte = new byte[j];

            input.readFully(abyte);
            return new DynamicallySavedTag<>(new String(abyte, StandardCharsets.UTF_8));
        }

        @Override
        public StreamTagVisitor.@NotNull ValueResult parse(DataInput input, StreamTagVisitor visitor) throws IOException {
            int i = input.readInt();
            byte[] abyte = new byte[i];
            input.readFully(abyte);
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
            return "Dynamically Saved Tag";
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
            if(reference instanceof RpgObject rpgObject){
                return gson.toJson(rpgObject.toJson());
            }
            return gson.toJson(reference);
        }else{
            return datum;
        }
    }
    public DynamicallySavedTag(byte[] json) {
        this(new String(json,StandardCharsets.UTF_8));
    }
    public DynamicallySavedTag(T reference) {
        super(new byte[]{});
        this.reference=reference;
        datum=null;
        System.out.println("INITIATING NEW DYNAMICALLY SAVED TAG WITH REFERENCE "+reference);
        try {
            throw new NullPointerException();
        }catch (NullPointerException exception){
            exception.printStackTrace();
        }

    }
    public DynamicallySavedTag(String json) {
        super(new byte[]{});
        reference=null;
        datum=json;
        System.out.println("INITIATING NEW DYNAMICALLY SAVED TAG WITH JSON "+datum);
        try {
            throw new NullPointerException();
        }catch (NullPointerException exception){
            exception.printStackTrace();
        }
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
        DynamicallySavedTag<T> copy = new DynamicallySavedTag<T>(datum);
        copy.setObject(reference);
        return copy;
    }
    public static StackWalker.StackFrame getCallerMethodName()
    {
        return StackWalker.
                getInstance().
                walk(stream -> stream.skip(8).findFirst().get());
    }
    @Override
    public byte @NotNull [] getAsByteArray() {
        return getAsString().getBytes(StandardCharsets.UTF_8);
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
//        System.out.println("CALLER METHODDDDDDDDDDDDDD IS "+getCallerMethodName().getClassName()+" "+getCallerMethodName().getMethodName()+" "
//                +getCallerMethodName().getDescriptor()+" "+getCallerMethodName().getFileName()+" "+getCallerMethodName().getLineNumber());
//        System.out.println("ASKED FOR WRITE ON A DATA OUTPUT OF TYPE "+output.getClass());
//        (new NullPointerException()).printStackTrace();
        byte[] datad;
        if(output instanceof ByteBufOutputStream){ //for internet reasons probably
            datad = ("DynamicallySavedTag@" + Integer.toHexString(hashCode())).getBytes();
        }else{
            datad = getAsBytes();
        }
        output.writeInt(datad.length);
        output.write(datad);
    }

    @Override
    public int sizeInBytes() {
//        System.out.println("ASKED FOR SIZE IN BYTES");
        return 24 + getAsBytes().length;
    }

    @Override
    public String getAsString() {
        if(reference!=null) {
            return ("DynamicallySavedTag<"+reference.getClass()+">");
        }else{
            return ("DynamicallySavedTag<?>");
        }
    }

    @Override
    public @NotNull String toString() {
        return getAsString();
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof DynamicallySavedTag<?> other) {
            return this.reference == other.reference||this.hashCode()==other.hashCode();

        }
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
        visitor.visitString(StringTag.valueOf(getAsString()));
    }

    @Override
    public int size() {
//        System.out.println("ASKED FOR SIZE");
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
    //not 100% sure what this even does
    @Override
    public StreamTagVisitor.@NotNull ValueResult accept(StreamTagVisitor visitor) {
//        System.out.println(" VISITED BY ACCEPT");
        return visitor.visit(getJsonData());
    }
}
