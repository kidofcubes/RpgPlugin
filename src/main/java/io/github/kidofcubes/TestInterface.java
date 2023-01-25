package io.github.kidofcubes;

import org.bukkit.NamespacedKey;

public interface TestInterface {
    NamespacedKey key = new NamespacedKey("rpgplugin", "rpgdata");

    public void addTest(String test);
    public String getAsJSON();

    public TestInterface loadFromJson(String json);
}
