package com.example.grapplinghook.shop;

import com.example.grapplinghook.GrapplingHookPlugin;
import com.example.grapplinghook.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class ShopGUI {

    private ShopGUI() {
    }

    public static class ShopHolder implements InventoryHolder {
        private Inventory inventory;

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }
    }

    public static void open(Player player, GrapplingHookPlugin plugin) {
        Component title = ItemUtils.color(plugin.getConfigManager().getShopGuiTitle());
        ShopHolder holder = new ShopHolder();
        Inventory gui = Bukkit.createInventory(holder, 27, title);
        holder.setInventory(gui);

        int slot = plugin.getConfigManager().getHookSlot();
        gui.setItem(slot, buildShopDisplayItem(plugin));

        player.openInventory(gui);
    }

    private static ItemStack buildShopDisplayItem(GrapplingHookPlugin plugin) {
        ItemStack display = ItemUtils.createGrapplingHook(plugin);
        ItemMeta meta = display.getItemMeta();

        if (meta != null) {
            List<Component> lore = meta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            } else {
                lore = new ArrayList<>(lore);
            }
            lore.add(Component.empty());
            lore.add(ItemUtils.color("&ePrice: &a" + plugin.getConfigManager().getShopPrice() + " points"));
            lore.add(ItemUtils.color("&7Click to purchase!"));
            meta.lore(lore);
            display.setItemMeta(meta);
        }

        return display;
    }
}
