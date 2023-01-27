package io.github.kidofcubes;

import com.google.gson.Gson;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

public class GlobalVariables {
    public static Logger logger;
    public static Gson gson = new Gson();
    public static RpgPlugin plugin;
}
