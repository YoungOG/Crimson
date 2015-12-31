package com.breakmc.crimson.objects;

import code.breakmc.legacy.Legacy;
import code.breakmc.legacy.spawn.SpawnManager;
import com.breakmc.crimson.Crimson;
import com.breakmc.crimson.managers.MessageManager;
import com.breakmc.crimson.utils.PlayerUtility;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;


public class CTF {

    private Crimson main = Crimson.getInstance();
    private Legacy legacy = Legacy.getInstance();
    private SpawnManager sm = legacy.getSpawnManager();
    private MessageManager mm = main.getMessageManager();

    private Location loc;
    private Location lastDroppedLocation;
    private ItemStack itemstack;
    private Item item;
    private Player player;
    private Player tying;
    private int untying;
    private String id;
    private ChatColor color;
    private boolean active;
    private boolean angelic;
    private boolean counting;
    private int minutes;
    private int seconds;
    private String time;
    private boolean dropped;
    private boolean tied;
    private int fireworkitem;
    private int fireworkplayer;

    public CTF(String id) {
        this.id = id;
        this.color = ChatColor.WHITE;
        this.itemstack = new ItemStack(Material.WOOL);
    }

    public void start() {
        tied = true;
        final CTF ctf = this;
        checkFirework();

        itemstack.setDurability(getColorData(color));

        BukkitTask runnable = new BukkitRunnable() {
            public void run() {
                if (item == null && dropped) {
                    item = lastDroppedLocation.getWorld().dropItem(lastDroppedLocation, itemstack);
                    setItem(item);
                    item.setPickupDelay(Integer.MAX_VALUE);
                    item.getWorld().strikeLightningEffect(lastDroppedLocation);
                    item.teleport(lastDroppedLocation);
                    item.setVelocity(new Vector());
                    item.setTicksLived(Integer.MAX_VALUE);
                }
            }
        }.runTaskTimerAsynchronously(main, 20L, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (getCarrier() != null) {
                    if (sm.getSpawn().isInSpawnRadius(getCarrier().getLocation()) && active) {
                        active = false;
                        tying = null;
                        tied = true;
                        counting = false;
                        dropped = false;
                        item = null;
                        Bukkit.broadcastMessage(mm.getCaptureMessage(getCarrier(), ctf));
                        setCarrier(null);
                        runnable.cancel();
                        this.cancel();
                    }
                }
                for (Player player : PlayerUtility.getOnlinePlayers()) {
                    if (!(player.isDead())) {
                        if (item != null) {
                            if (player.getLocation().distance(item.getLocation()) < 2) {
                                if (!(tied) && (!(dropped)) && getCarrier() == null) {
                                    item.remove();
                                    item = null;
                                    player.sendMessage(ChatColor.RED + "You have picked up the " + color + getID() + ChatColor.DARK_AQUA + " Flag, run to spawn!");
                                    Bukkit.broadcastMessage(mm.getPickupMessage(player, ctf));
                                    setCarrier(player);
                                }
                            }
                        }

                        if (player.getLocation().distance(loc) < 4) {
                            if (getCarrier() == null) {
                                if (tying == null && tied) {
                                    tying = player;
                                    untying = 10;
                                } else {
                                    if (tying == player && tied) {
                                        if (untying == 0) {
                                            item.remove();
                                            item = null;
                                            player.sendMessage(ChatColor.RED + "You have successfully untied the " + color + getID() + ChatColor.DARK_AQUA + " Flag, run to spawn!");
                                            Bukkit.broadcastMessage(mm.getPickupMessage(player, ctf));
                                            setCarrier(player);
                                            tying = null;
                                            tied = false;
                                        } else {
                                            player.sendMessage(ChatColor.DARK_AQUA + "You are untying the " + color + getID() + ChatColor.DARK_AQUA + " Flag. " + ChatColor.AQUA + "(" + untying + ")");
                                            untying -= 1;
                                        }
                                    }
                                }
                            } else {
                                if (getCarrier().getLocation().distance(loc) > 4) {
                                    player.sendMessage(ChatColor.RED + "The " + color + getID() + " Flag has been taken!");
                                    player.sendMessage(ChatColor.RED + "You may find it located at the following coordinates: " + getCarrier().getLocation().getBlockX() + ", " + getCarrier().getLocation().getBlockY() + ", " + getCarrier().getLocation().getBlockZ() + "!");
                                    player.setVelocity(new Vector(0, 5, 0));
                                    sendBack(player, 3);
                                }
                            }
                        } else {
                            if (tying != null && tying == player && getCarrier() == null) {
                                player.sendMessage(ChatColor.RED + "You have failed to untie the " + color + getID() + ChatColor.DARK_AQUA + " Flag!");
                                tying = null;
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(main, 20L, 20L);
    }

    public void startCountdown(int m, final int s) {

        counting = true;
        this.minutes = m;
        this.seconds = s;
        final DecimalFormat formatter = new DecimalFormat("00");
        int i = m * 60 + s + 1;
        for (int x = 0; x < i; x++) {
            new BukkitRunnable() {
                @Override
                public void run() {

                    time = minutes + ":" + formatter.format(seconds);

                    if (seconds == -1) {
                        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "A CTF event is about to begin. " + ChatColor.AQUA + "(" + minutes + ":00" + ")");
                        minutes -= 1;
                        seconds = 59;
                    }

                    if (minutes == 0 && seconds == 30) {
                        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "A CTF event is about to begin. " + ChatColor.AQUA + "(" + time + ")");
                    }

                    if (minutes == 0 && seconds <= 5) {
                        time = "0" + ":" + formatter.format(seconds);
                        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "A CTF event is about to begin. " + ChatColor.AQUA + "(" + time + ")");
                    }


                    if (minutes <= 0 && seconds <= 0) {
                        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "The " + color + getID() + ChatColor.DARK_AQUA + " CTF has begun!");
                        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "You may find its flag located at the following coordinates: " + ChatColor.AQUA + "X: " + getLocation().getBlockX() + ", Y: " + getLocation().getBlockY() + ", Z: " + getLocation().getBlockZ() + ChatColor.DARK_AQUA + "!");
                        active = true;
                        counting = false;
                        item = loc.getWorld().dropItem(loc, itemstack);
                        setItem(item);
                        item.setPickupDelay(Integer.MAX_VALUE);
                        item.getWorld().strikeLightningEffect(loc);
                        item.teleport(loc);
                        item.setVelocity(new Vector());
                        item.setTicksLived(Integer.MAX_VALUE);
                        start();
                        this.cancel();
                    }

                    seconds -= 1;
                }
            }.runTaskLater(main, x * 2L);
        }
    }

    public void setDropped(boolean dropped) {
        if (dropped) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    setDropped(false);
                    Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "The " + color + getID() + ChatColor.DARK_AQUA + " Flag can now be picked up!");
                }
            }.runTaskLater(main, 20L * 15);
        }

        this.dropped = dropped;
    }

    public void setCarrier(Player p) {
        PacketPlayOutEntityEquipment packet;

        if (player != null) {
            player.removePotionEffect(PotionEffectType.SLOW);

            if (player.getInventory().getHelmet() != null) {
                ItemStack helmet = player.getInventory().getHelmet();
                packet = new PacketPlayOutEntityEquipment(player.getEntityId(), 4, CraftItemStack.asNMSCopy(new ItemStack(helmet)));

                for (Player online : PlayerUtility.getOnlinePlayers()) {
                    if (online != player) {
                        ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
                    }
                }
            } else {
                for (Player online : PlayerUtility.getOnlinePlayers()) {
                    if (online != player) {
                        online.hidePlayer(player);
                        online.showPlayer(player);
                    }
                }
            }
        }

        player = null;

        if (p != null) {
            player = p;

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (active) {
                        if (player != null) {
                            PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(getCarrier().getEntityId(), 4, CraftItemStack.asNMSCopy(new ItemStack(itemstack)));

                            if (!player.hasPotionEffect(PotionEffectType.SLOW)) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 0));
                            }

                            for (Player online : PlayerUtility.getOnlinePlayers()) {
                                if (online != getCarrier()) {
                                    ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
                                }
                            }
                        }
                    }
                }
            }.runTaskTimerAsynchronously(main, 2L, 2L);
        }
    }

    private void shootFirework(Location location) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        Random r = new Random();
        int rt = r.nextInt(4) + 1;
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        if (rt == 1) type = FireworkEffect.Type.BALL;
        if (rt == 2) type = FireworkEffect.Type.BALL_LARGE;
        if (rt == 3) type = FireworkEffect.Type.BURST;
        if (rt == 4) type = FireworkEffect.Type.CREEPER;
        if (rt == 5) type = FireworkEffect.Type.STAR;
        int r1i = r.nextInt(17) + 1;
        int r2i = r.nextInt(17) + 1;
        Color c1 = getFWColor(r1i);
        Color c2 = getFWColor(r2i);
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
        fwm.addEffect(effect);
        fwm.setPower(1);
        fw.setFireworkMeta(fwm);
    }

    protected static Object getHandle(org.bukkit.entity.Entity entity) {
        Object handle = null;
        try {
            Method handleMethod = entity.getClass().getMethod("getHandle");
            handle = handleMethod.invoke(entity);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return handle;
    }

    public static void setInvulnerable(Entity entity) {
        try {
            Object handle = getHandle(entity);
            Field invulnerableField = entity.getClass().getDeclaredField("invulnerable");
            invulnerableField.setAccessible(true);
            invulnerableField.set(handle, true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void checkFirework() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            if (!(tied) && isActive()) {
                if (item != null) {
                    shootFirework(item.getLocation());
                } else {
                    if (getCarrier() != null) {
                        shootFirework(getCarrier().getLocation());
                    }
                }
            }
        }, 20 * 10L, 20 * 10L);
    }

    public boolean isTied() {
        return tied;
    }

    public void setTied(boolean tied) {
        this.tied = tied;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Player getTying() {
        return tying;
    }

    public String getID() {
        return id;
    }

    public void setTying(Player tying) {
        this.tying = tying;
    }

    public Player getCarrier() {
        return player;
    }

    public Location getLocation() {
        return loc;
    }

    public void setLocation(Location loc) {
        this.loc = loc;

    }

    private Color getFWColor(int i) {
        Color c = null;
        if(i==1){
            c=Color.AQUA;
        }
        if(i==2){
            c=Color.BLACK;
        }
        if(i==3){
            c=Color.BLUE;
        }
        if(i==4){
            c=Color.FUCHSIA;
        }
        if(i==5){
            c=Color.GRAY;
        }
        if(i==6){
            c=Color.GREEN;
        }
        if(i==7){
            c=Color.LIME;
        }
        if(i==8){
            c=Color.MAROON;
        }
        if(i==9){
            c=Color.NAVY;
        }
        if(i==10){
            c=Color.OLIVE;
        }
        if(i==11){
            c=Color.ORANGE;
        }
        if(i==12){
            c=Color.PURPLE;
        }
        if(i==13){
            c=Color.RED;
        }
        if(i==14){
            c = Color.SILVER;
        }
        if(i==15){
            c = Color.TEAL;
        }
        if(i==16){
            c = Color.WHITE;
        }
        if (i == 17) {
            c = Color.YELLOW;
        }

        return c;
    }

    public Short getColorData(ChatColor color) {
        if (color == ChatColor.RED){
            return (short) 14;
        }

        if (color == ChatColor.GREEN){
            return (short) 5;
        }

        if (color == ChatColor.BLUE){
            return (short) 11;
        }

        if (color == ChatColor.YELLOW){
            return (short) 4;
        }

        return (short) 0;
    }


    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        this.item.setPickupDelay(Integer.MAX_VALUE);
        ItemMeta meta = item.getItemStack().getItemMeta();
        meta.setDisplayName(UUID.randomUUID().toString());
        item.getItemStack().setItemMeta(meta);
    }

    public ItemStack getItemstack() {
        return itemstack;
    }

    public void setItemstack(ItemStack itemstack) {
        this.itemstack = itemstack;
    }

    public void setAngelic(boolean angelic) {
        this.angelic = angelic;
    }

    public boolean isAngelic() {
        return angelic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCounting() {
        return counting;
    }

    public void sendBack(Entity entity, double speed) {
        Vector unitVector = entity.getLocation().toVector().subtract(loc.toVector()).normalize();
        entity.setVelocity(unitVector.multiply(speed));
    }

    public void setLastDroppedLocation(Location lastDroppedLocation) {
        this.lastDroppedLocation = lastDroppedLocation;
    }

    public void setCounting(boolean counting) {
        this.counting = counting;
    }
}
