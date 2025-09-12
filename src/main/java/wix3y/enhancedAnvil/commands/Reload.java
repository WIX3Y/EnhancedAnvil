package wix3y.enhancedAnvil.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import wix3y.enhancedAnvil.EnhancedAnvil;

public class Reload implements CommandExecutor {
    private final EnhancedAnvil plugin;

    public Reload(EnhancedAnvil plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        plugin.reload();
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<dark_gray>[<gradient:#44AAFF:#CCEEFF:#44AAFF>Enhanced Anvil</gradient>]</dark_gray> <gray>>> <green>Config reloaded!"));
        return true;
    }
}