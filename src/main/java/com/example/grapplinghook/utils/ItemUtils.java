package com.example.grapplinghook.utils;

import com.example.grapplinghook.GrapplingHookPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class ItemUtils {

    private ItemUtils() {
    }

    public static Component color(String legacyText) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(legacyText);
    }

    public static ItemStack createGrapplingHook(GrapplingHookPlugin plugin) {
        ItemStack item = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(color(plugin.getConfigManager().getItemName()));

            List<Component> lore = new ArrayList<>();
            for (String line : plugin.getConfig().getStringList("hook.item-lore")) {
                lore.add(color(line));
            }
            meta.lore(lore);

            meta.getPersistentDataContainer().set(
                    plugin.getGrappleKey(),
                    PersistentDataType.BYTE,
                    (byte) 1
            );

            item.setItemMeta(meta);
        }

        return item;
    }

    public static boolean isGrapplingHook(GrapplingHookPlugin plugin, ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer()
                .has(plugin.getGrappleKey(), PersistentDataType.BYTE);
    }
}
