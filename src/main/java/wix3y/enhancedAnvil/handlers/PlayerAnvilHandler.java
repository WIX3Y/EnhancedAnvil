package wix3y.enhancedAnvil.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wix3y.enhancedAnvil.EnhancedAnvil;

import java.util.Map;

public class PlayerAnvilHandler implements Listener {

    public PlayerAnvilHandler(EnhancedAnvil plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Prevent items from increasing their enchantment level when merged in anvil
     *
     * @param event the prepare anvil event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerAnvil(PrepareAnvilEvent event) {
        if (!(event.getView().getPlayer() instanceof Player player) || player.hasPermission("enhancedanvil.anvil.merge.bypass")) {
            return;
        }

        ItemStack input1 = event.getInventory().getItem(0);
        ItemStack input2 = event.getInventory().getItem(1);
        ItemStack result = event.getResult();

        if (input1 == null || input2 == null || result == null) {
            return;
        }
        if (!input1.hasItemMeta() || !input2.hasItemMeta() || !result.hasItemMeta()) {
            return;
        }

        ItemMeta input1Meta = input1.getItemMeta();
        ItemMeta input2Meta = input2.getItemMeta();
        ItemMeta resultMeta = result.getItemMeta();

        if (!input1Meta.hasEnchants() || !input2Meta.hasEnchants()) {
            return;
        }

        // Check if level increased
        for (Map.Entry<Enchantment, Integer> entry : resultMeta.getEnchants().entrySet()) {
            Enchantment enchant = entry.getKey();
            int level = entry.getValue();

            int maxInputLevel = Math.max(input1Meta.getEnchantLevel(enchant), input2Meta.getEnchantLevel(enchant));
            if (level > maxInputLevel) {
                resultMeta.addEnchant(enchant, maxInputLevel, true);
            }
        }

        result.setItemMeta(resultMeta);
        event.setResult(result);
    }

    /**
     * Prevent items with different item models from being merged in anvil
     *
     * @param event the prepare anvil event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerAnvilItemModel(PrepareAnvilEvent event) {
        if (!(event.getView().getPlayer() instanceof Player)) {
            return;
        }

        ItemStack input1 = event.getInventory().getItem(0);
        ItemStack input2 = event.getInventory().getItem(1);

        if (input1 == null || input2 == null || input1.getType() == Material.AIR || input2.getType() == Material.AIR) {
            return;
        }
        if (input2.getType() == Material.ENCHANTED_BOOK) {
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