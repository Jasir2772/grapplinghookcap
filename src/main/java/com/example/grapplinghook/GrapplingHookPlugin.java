package com.example.grapplinghook;

import com.example.grapplinghook.config.ConfigManager;
import com.example.grapplinghook.listeners.GrappleListener;
import com.example.grapplinghook.shop.ShopGUI;
import com.example.grapplinghook.shop.ShopListener;
import com.example.grapplinghook.utils.ItemUtils;
import org.black_ixx.PlayerPoints;
import org.black_ixx.PlayerPointsAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GrapplingHookPlugin extends JavaPlugin implements CommandExecutor {

    private ConfigManager configManager;
    private NamespacedKey grappleKey;
    private PlayerPointsAPI playerPointsAPI;

    @Override
    public void onEnable() {
        this.grappleKey = new NamespacedKey(this, "is_grappling_hook");

        this.configManager = new ConfigManager(this);

        if (!getServer().getPluginManager().isPluginEnabled("PlayerPoints")) {
            getLogger().severe("PlayerPoints not found or not enabled! Disabling GrapplingHook.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.playerPointsAPI = PlayerPoints.getInstance().getAPI();
        getLogger().info("Hooked into PlayerPoints successfully.");

        getServer().getPluginManager().registerEvents(new GrappleListener(this), this);
        getServer().getPluginManager().registerEvents(new ShopListener(this), this);

        if (getCommand("hookshop") != null) {
            getCommand("hookshop").setExecutor(this);
        }
        if (getCommand("givehook") != null) {
            getCommand("givehook").setExecutor(this);
        }

        getLogger().info("GrapplingHook enabled successfully!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "hookshop" -> {
                ShopGUI.open(player, this);
                return true;
            }
            case "givehook" -> {
                player.getInventory().addItem(ItemUtils.createGrapplingHook(this));
                player.sendMessage(ItemUtils.color("&aYou received a Grappling Hook!"));
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public NamespacedKey getGrappleKey() {
        return grappleKey;
    }

    public PlayerPointsAPI getPlayerPointsAPI() {
        return playerPointsAPI;
    }
  }
