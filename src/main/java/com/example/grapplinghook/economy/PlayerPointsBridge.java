package com.example.grapplinghook.economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerPointsBridge {

    private Object api;
    private Method lookMethod;
    private Method takeMethod;
    private boolean available = false;

    public boolean hook(Logger logger) {
        Plugin playerPointsPlugin = Bukkit.getPluginManager().getPlugin("PlayerPoints");
        if (playerPointsPlugin == null || !playerPointsPlugin.isEnabled()) {
            return false;
        }

        try {
            Method getApiMethod = playerPointsPlugin.getClass().getMethod("getAPI");
            this.api = getApiMethod.invoke(playerPointsPlugin);

            this.lookMethod = api.getClass().getMethod("look", UUID.class);
            this.takeMethod = api.getClass().getMethod("take", UUID.class, int.class);

            this.available = true;
            return true;
        } catch (ReflectiveOperationException e) {
            logger.severe("Found PlayerPoints, but couldn't hook into its API via reflection: " + e.getMessage());
            this.available = false;
            return false;
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public int look(UUID playerId) {
        if (!available) {
            return 0;
        }
        try {
            Object result = lookMethod.invoke(api, playerId);
            return (result instanceof Integer) ? (Integer) result : 0;
        } catch (ReflectiveOperationException e) {
            return 0;
        }
    }

    public boolean take(UUID playerId, int amount) {
        if (!available) {
            return false;
        }
        try {
            Object result = takeMethod.invoke(api, playerId, amount);
            return (result instanceof Boolean) && (Boolean) result;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }
              }
