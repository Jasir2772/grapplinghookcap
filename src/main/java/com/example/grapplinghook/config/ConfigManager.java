package com.example.grapplinghook.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;

    private int cooldownSeconds;
    private double pullStrength;
    private double verticalBoost;
    private String itemName;

    private int shopPrice;
    private String shopGuiTitle;
    private int hookSlot;

    private String msgNotEnoughPoints;
    private String msgPurchaseSuccess;
    private String msgCooldownActive;
    private String msgHookFired;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();

        cooldownSeconds = cfg.getInt("hook.cooldown-seconds", 3);
        pullStrength = cfg.getDouble("hook.pull-strength", 2.2);
        verticalBoost = cfg.getDouble("hook.vertical-boost", 0.35);
        itemName = cfg.getString("hook.item-name", "&b&lGrappling Hook");

        shopPrice = cfg.getInt("shop.price", 500);
        shopGuiTitle = cfg.getString("shop.gui-title", "&8Token Shop");
        hookSlot = cfg.getInt("shop.hook-slot", 13);

        msgNotEnoughPoints = cfg.getString("messages.not-enough-points", "&cYou don't have enough points!");
        msgPurchaseSuccess = cfg.getString("messages.purchase-success", "&aPurchase successful!");
        msgCooldownActive = cfg.getString("messages.cooldown-active", "&cOn cooldown!");
        msgHookFired = cfg.getString("messages.hook-fired", "&7You fired the Grappling Hook!");
    }

    public int getCooldownSeconds() { return cooldownSeconds; }
    public double getPullStrength() { return pullStrength; }
    public double getVerticalBoost() { return verticalBoost; }
    public String getItemName() { return itemName; }

    public int getShopPrice() { return shopPrice; }
    public String getShopGuiTitle() { return shopGuiTitle; }
    public int getHookSlot() { return hookSlot; }

    public String getMsgNotEnoughPoints() { return msgNotEnoughPoints; }
    public String getMsgPurchaseSuccess() { return msgPurchaseSuccess; }
    public String getMsgCooldownActive() { return msgCooldownActive; }
    public String getMsgHookFired() { return msgHookFired; }
}
