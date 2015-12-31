package com.breakmc.crimson.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static String fromSpecialCharacters(String txt) {
        if (txt != null) {
            txt = txt.replace("§", "&");
            txt = txt.replace("\u00e9", "[*]-[e]-[*]");
            txt = txt.replace("\u00e8", "[*]-(e)-[*]");
            txt = txt.replace("\u00ea", "[*]-|e|-[*]");
            txt = txt.replace("\u00e0", "[*]-[a]-[*]");
            txt = txt.replace("\u00e2", "[*]-|a|-[*]");
            txt = txt.replace("@", "[*]-{a}-[*]");
            txt = txt.replace("\u00e7", "[*]-[c]-[*]");
            txt = txt.replace("\u00f9", "[*]-(u)-[*]");
            txt = txt.replace("\u00fb", "[*]-|u|-[*]");
            txt = txt.replace("\u00ec", "[*]-(i)-[*]");
            txt = txt.replace("\u00ee", "[*]-|i|-[*]");
            txt = txt.replace("\u00f4", "[*]-|o|-[*]");
            txt = txt.replace("\u00f2", "[*]-(o)-[*]");
            txt = txt.replace("§", "[*]-(s)-[*]");
            txt = txt.replace("\u20ac", "[*]-(euro)-[*]");
            txt = txt.replace("£", "[*]-(livre)-[*]");
            txt = txt.replace("$", "[*]-(dollar)-[*]");
            txt = txt.replace("²", "[*]-(2)-[*]");
            return txt;
        }
        return null;
    }

    public static List<String> fromSpecialCharacters(final List<String> txts) {
        final List<String> list = new ArrayList<String>();
        for (final String txt : txts) {
            list.add(fromSpecialCharacters(txt));
        }
        return list;
    }

    public static String toSpecialCharacters(String txt) {
        txt = toColor(txt);
        txt = txt.replace("[*]-(e)-[*]", "\u00e8");
        txt = txt.replace("[*]-[e]-[*]", "\u00e9");
        txt = txt.replace("[*]-|e|-[*]", "\u00ea");
        txt = txt.replace("[*]-[a]-[*]", "\u00e0");
        txt = txt.replace("[*]-|a|-[*]", "\u00e2");
        txt = txt.replace("[*]-{a}-[*]", "@");
        txt = txt.replace("[*]-[c]-[*]", "\u00e7");
        txt = txt.replace("[*]-(u)-[*]", "\u00f9");
        txt = txt.replace("[*]-|u|-[*]", "\u00fb");
        txt = txt.replace("[*]-(i)-[*]", "\u00ec");
        txt = txt.replace("[*]-|i|-[*]", "\u00ee");
        txt = txt.replace("[*]-|o|-[*]", "\u00f4");
        txt = txt.replace("[*]-(o)-[*]", "\u00f2");
        txt = txt.replace("[*]-(s)-[*]", "§");
        txt = txt.replace("[*]-(euro)-[*]", "\u20ac");
        txt = txt.replace("[*]-(livre)-[*]", "£");
        txt = txt.replace("[*]-(dollar)-[*]", "$");
        txt = txt.replace("[*]-(2)-[*]", "²");
        return txt;
    }

    public static List<String> toSpecialCharacters(final List<String> txts) {
        final List<String> list = new ArrayList<String>();
        for (final String txt : txts) {
            list.add(toSpecialCharacters(txt));
        }
        return list;
    }

    public static String toColor(String txt) {
        txt = txt.replace("&0", "§0");
        txt = txt.replace("&1", "§1");
        txt = txt.replace("&2", "§2");
        txt = txt.replace("&3", "§3");
        txt = txt.replace("&4", "§4");
        txt = txt.replace("&5", "§5");
        txt = txt.replace("&6", "§6");
        txt = txt.replace("&7", "§7");
        txt = txt.replace("&8", "§8");
        txt = txt.replace("&9", "§9");
        txt = txt.replace("&a", "§a");
        txt = txt.replace("&b", "§b");
        txt = txt.replace("&c", "§c");
        txt = txt.replace("&d", "§d");
        txt = txt.replace("&e", "§e");
        txt = txt.replace("&f", "§f");
        txt = txt.replace("&k", "§k");
        txt = txt.replace("&o", "§o");
        txt = txt.replace("&m", "§m");
        txt = txt.replace("&n", "§n");
        txt = txt.replace("&l", "§l");
        txt = txt.replace("&r", "§r");
        return txt;
    }

    public static String fromColor(String txt) {
        txt = txt.replace("§0", "&0");
        txt = txt.replace("§1", "&1");
        txt = txt.replace("§2", "&2");
        txt = txt.replace("§3", "&3");
        txt = txt.replace("§4", "&4");
        txt = txt.replace("§5", "&5");
        txt = txt.replace("§6", "&6");
        txt = txt.replace("§7", "&7");
        txt = txt.replace("§8", "&8");
        txt = txt.replace("§9", "&9");
        txt = txt.replace("§a", "&a");
        txt = txt.replace("§b", "&b");
        txt = txt.replace("§c", "&c");
        txt = txt.replace("§d", "&d");
        txt = txt.replace("§e", "&e");
        txt = txt.replace("§f", "&f");
        txt = txt.replace("§k", "&k");
        txt = txt.replace("§o", "&o");
        txt = txt.replace("§m", "&m");
        txt = txt.replace("§n", "&n");
        txt = txt.replace("§l", "&l");
        txt = txt.replace("§r", "&r");
        return txt;
    }

    public static String removeColor(String txt) {
        txt = txt.replace("§0", "");
        txt = txt.replace("§1", "");
        txt = txt.replace("§2", "");
        txt = txt.replace("§3", "");
        txt = txt.replace("§4", "");
        txt = txt.replace("§5", "");
        txt = txt.replace("§6", "");
        txt = txt.replace("§7", "");
        txt = txt.replace("§8", "");
        txt = txt.replace("§9", "");
        txt = txt.replace("§a", "");
        txt = txt.replace("§b", "");
        txt = txt.replace("§c", "");
        txt = txt.replace("§d", "");
        txt = txt.replace("§e", "");
        txt = txt.replace("§f", "");
        txt = txt.replace("§k", "");
        txt = txt.replace("§o", "");
        txt = txt.replace("§m", "");
        txt = txt.replace("§n", "");
        txt = txt.replace("§l", "");
        txt = txt.replace("§r", "");
        return txt;
    }

    public static int min(final int a, final int b) {
        if (a < b) {
            return a;
        }
        return b;
    }

    public static int max(final int a, final int b) {
        if (a > b) {
            return a;
        }
        return b;
    }

    public static Location toLocation(final String txt) {
        final String[] split = txt.split(",");
        Location location = null;
        if (split.length == 4) {
            final String worldname = split[0];
            final String xname = split[1];
            final String yname = split[2];
            final String zname = split[3];
            try {
                final World world = Bukkit.getWorld(worldname);
                final double x = Double.parseDouble(xname);
                final double y = Double.parseDouble(yname);
                final double z = Double.parseDouble(zname);

                if (world == null) {
                    return null;
                }

                location = new Location(world, x, y, z);

                return location;
            }
            catch (Exception e) {
                return null;
            }
        }
        if (split.length == 6) {
            final String worldname = split[0];
            final String xname = split[1];
            final String yname = split[2];
            final String zname = split[3];
            final String yawname = split[4];
            final String pitchname = split[5];
            try {
                final World world2 = Bukkit.getWorld(worldname);
                final double x2 = Double.parseDouble(xname);
                final double y2 = Double.parseDouble(yname);
                final double z2 = Double.parseDouble(zname);
                final float yaw = Float.parseFloat(yawname);
                final float pitch = Float.parseFloat(pitchname);
                if (world2 != null) {
                    location = new Location(world2, x2, y2, z2, yaw, pitch);
                }
            }
            catch (Exception e2) {
                return null;
            }
        }
        return location;
    }

    public static String fromLocation(final Location location) {
        if (location != null) {
            return String.valueOf(location.getWorld().getName()) + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
        }
        return null;
    }

    public static String format(final String txt) {
        return txt.replace("\\n", System.getProperty("line.separator").substring(1)).replace("\\N", System.getProperty("line.separator").substring(1));
    }

    public static boolean hasPermission(final CommandSender player, final boolean msg, final String... perms) {
        if (!(player instanceof Player)) {
            return true;
        }
        String permission = "oprot";
        int i = 1;
        if (player.hasPermission("*") || player.hasPermission("*") || player.isOp()) {
            return true;
        }
        for (final String p : perms) {
            if (player.hasPermission(String.valueOf(permission) + ".*") || (player.hasPermission(String.valueOf(permission) + "." + p) && perms.length == i)) {
                return true;
            }
            permission = String.valueOf(permission) + "." + p;
            ++i;
        }
        if (msg) {
            player.sendMessage("§cYou do not have permission.");
        }
        return false;
    }

    public static Location retrieveLocation(final FileConfiguration fileConf, final String path) {
        return fileConf.getVector(String.valueOf(path) + ".coords").toLocation(Bukkit.getWorld(fileConf.getString(String.valueOf(path) + ".world")));
    }

    public static void writeLocation(final FileConfiguration fileConf, final String path, final Location location) {
        fileConf.set(String.valueOf(path) + ".coords", location.toVector());
        fileConf.set(String.valueOf(path) + ".world", location.getWorld().getName());
    }
}
