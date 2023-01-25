package io.github.kidofcubes;

import java.util.ArrayList;
import java.util.List;

public class RpgItemMeta implements RpgItem{
    private List<String> tests = new ArrayList<>();

    @Override
    public void addTest(String test) {
        tests.add(test);
    }

    @Override
    public String getAsJSON() {
        return RpgPlugin.gson.toJson(tests);
    }
}
