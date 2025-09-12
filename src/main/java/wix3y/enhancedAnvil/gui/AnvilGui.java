package wix3y.enhancedAnvil.gui;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import wix3y.enhancedAnvil.EnhancedAnvil;

public class AnvilGui implements InventoryHolder {
    private final Inventory inventory;

    public AnvilGui(EnhancedAnvil plugin, String anvilName) {
        String menuName = "<white>七七七七七七七七ㇹ七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七七" + anvilName;
        this.inventory = plugin.getServer().createInventory(this, 36, MiniMessage.miniMessage().deserialize(menuName));
        initialize();
    }

    /**
     * Set filler items in slots in inventory
     *
     */
    private void initialize() {
        ItemStack empty = new ItemStack(Material.PAPER);
        ItemMeta meta = empty.getItemMeta();
        meta.setItemModel(new NamespacedKey("enhancedanvil", "empty"));
        meta.setHideTooltip(true);
        empty.setItemMeta(meta);

        for (int i=0; i<inventory.getSize(); i++) {
            if (!(i==19 || i==22 || i==25)) {
                inventory.setItem(i, empty);
            }
        }
    }

    /**
     * Show or hide the anvil cost
     *
     * @param show true if the cost should be shown
     * @param cost the cost
     */
    public void showCost(boolean show, int cost) {
        if (show) {
            ItemStack costItem = new ItemStack(Material.PAPER);
            for (int i=29; i<inventory.getSize(); i++) {
                ItemMeta costMeta = costItem.getItemMeta();
                costMeta.setItemModel(new NamespacedKey("enhancedanvil", "cost_" + cost + "_" + i));
                costMeta.setHideTooltip(true);
                costItem.setItemMeta(costMeta);
                inventory.setItem(i, costItem);
            }
        }
        else {
            ItemStack empty = new ItemStack(Material.PAPER);
            ItemMeta meta = empty.getItemMeta();
            meta.setItemModel(new NamespacedKey("enhancedanvil", "empty"));
            meta.setHideTooltip(true);
            empty.setItemMeta(meta);
            for (int i=29; i<inventory.getSize(); i++) {
                inventory.setItem(i, empty);
            }
        }
    }

    /**
     * Get the inventory page
     *
     * @return the inventory
     */
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}