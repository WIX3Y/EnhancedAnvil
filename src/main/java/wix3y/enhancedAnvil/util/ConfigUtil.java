package wix3y.enhancedAnvil.util;

import org.bukkit.configuration.file.FileConfiguration;
import wix3y.enhancedAnvil.EnhancedAnvil;

public class ConfigUtil {
    private final int anvilCost;
    private final String anvilName;

    public ConfigUtil(EnhancedAnvil plugin) {
        FileConfiguration config = plugin.getConfig();

        anvilCost = config.contains("CustomAnvilCost") ? config.getInt("CustomAnvilCost"): 40;
        anvilName = config.contains("CustomAnvilName") ? config.getString("CustomAnvilName"): "";
    }

    /**
     * Get the anvil cost to be used instead of "to expensive"
     *
     * @return the anvil cost
     */
    public int getAnvilCost() {
        return anvilCost;
    }

    /**
     * Get the custom anvil name to be displayed at the top of the gui
     *
     * @return the anvil cost
     */
    public String getAnvilName() {
        return anvilName;
    }
}