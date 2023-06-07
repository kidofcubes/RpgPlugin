//package io.github.kidofcubes.rpgplugin;
//
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.StreamTagVisitor;
//import net.minecraft.nbt.Tag;
//import net.minecraft.nbt.TagVisitor;
//import org.bukkit.Bukkit;
//import org.bukkit.Material;
//import org.bukkit.NamespacedKey;
//import org.bukkit.inventory.ItemStack;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.io.DataOutput;
//import java.io.IOException;
//import java.util.Objects;
//
//
////todo actually check the impact of calling .totag every time we .copy
//public class RpgObjectTag extends CompoundTag{
//    public static final NamespacedKey RpgObjectTagKey = new NamespacedKey("rpg_plugin", "rpg_object");
//
//    @Nullable
//    private RpgObject rpgObject;
//
//    private boolean isClone=false;
//    private RpgObjectTag(){
//        putBoolean("placeholder",true);
//    }
//    private RpgObjectTag(CompoundTag compoundTag){
//        super(compoundTag.copy().tags);
//        putBoolean("placeholder",true);
//    }
//    private RpgObjectTag(CompoundTag compoundTag, RpgObject rpgObject){
//        this(compoundTag);
//        this.rpgObject=rpgObject;
//    }
//    private RpgObjectTag(CompoundTag compoundTag, RpgObject rpgObject, boolean isClone){
//        this(compoundTag,rpgObject);
////        System.out.println("A CLONE HAS BEEN MADE A CLONE HAS BEEN MADE ALERT=-=--=_+_+!_+_=-++++++++++_________+++++++============");
//        this.isClone=isClone;
//    }
//
//    public static RpgObjectTag fromCompoundTag(@Nullable CompoundTag compoundTag){
//        if(compoundTag!=null){
//            System.out.println("LOADED NEW OBJECT TAG FROM "+compoundTag);
//            return new RpgObjectTag(compoundTag);
//        }else{
//            System.out.println("MADE NEW OBJECT TAG FROM NULL WTF");
//            return new RpgObjectTag();
//        }
//    }
//
//    @Override
//    public void write(@NotNull DataOutput output) throws IOException {
//        if(rpgObject!=null) {
//            System.out.println("GOT TOOTAGGED WRITTEN ON IS CLONE???? "+isClone);
////            try {throw new NullPointerException();}catch(NullPointerException exception){exception.printStackTrace();}
//            rpgObject.toTag().write(output);
//        }else{
//            super.write(output);
//        }
//    }
//
//    public boolean isClone(){
//        return isClone;
//    }
//
//    @Override
//    public @NotNull CompoundTag copy() {
////        System.out.println("WE HAVE BEEN COPIED ALERT WE HAVE BEEN COPIED=============================================================");
////        System.out.println("WE HAVE BEEN COPIED ALERT WE HAVE BEEN COPIED=============================================================");
////        System.out.println("WE HAVE BEEN COPIED ALERT WE HAVE BEEN COPIED=============================================================");
////        System.out.println("WE HAVE BEEN COPIED ALERT WE HAVE BEEN COPIED=============================================================");
////        System.out.println("WE HAVE BEEN COPIED ALERT WE HAVE BEEN COPIED=============================================================");
////        System.out.println("WE HAVE BEEN COPIED ALERT WE HAVE BEEN COPIED=============================================================");
////        System.out.println("WE HAVE BEEN COPIED ALERT WE HAVE BEEN COPIED=============================================================");
////        System.out.println("WE HAVE BEEN COPIED ALERT WE HAVE BEEN COPIED=============================================================");
////        System.out.println("WE HAVE BEEN COPIED ALERT WE HAVE BEEN COPIED=============================================================");
////        try {throw new NullPointerException();}catch (NullPointerException exception){exception.printStackTrace();}
//        return new RpgObjectTag(new CompoundTag(),rpgObject,true);
//
////        return rpgObject.toTag().copy(); //i die of death on copy
//    }
//
//    @Override
//    public void accept(TagVisitor visitor) {
////        System.out.println("GOT TOOTAGGED ACCEPTED ON A CLONE??? "+isClone);
////        try {
////            throw new NullPointerException();
////        }catch (NullPointerException exception){
////            exception.printStackTrace();
////        }
//        if(rpgObject!=null) {
//            System.out.println("visiting RpgObjectTag@"+hashCode()+" was "+rpgObject+isClone);
//            visitor.visitCompound(rpgObject.toTag());
//        }else{
//            System.out.println("visiting RpgObjectTag@"+hashCode()+" was null, am i clone? "+isClone);
//            visitor.visitCompound(this);
//        }
//    }
//
//    @Override
//    public StreamTagVisitor.ValueResult accept(StreamTagVisitor visitor) {
//        //nullcheck l8r
//        System.out.println("GOT TOOTAGGED ACCEPTED2");
//        if(rpgObject!=null) {
//            return rpgObject.toTag().accept(visitor);
//        }else{
//            return super.accept(visitor);
//        }
//    }
//
//    @Override
//    public int sizeInBytes() {
//        System.out.println("GOT TOOTAGGED SIZED");
//        if(rpgObject!=null) {
//            return rpgObject.toTag().sizeInBytes();
//        }else{
//            return super.sizeInBytes();
//        }
//    }
//
//    //when this called, this clone is actually going to be used i think
//    public RpgObject getRpgObject(){
//        if(isClone){
////            System.out.println("was clone, merging now cause smth");
////            System.out.println("WE ARE NOW MERGING===========================================!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
////            try {throw new NullPointerException();}catch (NullPointerException exception){exception.printStackTrace();}
////            System.out.println("RETURNING "+rpgObject+" AS A CLONE FROM RPGOBJECTTAG#"+hashCode());
//            return rpgObject;
////            if(rpgObject!=null) {
////                merge(rpgObject.toTag());
////            }
////            rpgObject=null;
////            isClone=false;
//        }
////        System.out.println("RETURNING "+rpgObject+" AS A ORIGINAL FROM RPGOBJECTTAG#"+hashCode());
//        return rpgObject;
//    }
//
//    private RpgObject rpgObject(){
//        return rpgObject;
//    }
//
//    public void setRpgObject(RpgObject rpgObject){
////        System.out.println("set the rpgobject of RpgObjectTag@"+hashCode()+" to "+rpgObject);
////        try {throw new NullPointerException();}catch (NullPointerException exception){exception.printStackTrace();}
//        this.rpgObject=rpgObject;
//        isClone=false;
//    }
//
//    public void unload(){
//        if(rpgObject!=null){
//            merge(rpgObject.toTag());
//            rpgObject=null;
//        }
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        if (this == object) {
//            return true;
//        } else if(object instanceof RpgObjectTag rpgObjectTag){
//            if(hashCode() == rpgObjectTag.hashCode()) return true;
////            System.out.println("checking equals");
////            try {
////                throw new NullPointerException();
////            }catch (NullPointerException exception){
////                exception.printStackTrace();
////            }
////            return object instanceof RpgObjectTag && Objects.equals(rpgObject, ((RpgObjectTag)object).rpgObject());
//            if(rpgObjectTag.isClone){
////                System.out.println("other is clone, i am "+isClone+" clone, the objects are "+this.rpgObject+" and "+rpgObjectTag.getRpgObject());
//                if(this.rpgObject==rpgObjectTag.rpgObject){
////                    System.out.println("was equal");
//                    return true;
//                }
//            };
//        }
//        return false;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash=17;
//        if(rpgObject!=null){
//            hash = 37*hash + rpgObject.hashCode();
//        }else{
//            hash = 37 * hash + tags.hashCode();
//        }
////        System.out.println("my "+isClone+"tags are: ");
////        tags.forEach((key,tag)->{
////            System.out.println(key+": "+tag.getAsString());
////        });
////        System.out.println("tags hash: "+hash);
////        if(rpgObject!=null){
//////            System.out.println("rpg hash: "+rpgObject.hashCode());
////        }
////        System.out.println("asked for hash, result was "+hash);
//        return hash;
//    }
//
//    @Override
//    public String getAsString() {
//        System.out.println("GOT A RPGOBJECTTAG AS STRING");
//        return super.getAsString();
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return false;
//    }
//}
//
//
//
////public class RpgObjectTag extends CompoundTag {
////
////    public static final NamespacedKey RpgObjectTagKey = new NamespacedKey("rpg_plugin", "rpg_object");
////    public static final String dataKey = ("data");
////    public static final String typeKey = ("type");
////
////
//////    public RpgObjectTag(RpgObject rpgObject){
//////        super();
//////        setObject(rpgObject);
//////        if(rpgObject.getRpgType()!=RpgObject.defaultTypeKey){
//////            setSavedType(rpgObject.getRpgType());
//////        }
//////    }
////    private RpgObjectTag(){
////        super();
////    }
////    DynamicallySavedTag<RpgObject> rpgDataTag;
////
////    @Nullable
////    public RpgObject getObject(){
////        if(rpgDataTag==null){
////            return null;
////        }
////        return rpgDataTag.getObject();
////    }
////
////    public void setObject(RpgObject object){
////        if(rpgDataTag==null){
////            rpgDataTag = new DynamicallySavedTag<RpgObject>(object);
////            put(dataKey,rpgDataTag);
////        }
////        rpgDataTag.setObject(object);
////    }
////
////    public void unload(){
////        if(contains(dataKey)){
////            ((DynamicallySavedTag<?>) Objects.requireNonNull(get(dataKey))).unload();
////        }
////    }
////
////
////    public String getJson(){
////        if(!contains(dataKey)){
////            return "";
////        }
////        return ((DynamicallySavedTag<?>)(Objects.requireNonNull(get(dataKey)))).getJsonData();
////    }
////
////    public void setSavedType(NamespacedKey key){
////        putString(typeKey,key.asString());
////    }
////    public NamespacedKey getSavedType(){
////        if(contains(typeKey)){
////            return NamespacedKey.fromString(getString(typeKey));
////        }
////        return RpgObject.defaultTypeKey;
////    }
////
////    public static RpgObjectTag fromCompoundTag(@Nullable CompoundTag compoundTag){
////        if(compoundTag instanceof RpgObjectTag rpgObjectTag){
////            return rpgObjectTag;
////        }
////        if(compoundTag!=null) {
////            RpgObjectTag instance = new RpgObjectTag();
////            if (compoundTag.contains(typeKey)) {
////                instance.putString(typeKey, compoundTag.getString(typeKey));
////            }
////            if (compoundTag.contains(dataKey)) {
////                if(compoundTag.get(dataKey) instanceof DynamicallySavedTag<?> dynamicallySavedTag) {
////                    instance.put(dataKey, dynamicallySavedTag);
////                }else {
////                    instance.put(dataKey, new DynamicallySavedTag<RpgObject>(compoundTag.getByteArray(dataKey)));
////                }
////            }
////            return instance;
////        }
////        return new RpgObjectTag();
////    }
////    private RpgObjectTag(Map<String,Tag> tagz){
////        super(tagz);
////
////    }
////
////    @Override
////    public CompoundTag copy() {
////        // Paper start - reduce memory footprint of NBTTagCompound
////        it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<String, Tag> ret = new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(this.tags.size(), 0.8f);
////        java.util.Iterator<java.util.Map.Entry<String, Tag>> iterator = (this.tags instanceof it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap) ? ((it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap)this.tags).object2ObjectEntrySet().fastIterator() : this.tags.entrySet().iterator();
////        while (iterator.hasNext()) {
////            Map.Entry<String, Tag> entry = iterator.next();
////            ret.put(entry.getKey(), entry.getValue().copy());
////        }
////
////        return new RpgObjectTag(ret);
////        // Paper end - reduce memory footprint of NBTTagCompound
////    }
////}
