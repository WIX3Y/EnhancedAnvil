package wix3y.enhancedAnvil;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import wix3y.enhancedAnvil.commands.Anvil;
import wix3y.enhancedAnvil.commands.Reload;
import wix3y.enhancedAnvil.gui.AnvilGui;
import wix3y.enhancedAnvil.handlers.PlayerAnvilHandler;
import wix3y.enhancedAnvil.handlers.PlayerEnchantHandler;
import wix3y.enhancedAnvil.handlers.PlayerGrindstoneHandler;
import wix3y.enhancedAnvil.util.ConfigUtil;

public final class EnhancedAnvil extends JavaPlugin {
    private ConfigUtil configUtil;
    private Anvil anvilCommand;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.configUtil = new ConfigUtil(this);

        new PlayerEnchantHandler(this);
        new PlayerAnvilHandler(this);
        new PlayerGrindstoneHandler(this);

        anvilCommand = new Anvil(this, configUtil);
        getCommand("eanvil").setExecutor(anvilCommand);
        getCommand("eareload").setExecutor(new Reload(this));

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("      <gradient:#44AAFF:#CCEEFF:#44AAFF>Enhanced Anvil</gradient>"));
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("          <gray>v1.0.0"));
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("          <green>Enabled"));
        Bukkit.getConsoleSender().sendMessage("");
    }

    public void reload() {
        this.reloadConfig();
        this.configUtil = new ConfigUtil(this);
        anvilCommand.reloadConfigUtil(configUtil);
    }

    @Override
    public void onDisable() {
        // give any items in custom anvil gui to player
        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory topInv = player.getOpenInventory().getTopInventory();
            if (topInv.getHolder() instanceof AnvilGui gui) {

                ItemStack input1 = gui.getInventory().getItem(19);
                ItemStack input2 = gui.getInventory().getItem(22);

                gui.getInventory().setItem(19, null);
                gui.getInventory().setItem(22, null);

                if (input1 != null && input1.getType() != Material.AIR) {
                    player.give(input1);
                }

                if (input2 != null && input2.getType() != Material.AIR) {
                    player.give(input2);
                }
            }
        }

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("      <gradient:#44AAFF:#CCEEFF:#44AAFF>Enhanced Anvil</gradient>"));
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("          <gray>v1.0.0"));
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("         <red>Disabled"));
        Bukkit.getConsoleSender().sendMessage("");
    }
}