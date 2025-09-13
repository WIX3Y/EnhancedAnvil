package wix3y.enhancedAnvil.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wix3y.enhancedAnvil.EnhancedAnvil;

import java.util.Arrays;

public class PlayerEnchantHandler implements Listener {
    private final EnhancedAnvil plugin;
    private final int expLevelCost = 10;

    public PlayerEnchantHandler(EnhancedAnvil plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    /**
     * Prevents players without permission to use the enchanting table
     *
     * @param event the prepare item enchant event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEnchant(PrepareItemEnchantEvent event) {
        for (int i=0; i<event.getOffers().length; i++) {
            if (i == 0 && !event.getEnchanter().hasPermission("enhancedanvil.enchant.low")) {
                event.getOffers()[i] = null;
            }
            else if (i == 1 && !event.getEnchanter().hasPermission("enhancedanvil.enchant.medium")) {
                event.getOffers()[i] = null;
            }
            else if (i == 2 && !event.getEnchanter().hasPermission("enhancedanvil.enchant.high")) {
                event.getOffers()[i] = null;
            }
            event.getView().setOffers(event.getOffers());
        }
    }

    /**
     * Allow players to enchant runes in enchanting table
     *
     * @param event the prepare item enchant event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPrepareRuneEnchant(PrepareItemEnchantEvent event) {
        ItemStack item = event.getItem();
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (!meta.hasEnchants() && meta.hasItemModel() && (
                    meta.getItemModel().equals(new NamespacedKey("enhancedanvil", "rune")) ||
                    meta.getItemModel().equals(new NamespacedKey("enhancedanvil", "enchanted_rune_low")) ||
                    meta.getItemModel().equals(new NamespacedKey("enhancedanvil", "enchanted_rune_medium")) ||
                    meta.getItemModel().equals(new NamespacedKey("enhancedanvil", "enchanted_rune_high"))
            )) {

                Arrays.fill(event.getOffers(), null);
                int numbBookshelves = event.getEnchantmentBonus();
                Player player = event.getEnchanter();

                if (numbBookshelves >= 0 && player.hasPermission("enhancedanvil.enchant.low")) {
                    event.getOffers()[0] = new EnchantmentOffer(Enchantment.UNBREAKING, 1, expLevelCost);
                }
                if (numbBookshelves >= 8 && player.hasPermission("enhancedanvil.enchant.medium")) {
                    event.getOffers()[1] = new EnchantmentOffer(Enchantment.UNBREAKING, 1, expLevelCost);
                }
                if (numbBookshelves >= 15 && player.hasPermission("enhancedanvil.enchant.high")) {
                    event.getOffers()[2] = new EnchantmentOffer(Enchantment.UNBREAKING, 1, expLevelCost);
                }
            }
        }
    }

    /**
     * Allow players to enchant runes in enchanting table
     *
     * @param event the item enchant event
     */
    @EventHandler
    public void onPlayerRuneEnchant(EnchantItemEvent event) {
        ItemStack item = event.getItem();
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            if (!meta.hasEnchants() && meta.hasItemModel() && (
                    meta.getItemModel().equals(new NamespacedKey("enhancedanvil", "rune")) ||
                    meta.getItemModel().equals(new NamespacedKey("enhancedanvil", "enchanted_rune_low")) ||
                    meta.getItemModel().equals(new NamespacedKey("enhancedanvil", "enchanted_rune_medium")) ||
                    meta.getItemModel().equals(new NamespacedKey("enhancedanvil", "enchanted_rune_high"))
            )) {

                // apply enchants to rune
                int cost = event.getExpLevelCost();
                if (cost == expLevelCost) {
                        event.getEnchantsToAdd().clear();
                        event.getEnchantsToAdd().put(Enchantment.UNBREAKING, 1);
                }
                else {
                    plugin.getLogger().warning("Exp level cost " + cost + " does not match any tier. Runestone will not be enchanted for player " + event.getEnchanter().getName() + ".");
                }
            }
        }
    }
}