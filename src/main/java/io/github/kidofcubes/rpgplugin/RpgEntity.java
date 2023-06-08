package io.github.kidofcubes.rpgplugin;

import java.util.List;

public interface RpgEntity extends RPG {
    List<RPG> usedThings();
    void use(RPG rpg);
    void stopUsing(RPG rpg);
}
