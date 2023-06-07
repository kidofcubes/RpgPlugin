package io.github.kidofcubes.rpgplugin;

import net.minecraft.nbt.CompoundTag;

public class StatInst {
    private final CompoundTag tag;
    public StatInst(CompoundTag compoundTag){
        tag=compoundTag;
    }
    public CompoundTag getTag(){
        return tag;
    }
}
