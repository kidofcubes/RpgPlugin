package io.github.kidofcubes;


//add entity specific stuff here later
public interface RpgEntity extends RpgObject {

    default RpgEntity loadFromJson(String json) {
        RpgObject.super.loadFromJson(json);
        return this;
    }
}
