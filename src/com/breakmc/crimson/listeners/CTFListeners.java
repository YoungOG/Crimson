package com.breakmc.crimson.listeners;

import com.breakmc.crimson.Crimson;
import com.breakmc.crimson.managers.CTFManager;
import com.breakmc.crimson.objects.CTF;
import com.breakmc.crimson.region.Region;
import com.breakmc.crimson.region.RegionUser;
import com.breakmc.crimson.region.Selection;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CTFListeners implements Listener {

    private Crimson main = Crimson.getInstance();
    private CTFManager cm = main.getCtfManager();
    private HashMap<UUID, Integer> hit = new HashMap<>();

    public CTFListeners() {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (!(p.hasPermission("admin"))) {
            for (CTF ctf : cm.getCTFS()) {
                if (ctf.getCarrier() == p) {
                    if (!(e.getMessage().startsWith("/report"))) {
                        p.sendMessage(ChatColor.RED + "You cannot issue commands as the flag carrier!");
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEnderpearl(PlayerInteractEvent e) {
        if (e.getItem() != null && e.getItem().getType() == Material.ENDER_PEARL) {
            Player p = e.getPlayer();
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                for (CTF ctf : cm.getCTFS()) {
                    if (ctf.getCarrier() == p) {
                        p.sendMessage(ChatColor.RED + "You cannot enderpearl as the flag carrier.");
                        e.setCancelled(true);
                        p.updateInventory();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            Player d = (Player) e.getDamager();
            for (CTF ctf : cm.getCTFS()) {

                if (ctf.getCarrier() == d) {
                    e.setCancelled(true);

                    if (d != p)
                        d.sendMessage(ChatColor.RED + "You cannot deal damage to other players as the flag carrier.");
                    return;
                }

                if (ctf.getCarrier() == p) {
                    if (hit.containsKey(p.getUniqueId())) {
                        if (hit.get(p.getUniqueId()) >= 4) {
                            hit.remove(p.getUniqueId());
                            Item item = p.getWorld().dropItem(p.getLocation(), ctf.getItemstack());
                            item.setPickupDelay(Integer.MAX_VALUE);
                            ctf.setItem(item);
                            ctf.setCarrier(null);
                            ctf.setDropped(true);
                            ctf.setLastDroppedLocation(item.getLocation());
                            Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "The " + ctf.getColor() + ctf.getID() + ChatColor.DARK_AQUA + " flag was dropped at the following coordinates: " + ChatColor.AQUA + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + ChatColor.DARK_AQUA + "!");
                        } else {
                            hit.put(p.getUniqueId(), hit.get(p.getUniqueId()) + 1);
                        }
                    } else {
                        hit.put(p.getUniqueId(), 1);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBow(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Projectile && e.getEntity() instanceof Player) {
            Projectile projectile = (Projectile) e.getDamager();
            if (projectile.getShooter() instanceof Player) {

                Player p = (Player) projectile.getShooter();
                for (CTF ctf : cm.getCTFS()) {
                    if (ctf.getCarrier() == p) {
                        p.sendMessage(ChatColor.RED + "You cannot deal damage to other players as the flag carrier.");
                        e.setCancelled(true);
                        p.updateInventory();
                    }
                }

            }
        }
        if (e.getDamager() instanceof Arrow && e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            for (CTF ctf : cm.getCTFS()) {
                if (ctf.getCarrier() == p) {
                    Item item = p.getWorld().dropItem(p.getLocation(), ctf.getItemstack());
                    item.setPickupDelay(Integer.MAX_VALUE);
                    ctf.setItem(item);
                    ctf.setCarrier(null);
                    ctf.setDropped(true);
                    ctf.setLastDroppedLocation(item.getLocation());
                    Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "The " + ctf.getColor() + ctf.getID() + ChatColor.DARK_AQUA + " flag was dropped at the following coordinates: " + ChatColor.AQUA + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + ChatColor.DARK_AQUA + "!");
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        for (CTF ctf : cm.getCTFS()) {
            if (ctf.getCarrier() == p) {
                Item item = p.getWorld().dropItem(p.getLocation(), ctf.getItemstack());
                item.setPickupDelay(Integer.MAX_VALUE);
                ctf.setItem(item);
                ctf.setCarrier(null);
                ctf.setDropped(true);
                ctf.setLastDroppedLocation(item.getLocation());
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "The " + ctf.getColor() + ctf.getID() + ChatColor.DARK_AQUA + " flag was dropped at the following coordinates: " + ChatColor.AQUA + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + ChatColor.DARK_AQUA + "!");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        for (CTF ctf : cm.getCTFS()) {
            if (ctf.getCarrier() == p) {
                Item item = p.getWorld().dropItem(p.getLocation(), ctf.getItemstack());
                item.setPickupDelay(Integer.MAX_VALUE);
                ctf.setItem(item);
                ctf.setCarrier(null);
                ctf.setDropped(true);
                ctf.setLastDroppedLocation(item.getLocation());
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "The " + ctf.getColor() + ctf.getID() + ChatColor.DARK_AQUA + " flag was dropped at the following coordinates: " + ChatColor.AQUA + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + ChatColor.DARK_AQUA + "!");
            }
        }
    }

    @EventHandler
    public void onDespawn(ItemDespawnEvent e) {
        for (CTF ctf : cm.getCTFS()) {
            if (ctf.getItem() != null) {
                if (ctf.getItem() == e.getEntity()) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        ArrayList<Item> items = new ArrayList<>();

        for (Entity ent : e.getChunk().getEntities()) {
            if (ent instanceof Item) {
                items.add((Item) ent);
            }
        }

        for (Item item : items) {
            for (CTF ctf : Crimson.getInstance().getCtfManager().getCTFS()) {
                if (item == ctf.getItem()) {
                    System.out.println("Tried to unload chunk with CTF Flag!");
                    e.setCancelled(true);
                }
            }
        }
    }

    //ForceField V

    private final Map<UUID, Set<Location>> previousUpdates = new HashMap<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("ForceField Thread").build());

    @EventHandler
    public void shutdown(PluginDisableEvent event) {
        // Do nothing if plugin being disabled isn't CombatTagPlus
        if (event.getPlugin() != main) return;

        // Shutdown executor service and clean up threads
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ignore) {}

        // Go through all previous updates and revert spoofed blocks
        for (UUID uuid : previousUpdates.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            for (Location location : previousUpdates.get(uuid)) {
                Block block = location.getBlock();
                player.sendBlockChange(location, block.getType(), block.getData());
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();

        if ((Region.get(e.getTo()) == null || Region.get(e.getFrom()) == null) && Region.get(e.getTo()) != null) {
            boolean active = false;

            for (CTF ctf : main.getCtfManager().getCTFS()) {
                if (ctf.isCounting()) {
                    active = true;
                }
            }

            if (active) {
                RegionUser.getRegionUser(p).setExceptTeleportation(true);
                e.setCancelled(true);
                p.sendMessage("§cYou can not enter this area.");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void updateViewedBlocks(PlayerMoveEvent event) {
        // Do nothing if player hasn't moved over a whole block
        Location t = event.getTo();
        Location f = event.getFrom();
        if (t.getBlockX() == f.getBlockX() && t.getBlockY() == f.getBlockY() &&
                t.getBlockZ() == f.getBlockZ()) {
            return;
        }

        final Player player = event.getPlayer();

        boolean active = false;

        for (CTF ctf : main.getCtfManager().getCTFS()) {
            if (ctf.isCounting()) {
                active = true;
            }
        }

        if (active) {
            if ((Region.get(event.getTo()) == null || Region.get(event.getFrom()) == null) && Region.get(event.getTo()) != null) {
                RegionUser.getRegionUser(player).setExceptTeleportation(true);
                Location from = new Location(event.getFrom().getWorld(), (double) event.getFrom().getBlockX(), (double) event.getFrom().getBlockY(), (double) event.getFrom().getBlockZ(), event.getFrom().getYaw(), event.getFrom().getPitch());
                from.add(0.5, 0.0, 0.5);
                player.teleport(from);
                player.sendMessage("§cYou can not enter this area.");
            }
        }

        // Asynchronously send block changes around player
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                // Stop processing if player has logged off
                UUID uuid = player.getUniqueId();
                if (!player.isOnline()) {
                    previousUpdates.remove(uuid);
                    return;
                }

                // Update the players force field perspective and find all blocks to stop spoofing
                Set<Location> changedBlocks = getChangedBlocks(player);
                Material forceFieldMaterial = Material.STAINED_GLASS;
                byte forceFieldMaterialDamage = 9;

                Set<Location> removeBlocks;
                if (previousUpdates.containsKey(uuid)) {
                    removeBlocks = previousUpdates.get(uuid);
                } else {
                    removeBlocks = new HashSet<>();
                }

                for (Location location : changedBlocks) {
                    player.sendBlockChange(location, forceFieldMaterial, forceFieldMaterialDamage);
                    removeBlocks.remove(location);
                }

                // Remove no longer used spoofed blocks
                for (Location location : removeBlocks) {
                    Block block = location.getBlock();
                    player.sendBlockChange(location, block.getType(), block.getData());
                }

                previousUpdates.put(uuid, changedBlocks);
            }
        });
    }

    private Set<Location> getChangedBlocks(Player player) {
        Set<Location> locations = new HashSet<>();

        // Do nothing if player is not tagged
        boolean active = false;

        for (CTF ctf : main.getCtfManager().getCTFS()) {
            if (ctf.isCounting()) {
                active = true;
            }
        }

        if (!active) return locations;

        // Find the radius around the player
        int r = 7;
        Location l = player.getLocation();
        Location loc1 = l.clone().add(r, 0, r);
        Location loc2 = l.clone().subtract(r, 0, r);
        int topBlockX = loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
        int bottomBlockX = loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
        int topBlockZ = loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();
        int bottomBlockZ = loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();

        // Iterate through all blocks surrounding the player
        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                // Location corresponding to current loop
                Location location = new Location(l.getWorld(), (double) x, l.getY(), (double) z);

                if (Region.get(location) == null) continue;

                for (int i = -r; i < r; i++) {
                    Location loc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());

                    loc.setY(loc.getY() + i);

                    // Do nothing if the block at the location is not air
                    if (!loc.getBlock().getType().equals(Material.AIR)) continue;

                    // Add this location to locations
                    locations.add(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                }
            }
        }

        return locations;
    }

//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onMove(PlayerMoveEvent e) {
//        Player p = e.getPlayer();
//
//        RegionUser user = RegionUser.getRegionUser(p);
//        if ((e.getTo().getBlockX() != e.getFrom().getBlockX() || e.getTo().getBlockY() != e.getFrom().getBlockY() || e.getTo().getBlockZ() != e.getFrom().getBlockZ())) {
//            if ((Region.get(e.getTo()) == null || Region.get(e.getFrom()) == null) && Region.get(e.getTo()) != null) {
//                user.setExceptTeleportation(true);
//                Location from = new Location(e.getFrom().getWorld(), (double) e.getFrom().getBlockX(), (double) e.getFrom().getBlockY(), (double) e.getFrom().getBlockZ(), e.getFrom().getYaw(), e.getFrom().getPitch());
//                from.add(0.5, 0.0, 0.5);
//                p.teleport(from);
//                p.sendMessage("§cYou can not enter spawn.");
//                p.playEffect(from, Effect.PARTICLE_SMOKE, 20L);
//            }
//
//            Location loc = p.getLocation();
//            Location loc2 = new Location(p.getWorld(), (double) (loc.getBlockX() + 7), (double) (loc.getBlockY() + 7), (double) (loc.getBlockZ() + 7));
//            Location loc3 = new Location(p.getWorld(), (double) (loc.getBlockX() - 7), (double) (loc.getBlockY() - 7), (double) (loc.getBlockZ() - 7));
//            int minX = Utils.min(loc2.getBlockX(), loc3.getBlockX());
//            int minY = Utils.min(loc2.getBlockY(), loc3.getBlockY());
//            int minZ = Utils.min(loc2.getBlockZ(), loc3.getBlockZ());
//            int maxX = Utils.max(loc2.getBlockX(), loc3.getBlockX());
//            int maxY = Utils.max(loc2.getBlockY(), loc3.getBlockY());
//            int maxZ = Utils.max(loc2.getBlockZ(), loc3.getBlockZ());
//
//            for (int x = minX; x < maxX; ++x) {
//                for (int y = minY; y < maxY; ++y) {
//                    for (int z = minZ; z < maxZ; ++z) {
//                        Location location = new Location(p.getWorld(), (double) x, (double) y, (double) z);
//
//                        if (location.getY() < 255.0) {
//                            boolean verif = false;
//                            boolean active = false;
//
//                            for (CTF ctfs : Crimson.getInstance().getCtfManager().getCTFS()) {
//                                if (ctfs.isCounting()) {
//                                    active = true;
//                                }
//                            }
//
//                            for (Location clone = location.clone(); clone != null && !clone.getBlock().isLiquid() && clone.getBlock().isEmpty() && !verif; verif = true) {
//                                clone.add(0.0, -1.0, 0.0);
//                                if ((clone.getBlock().getType() == Material.WOOL && clone.getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE) || (clone.getBlock().getRelative(BlockFace.DOWN).getType() == Material.BEDROCK && clone.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.SPONGE)) {
//                                }
//                            }
//                            if (verif && active) {
//                                ArrayList<Location> taskLocs = new ArrayList<>();
//                                    if (regionTasks.containsKey(p.getUniqueId())) {
//                                        taskLocs = regionTasks.get(p.getUniqueId());
//                                    }
//
//                                    if (!taskLocs.contains(location) && p.getLocation().distance(location) <= 7) {
//                                        int decayTime = 5;
//                                        Material type = Material.STAINED_GLASS;
//                                        byte data = (byte)14;
//
//                                        p.sendBlockChange(location, type, data);
//
//                                        new BukkitRunnable() {
//                                            public void run() {
////                                                if () {
////                                                    p.sendBlockChange(location, Material.AIR, (byte)0);
////                                                    ArrayList<Location> taskLocs = new ArrayList<>();
////
////                                                    if (regionTasks.containsKey(p.getUniqueId())) {
////                                                        taskLocs = regionTasks.get(p.getUniqueId());
////                                                    }
////
////                                                    taskLocs.remove(location);
////                                                    regionTasks.put(p.getUniqueId(), taskLocs);
////                                                    this.cancel();
////                                                } else
//                                                if (p.getLocation().distance(location) > 7) {
//                                                    p.sendBlockChange(location, Material.AIR, (byte)0);
//                                                    ArrayList<Location> taskLocs = new ArrayList<>();
//
//                                                    if (regionTasks.containsKey(p.getUniqueId())) {
//                                                        taskLocs = regionTasks.get(p.getUniqueId());
//                                                    }
//
//                                                    taskLocs.remove(location);
//                                                    regionTasks.put(p.getUniqueId(), taskLocs);
//                                                    this.cancel();
//                                                }
//                                            }
//                                        }.runTaskTimer(Crimson.getInstance(), (long)decayTime, (long)decayTime);
//
//                                        taskLocs.add(location);
//                                        regionTasks.put(p.getUniqueId(), taskLocs);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }


//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onMove(PlayerMoveEvent e) {
//        Player p = e.getPlayer();
//
//        if (p != null) {
//            RegionUser user = RegionUser.getRegionUser(p);
//
//            boolean active = false;
//
//            for (CTF ctfs : Crimson.getInstance().getCtfManager().getCTFS()) {
//                if (ctfs.isCounting()) {
//                    active = true;
//                }
//            }
//
//            if (active && (e.getTo().getBlockX() != e.getFrom().getBlockX() || e.getTo().getBlockY() != e.getFrom().getBlockY() || e.getTo().getBlockZ() != e.getFrom().getBlockZ())) {
//                if ((Region.get(e.getTo()) == null || Region.get(e.getFrom()) == null) && Region.get(e.getTo()) != null) {
//                    user.setExceptTeleportation(true);
//
//                    Location from = new Location(e.getFrom().getWorld(), (double)e.getFrom().getBlockX(), (double)e.getFrom().getBlockY(), (double)e.getFrom().getBlockZ(), e.getFrom().getYaw(), e.getFrom().getPitch());
//                    from.add(0.5, 0.0, 0.5);
//
//                    p.teleport(from);
//                    p.sendMessage(ChatColor.RED + "You are not allowed in this area!");
//                }
//
//                Location loc = p.getLocation();
//                Location loc2 = new Location(p.getWorld(), (double)(loc.getBlockX() + 7), (double)(loc.getBlockY() + 7), (double)(loc.getBlockZ() + 7));
//                Location loc3 = new Location(p.getWorld(), (double)(loc.getBlockX() - 7), (double)(loc.getBlockY() - 7), (double)(loc.getBlockZ() - 7));
//                int minX = Utils.min(loc2.getBlockX(), loc3.getBlockX());
//                int minY = Utils.min(loc2.getBlockY(), loc3.getBlockY());
//                int minZ = Utils.min(loc2.getBlockZ(), loc3.getBlockZ());
//                int maxX = Utils.max(loc2.getBlockX(), loc3.getBlockX());
//                int maxY = Utils.max(loc2.getBlockY(), loc3.getBlockY());
//                int maxZ = Utils.max(loc2.getBlockZ(), loc3.getBlockZ());
//
//                for (int x = minX; x < maxX; ++x) {
//                    for (int y = minY; y < maxY; ++y) {
//                        for (int z = minZ; z < maxZ; ++z) {
//                            Location location = new Location(p.getWorld(), (double)x, (double)y, (double)z);
//
//                            if (location.getY() < 255.0) {
//                                boolean verified = false;
//
//                                for (Location clone = location.clone(); clone != null && !clone.getBlock().isLiquid() && clone.getBlock().isEmpty() && !verified; verified = true) {
//                                    clone.add(0.0, -1.0, 0.0);
//                                    if (clone.getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE || clone.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.SPONGE) {}
//                                }
//
//                                if (verified) {
//                                    ArrayList<Location> taskLocs = new ArrayList<>();
//
//                                    if (regionTasks.containsKey(p.getUniqueId())) {
//                                        taskLocs = regionTasks.get(p.getUniqueId());
//                                    }
//
//                                    if (!taskLocs.contains(location) && p.getLocation().distance(location) <= 7) {
//                                        int decayTime = 5;
//                                        Material type = Material.STAINED_GLASS;
//                                        byte data = (byte)14;
//
//                                        p.sendBlockChange(location, type, data);
//
//                                        final boolean finalActive = active;
//
//                                        new BukkitRunnable() {
//                                            public void run() {
//                                                if (!finalActive) {
//                                                    p.sendBlockChange(location, Material.AIR, (byte)0);
//                                                    ArrayList<Location> taskLocs = new ArrayList<>();
//
//                                                    if (regionTasks.containsKey(p.getUniqueId())) {
//                                                        taskLocs = regionTasks.get(p.getUniqueId());
//                                                    }
//
//                                                    taskLocs.remove(location);
//                                                    regionTasks.put(p.getUniqueId(), taskLocs);
//                                                    this.cancel();
//                                                } else if (p.getLocation().distance(location) > 7) {
//                                                    p.sendBlockChange(location, Material.AIR, (byte)0);
//                                                    ArrayList<Location> taskLocs = new ArrayList<>();
//
//                                                    if (regionTasks.containsKey(p.getUniqueId())) {
//                                                        taskLocs = regionTasks.get(p.getUniqueId());
//                                                    }
//
//                                                    taskLocs.remove(location);
//                                                    regionTasks.put(p.getUniqueId(), taskLocs);
//                                                    this.cancel();
//                                                }
//                                            }
//                                        }.runTaskTimer(Crimson.getInstance(), (long)decayTime, (long)decayTime);
//
//                                        taskLocs.add(location);
//                                        regionTasks.put(p.getUniqueId(), taskLocs);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getItem() != null && e.getItem().getType() == Material.BONE && e.getClickedBlock() != null && p.hasPermission("crimson.wand") && p.getGameMode() == GameMode.CREATIVE) {
            RegionUser user = RegionUser.getRegionUser(p);

            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                e.setCancelled(true);
                Selection selection = (user.getSelection() != null) ? user.getSelection() : new Selection(null, null);
                Location location = e.getClickedBlock().getLocation();
                location.setY(0.0);
                selection.setLocation1(location);
                user.setSelection(selection);
                p.sendMessage("Selection 1: " + location.getWorld().getName() + " - " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                e.setCancelled(true);
                Selection selection = (user.getSelection() != null) ? user.getSelection() : new Selection(null, null);
                Location location = e.getClickedBlock().getLocation();
                location.setY((double) location.getWorld().getMaxHeight());
                selection.setLocation2(location);
                user.setSelection(selection);
                p.sendMessage("Selection 2: " + location.getWorld().getName() + " - " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
            }
        }
    }
}
