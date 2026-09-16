package com.example.grapplinghook.listeners;

import com.example.grapplinghook.GrapplingHookPlugin;
import com.example.grapplinghook.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GrappleListener implements Listener {

    private final GrapplingHookPlugin plugin;
    private final NamespacedKey projectileKey;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public GrappleListener(GrapplingHookPlugin plugin) {
        this.plugin = plugin;
        this.projectileKey = new NamespacedKey(plugin, "grapple_projectile");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!ItemUtils.isGrapplingHook(plugin, itemInHand)) {
            return;
        }

        event.setCancelled(true);

        UUID uuid = player.getUniqueId();
        int cooldownSeconds = plugin.getConfigManager().getCooldownSeconds();
        long cooldownMillis = cooldownSeconds * 1000L;
        long now = System.currentTimeMillis();

        Long lastUse = cooldowns.get(uuid);
        if (lastUse != null && (now - lastUse) < cooldownMillis) {
            long remainingSeconds = (cooldownMillis - (now - lastUse)) / 1000 + 1;
            String msg = plugin.getConfigManager().getMsgCooldownActive()
                    .replace("%seconds%", String.valueOf(remainingSeconds));
            player.sendMessage(ItemUtils.color(msg));
            return;
        }

        cooldowns.put(uuid, now);

        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(player.getLocation().getDirection().multiply(2.2));
        projectile.getPersistentDataContainer().set(projectileKey, PersistentDataType.BYTE, (byte) 1);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.5f);
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0, 1, 0), 15, 0.2, 0.2, 0.2, 0.1);
        player.sendActionBar(ItemUtils.color(plugin.getConfigManager().getMsgHookFired()));
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball snowball)) {
            return;
        }
        if (!snowball.getPersistentDataContainer().has(projectileKey, PersistentDataType.BYTE)) {
            return;
        }
        if (!(snowball.getShooter() instanceof Player player)) {
            return;
        }

        Location targetLoc;
        if (event.getHitBlock() != null) {
            targetLoc = event.getHitBlock().getLocation().add(0.5, 0.5, 0.5);
        } else if (event.getHitEntity() != null) {
            targetLoc = event.getHitEntity().getLocation();
        } else {
            return;
        }

        Location playerLoc = player.getLocation();
        Vector direction = targetLoc.toVector().subtract(playerLoc.toVector());

        if (direction.lengthSquared() < 0.0001) {
            return;
        }

        double pullStrength = plugin.getConfigManager().getPullStrength();
        double verticalBoost = plugin.getConfigManager().getVerticalBoost();

        Vector velocity = direction.normalize().multiply(pullStrength);
        velocity.setY(velocity.getY() + verticalBoost);

        player.setVelocity(velocity);
        player.setFallDistance(0f);

        player.getWorld().playSound(playerLoc, Sound.ENTITY_BAT_TAKEOFF, 1.0f, 1.2f);
        player.getWorld().spawnParticle(Particle.CLOUD, playerLoc, 20, 0.3, 0.1, 0.3, 0.05);
    }
                                    }
