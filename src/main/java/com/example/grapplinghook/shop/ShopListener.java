package com.example.grapplinghook.shop;

import com.example.grapplinghook.GrapplingHookPlugin;
import com.example.grapplinghook.economy.PlayerPointsBridge;
import com.example.grapplinghook.utils.ItemUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class ShopListener implements Listener {

    private final GrapplingHookPlugin plugin;

    public ShopListener(GrapplingHookPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ShopGUI.ShopHolder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int hookSlot = plugin.getConfigManager().getHookSlot();
        if (event.getRawSlot() != hookSlot) {
            return;
        }

        purchaseHook(player);
    }

    private void purchaseHook(Player player) {
        PlayerPointsBridge points = plugin.getPlayerPoints();
        UUID uuid = player.getUniqueId();
        int price = plugin.getConfigManager().getShopPrice();

        int balance = points.look(uuid);

        if (balance < price) {
            String msg = plugin.getConfigManager().getMsgNotEnoughPoints()
                    .replace("%price%", String.valueOf(price));
            player.sendMessage(ItemUtils.color(msg));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        boolean success = points.take(uuid, price);
        if (!success) {
            player.sendMessage(ItemUtils.color("&cTransaction failed, please try again."));
            return;
        }

        player.getInventory().addItem(ItemUtils.createGrapplingHook(plugin));

        String msg = plugin.getConfigManager().getMsgPurchaseSuccess()
                .replace("%price%", String.valueOf(price));
        player.sendMessage(ItemUtils.color(msg));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.closeInventory();
    }
}
