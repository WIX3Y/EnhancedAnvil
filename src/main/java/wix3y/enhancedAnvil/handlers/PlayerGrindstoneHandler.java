package wix3y.enhancedAnvil.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wix3y.enhancedAnvil.EnhancedAnvil;

public class PlayerGrindstoneHandler implements Listener {

    public PlayerGrindstoneHandler(EnhancedAnvil plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Prevent items with different item models from being merged in grindstone
     *
     * @param event the prepare grindstone event
     */
    @EventHandler
    public void onPlayerGrindstoneItemModel(PrepareGrindstoneEvent event) {
        if (!(event.getView().getPlayer() instanceof Player)) {
            return;
        }

        ItemStack input1 = event.getInventory().getItem(0);
        ItemStack input2 = event.getInventory().getItem(1);

        if (input1 == null || input2 == null || input1.getType() == Material.AIR || input2.getType() == Material.AIR) {
            return;
        }
        if (!input1.hasItemMeta() && !input2.hasItemMeta()) {
            return;
        }

        if (input1.hasItemMeta() && input2.hasItemMeta()) {
            ItemMeta input1Meta = input1.getItemMeta();
            ItemMeta input2Meta = input2.getItemMeta();

            if (!input1Meta.hasItemModel() && !input2Meta.hasItemModel()) {
                return;
            }
            if (input1Meta.hasItemModel() && input2Meta.hasItemModel()) {
                if (input1Meta.getItemModel().equals(input2Meta.getItemModel())) {
                    return;
                }
            }
        }

        event.setResult(null);
    }
}