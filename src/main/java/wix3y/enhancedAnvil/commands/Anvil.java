package wix3y.enhancedAnvil.commands;

import net.advancedplugins.ae.api.AEAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import wix3y.enhancedAnvil.EnhancedAnvil;
import wix3y.enhancedAnvil.gui.AnvilGui;
import wix3y.enhancedAnvil.util.ConfigUtil;

import java.util.*;

public class Anvil implements Listener, CommandExecutor {
    private final EnhancedAnvil plugin;
    private int cost;
    private String anvilName;

    public Anvil(EnhancedAnvil plugin, ConfigUtil configUtil) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.cost = configUtil.getAnvilCost();
        this.anvilName = configUtil.getAnvilName();
    }

    /**
     * Reload parameters dependent on the config util
     *
     * @param configUtil the new config util
     */
    public void reloadConfigUtil(ConfigUtil configUtil) {
        this.cost = configUtil.getAnvilCost();
        this.anvilName = configUtil.getAnvilName();
    }

    /**
     * Open custom anvil gui
     *
     * @param sender who executed the command
     * @param command the command
     * @param label the command name
     * @param args arguments for the command (player to display statistics for, or none)
     * @return whether command was successful
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player;
        if (!(sender instanceof Player)) {
            if (args.length > 0) {
                player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid player name"));
                    return true;
                }
            }
            else {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>/eanvil <player>"));
                return true;
            }
        }
        else {
            player = (Player) sender;
        }

        if (!player.hasPermission("enhancedanvil.anvil.cost.bypass")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>" + player.getName() + " does not have permission to use this anvil."));
            return true;
        }

        AnvilGui page = new AnvilGui(plugin, anvilName);
        player.openInventory(page.getInventory());
        return true;
    }

    /**
     * Cancel inventory clicking in custom anvil gui
     *
     * @param event the inventory click event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder(false);

        if (holder instanceof AnvilGui anvilGui) {
            event.setCancelled(true);
            onAnvilGuiClick(anvilGui, event);
        }
    }

    /**
     * Return any remaining items in gui to player
     *
     * @param event the inventory click event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder(false);

        if (holder instanceof AnvilGui) {
            onAnvilGuiClose(event);
        }
    }

    /**
     * Return any items in slot 19 and 22 to player
     *
     * @param event inventory click event
     */
    private void onAnvilGuiClose(InventoryCloseEvent event) {
        if (!( event.getPlayer() instanceof  Player player)) {
            return;
        }
        ItemStack input1 = event.getInventory().getItem(19);
        ItemStack input2 = event.getInventory().getItem(22);

        event.getInventory().setItem(19, null);
        event.getInventory().setItem(22, null);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (input1 != null && input1.getType() != Material.AIR) {
                player.give(input1);
            }

            if (input2 != null && input2.getType() != Material.AIR) {
                player.give(input2);
            }
        }, 1L);
    }

    /**
     * Allow moving items between anvil gui and inventory
     * Allow claiming result item
     *
     * @param anvilGui the anvil gui
     * @param event inventory click event
     */
    private void onAnvilGuiClick(AnvilGui anvilGui, InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        ItemStack input1 = event.getInventory().getItem(19);
        ItemStack input2 = event.getInventory().getItem(22);
        ItemStack result = event.getInventory().getItem(25);

        // Click in result slot
        if (slot == 25 && input1 != null && input2 != null && result != null && input1.getType() != Material.AIR && input2.getType() != Material.AIR && result.getType() != Material.AIR) {
            if (player.getLevel() >= cost) {
                event.getInventory().setItem(19, null);
                event.getInventory().setItem(22, null);
                event.getInventory().setItem(25, null);
                player.setLevel(player.getLevel()-cost);
                player.give(result);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                anvilGui.showCost(false, 0);
            } else {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have enough xp to complete this action."));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            }
        }
        // Click in input slot 1
        else if (slot == 19 && input1 != null && input1.getType() != Material.AIR) {
            event.getInventory().setItem(19, null);
            event.getInventory().setItem(25, null);
            player.give(input1);
            anvilGui.showCost(false, 0);
        }
        // Click in input slot 2
        else if (slot == 22 && input2 != null && input2.getType() != Material.AIR) {
            event.getInventory().setItem(22, null);
            event.getInventory().setItem(25, null);
            player.give(input2);
            anvilGui.showCost(false, 0);
        }
        // Click in player inventory
        else if (slot >= event.getInventory().getSize()) {
            int clickedSlot = event.getSlot();
            ItemStack clickedItem = player.getInventory().getItem(clickedSlot);

            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                if (input1 == null || input1.getType() == Material.AIR) {
                    player.getInventory().setItem(clickedSlot, null);
                    event.getInventory().setItem(19, clickedItem);
                    if (input2 != null && input2.getType() != Material.AIR) {
                        checkSetResult(anvilGui, event, clickedItem, input2);
                    }
                }
                else if (input2 == null || input2.getType() == Material.AIR) {
                    player.getInventory().setItem(clickedSlot, null);
                    event.getInventory().setItem(22, clickedItem);
                    checkSetResult(anvilGui, event, input1, clickedItem);
                }
            }
        }
    }

    /**
     * Check if a result anvil item should be set in the gui and set it
     *
     * @param anvilGui the gui
     * @param event the click event that triggered the check
     * @param input1 the item in the first input slot
     * @param input2 the item in the second input slot
     */
    private void checkSetResult (AnvilGui anvilGui, InventoryClickEvent event, ItemStack input1, ItemStack input2) {
        if (input1 == null || input2 == null || input1.getType() == Material.AIR || input2.getType() == Material.AIR) {
            return;
        }
        if (!isValidMaterial(input1.getType())) {
            return;
        }
        if (input2.getType() != Material.ENCHANTED_BOOK) {
            return;
        }
        EnchantmentStorageMeta input2Meta = (EnchantmentStorageMeta) input2.getItemMeta();

        ItemStack result = input1.clone();
        ItemMeta meta = result.getItemMeta();
        Map<Enchantment, Integer> enchantsToAdd = input2Meta.getStoredEnchants();
        List<Enchantment> currentEnchants = new ArrayList<>(meta.getEnchants().keySet());

        boolean changed = false;
        // Add potential vanilla enchantments
        for (Map.Entry<Enchantment, Integer> enchantToAdd: enchantsToAdd.entrySet()) {
            Enchantment enchant = enchantToAdd.getKey();

            if (isMutuallyExclusive(enchant, currentEnchants)) {
                continue;
            }
            if (!enchant.canEnchantItem(result)) {
                continue;
            }

            int newLevel = enchantToAdd.getValue();
            int existingLevel = meta.hasEnchant(enchant) ? meta.getEnchantLevel(enchant) : 0;
            if (newLevel > existingLevel) {
                meta.addEnchant(enchant, newLevel, true);
                changed = true;
            }
        }
        result.setItemMeta(meta);

        // Add potential advanced enchantments enchants
        if (AEAPI.isCustomEnchantBook(input2)) {
            String aeEnchantment = AEAPI.getBookEnchantment(input2);
            if (aeEnchantment != null && AEAPI.isApplicable(result.getType(), aeEnchantment)) {
                int aeEnchantLevel = AEAPI.getBookEnchantmentLevel(input2);

                if (!AEAPI.hasCustomEnchant(aeEnchantment, input1) || (AEAPI.hasCustomEnchant(aeEnchantment, input1) && aeEnchantLevel > AEAPI.getEnchantLevel(aeEnchantment, input1))) {
                    AEAPI.applyEnchant(aeEnchantment, aeEnchantLevel, result);
                    changed = true;
                }
            }
        }

        if (!changed) {
            return;
        }

        event.getInventory().setItem(25, result);
        anvilGui.showCost(true, cost);
    }

    /**
     * Check if enchantment is mutually exclusive with an enchantment in a list
     *
     * @param enchantment the enchantment
     * @param currentEnchantments the list of enchantments
     * @return true if the enchantment is mutually exclusive with any enchantment in the list
     */
    private boolean isMutuallyExclusive(Enchantment enchantment, List<Enchantment> currentEnchantments) {
        for (Enchantment currentEnchantment: currentEnchantments) {
            if (enchantment.conflictsWith(currentEnchantment)) {
                if (!enchantment.equals(currentEnchantment)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if material is an anvilable material
     *
     * @param material the material
     * @return true if the material can be used in an anvil
     */
    private boolean isValidMaterial(Material material) {
        List<Material> validMaterials = Arrays.asList(
                // Swords
                Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
                Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
                // Pickaxes
                Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
                Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
                // Axes
                Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
                Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
                // Shovels
                Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL,
                Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL,
                // Hoes
                Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE,
                Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE,
                // Armor
                Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
                Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
                Material.IRON_HELMET, Material.IRON_CHESTPLATE,
                Material.IRON_LEGGINGS, Material.IRON_BOOTS,
                Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE,
                Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
                Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
                Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
                Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE,
                Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS,
                Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE,
                Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
                Material.TURTLE_HELMET,
                // Other
                Material.TRIDENT, Material.SHIELD, Material.ELYTRA,
                Material.FISHING_ROD, Material.CROSSBOW, Material.BOW,
                Material.SHEARS, Material.FLINT_AND_STEEL,
                Material.CARROT_ON_A_STICK, Material.WARPED_FUNGUS_ON_A_STICK
        );
        return validMaterials.contains(material);
    }
}