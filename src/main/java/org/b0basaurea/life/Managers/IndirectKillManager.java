//package org.b0basaurea.life.Managers;
//
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.entity.EnderCrystal;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.Player;
//import org.bukkit.entity.minecart.ExplosiveMinecart;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.block.BlockIgniteEvent;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;
//import org.bukkit.event.entity.EntityDamageEvent;
//import org.bukkit.event.player.PlayerBucketEmptyEvent;
//import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.plugin.Plugin;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//public class IndirectKillManager implements Listener {
//
//    private final Map<UUID, UUID> explosiveOwners = new HashMap<>();
//    private final Map<Location, UUID> lavaOwners = new HashMap<>();
//    private final Map<Location, UUID> fireOwners = new HashMap<>();
//    private Plugin plugin;
//
//    public IndirectKillManager(Plugin plugin)
//    {
//        this.plugin = plugin;
//    }
//
//    public Player getIndirectKiller(Player dead) {
//        EntityDamageEvent lastDamage = dead.getLastDamageCause();
//
//        if (!(lastDamage instanceof EntityDamageByEntityEvent event)) {
//            return null;
//        }
//
//        Entity damager = event.getDamager();
//
//        UUID ownerId = explosiveOwners.get(damager.getUniqueId());
//
//        if (ownerId == null) return null;
//
//        return Bukkit.getPlayer(ownerId);
//    }
//
//    @EventHandler
//    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
//        if (event.getBucket() != Material.LAVA_BUCKET) return;
//
//        lavaOwners.put(
//                event.getBlockClicked().getRelative(event.getBlockFace()).getLocation(),
//                event.getPlayer().getUniqueId()
//        );
//    }
//
//    @EventHandler
//    public void onIgnite(BlockIgniteEvent event) {
//        if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) return;
//        if (event.getPlayer() == null) return;
//
//        fireOwners.put(
//                event.getBlock().getLocation(),
//                event.getPlayer().getUniqueId()
//        );
//    }
//
//    @EventHandler
//    public void onPlaceEndCrystal(PlayerInteractEvent event) {
//        if (event.getItem() == null) return;
//        if (event.getItem().getType() != Material.END_CRYSTAL) return;
//
//        Player player = event.getPlayer();
//
//        Bukkit.getScheduler().runTaskLater(plugin, () -> {
//            for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
//                if (entity instanceof EnderCrystal crystal) {
//                    explosiveOwners.put(crystal.getUniqueId(), player.getUniqueId());
//                }
//            }
//        }, 1L);
//    }
//
//    @EventHandler
//    public void onPlaceTntMinecart(PlayerInteractEvent event) {
//        if (event.getItem() == null) return;
//        if (event.getItem().getType() != Material.TNT_MINECART) return;
//
//        Player player = event.getPlayer();
//
//        Bukkit.getScheduler().runTaskLater(plugin, () -> {
//            for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
//                if (entity instanceof ExplosiveMinecart minecart) {
//                    explosiveOwners.put(minecart.getUniqueId(), player.getUniqueId());
//                }
//            }
//        }, 1L);
//    }
//
//
//}
