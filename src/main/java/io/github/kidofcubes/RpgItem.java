package io.github.kidofcubes;

public interface RpgItem extends RpgObject{
    default RpgItem loadFromJson(String json) {
        System.out.println("LOADINGGGGGG A ITEM FROM JSON "+json);
        RpgObject.super.loadFromJson(json);
        System.out.println("LOADED A ITEM FROM JSON "+json);
        return this;
    }
}