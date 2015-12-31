package com.breakmc.crimson.commands;

import com.breakmc.crimson.Crimson;
import com.breakmc.crimson.managers.CTFManager;
import com.breakmc.crimson.managers.MessageManager;
import com.breakmc.crimson.objects.CTF;
import com.breakmc.crimson.region.Region;
import com.breakmc.crimson.region.RegionUser;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

/**
 * Created by Alex on 8/24/2015.
 */
public class BaseCommand implements CommandExecutor {

    private Crimson main = Crimson.getInstance();
    private CTFManager cm = main.getCtfManager();
    private MessageManager mm = main.getMessageManager();

    public BaseCommand() {
        main.getCommand("ctf").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
        if (cmd.equalsIgnoreCase("ctf")) {

            if (!(sender.hasPermission("admin"))) {
                sender.sendMessage(RED + "You do not have permission to do this!");
                return false;
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {

                    if (cm.getCTFS().isEmpty()) {
                        sender.sendMessage(RED + "There are currently no defined CTFs.");
                        return false;
                    }

                    sender.sendMessage(mm.getCTFs());
                    return false;

                }
            }

            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("setcolor")) {
                    if (cm.getCTF(args[1]) == null) {
                        sender.sendMessage(RED + "A CTF with the name " + args[1] + " could not be found!");
                        return false;
                    }

                    if (!(mm.validColors().contains(args[2].toUpperCase()))) {
                        sender.sendMessage(RED + "Please specify a valid color!");
                        return false;
                    }

                    ChatColor color = valueOf(args[2].toUpperCase());
                    CTF ctf = cm.getCTF(args[1]);
                    sender.sendMessage(DARK_AQUA + "You have set the CTF named " + AQUA + ctf.getID() + DARK_AQUA + "'s color to " + color + color.name() + DARK_AQUA + ".");
                    ctf.setColor(color);
                    return false;
                }
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("setregion")) {
                    Player p = (Player) sender;
                    RegionUser user = RegionUser.getRegionUser(p);

                    Region region = new Region(args[1]);

                    if (p.hasPermission("crimson.wand")) {
                        if (user.getSelection() == null) {
                            p.sendMessage(ChatColor.RED + "You must select 2 points first.");
                            return false;
                        } else if (user.getSelection().getLocation1() == null || user.getSelection().getLocation2() == null) {
                            p.sendMessage(ChatColor.RED + "You must select 2 points first.");
                            return false;
                        } else if (user.getSelection().getLocation1().getWorld() != user.getSelection().getLocation2().getWorld()) {
                            p.sendMessage(ChatColor.RED + "Your selection must be in the same world.");
                            return false;
                        } else {
                            p.sendMessage(ChatColor.GREEN + "Successfully created a region.");
                            region.setSelection(user.getSelection());
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have permission!");
                        return false;
                    }
                }

                if (args[0].equalsIgnoreCase("setitem")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(RED + "You must be a player to set the item for a CTF");
                        return false;
                    }

                    Player p = (Player) sender;

                    if (cm.getCTF(args[1]) == null) {
                        p.sendMessage(RED + "A CTF with the name " + args[1] + " could not be found!");
                        return false;
                    }

                    CTF ctf = cm.getCTF(args[1]);

                    if (p.getItemInHand() == null) {
                        p.sendMessage(RED + "You do not have an item in your hand!");
                        return false;
                    } else {
                        ctf.setItemstack(p.getItemInHand());
                        p.sendMessage(DARK_AQUA + "You have set the CTF named " + AQUA + ctf.getID() + DARK_AQUA + "'s item to the item in your hand!");
                    }

                    return false;
                }

                if (args[0].equalsIgnoreCase("start")) {
                    if (cm.getCTF(args[1]) == null) {
                        sender.sendMessage(RED + "A CTF with the name " + args[1] + " could not be found!");
                        return false;
                    }

                    CTF ctf = cm.getCTF(args[1]);

                    if (ctf.isActive()) {
                        sender.sendMessage(RED + "That CTF is already active!");
                        return false;
                    }

                    if (ctf.isCounting()) {
                        sender.sendMessage(RED + "That CTF is on countdown!");
                        return false;
                    }

                    sender.sendMessage(DARK_AQUA + "You have started the countdown for the CTF named " + AQUA + ctf.getID() + DARK_AQUA + ".");
                    ctf.startCountdown(5, 0);

                    return false;
                }

                if (args[0].equalsIgnoreCase("angelic")) {
                    if (cm.getCTF(args[1]) == null) {
                        sender.sendMessage(RED + "A CTF with the name " + args[1] + " could not be found!");
                        return false;
                    }

                    CTF ctf = cm.getCTF(args[1]);

                    if (ctf.isAngelic()) {
                        sender.sendMessage(DARK_AQUA + "You have the the CTF named " + AQUA + ctf.getID() + DARK_AQUA + "'s angelic mode to " + AQUA + "false" + DARK_AQUA + ".");
                        ctf.setAngelic(false);
                    } else {
                        sender.sendMessage(DARK_AQUA + "You have the the CTF named " + AQUA + ctf.getID() + DARK_AQUA + "'s angelic mode to " + AQUA + "true" + DARK_AQUA + ".");
                        ctf.setAngelic(true);
                    }

                    return false;
                }

                if (args[0].equalsIgnoreCase("set")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(RED + "You must be a player to set a CTF location!");
                        return false;
                    }

                    Player p = (Player) sender;

                    if (cm.getCTF(args[1]) == null) {
                        sender.sendMessage(RED + "A CTF with the name " + args[1] + " could not be found!");
                        return false;
                    }

                    Location location = new Location(p.getLocation().getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ());
                    double x;
                    double z;

                    if (location.getBlockX() > 0) {
                        x = 0.5;
                    } else {
                        x = -0.5;
                    }

                    if (location.getBlockZ() > 0) {
                        z = 0.5;
                    } else {
                        z = -0.5;
                    }

                    cm.getCTF(args[1]).setLocation(location.add(x, 2, z));
                    p.sendMessage(DARK_AQUA + "You have set the CTF named " + AQUA + args[1] + DARK_AQUA + "'s location to" + GRAY + ": " + WHITE + "X: " + p.getLocation().getBlockX() + ", Y: " + p.getLocation().getBlockY() + ", Z: " + p.getLocation().getBlockZ() + DARK_AQUA + "!");
                    return false;
                }

                if (args[0].equalsIgnoreCase("create")) {
                    if (cm.getCTF(args[1]) != null) {
                        sender.sendMessage(RED + "A CTF with the name " + args[1] + " already exists!");
                        return false;
                    }

                    CTF ctf = new CTF(args[1]);
                    cm.getCTFS().add(ctf);
                    sender.sendMessage(DARK_AQUA + "You have created a new CTF named " + AQUA + args[1] + DARK_AQUA + "!");
                    return false;
                }

                if (args[0].equalsIgnoreCase("delete")) {
                    if (cm.getCTF(args[1]) == null) {
                        sender.sendMessage(RED + "A CTF with the name " + args[1] + " could not be found!");
                        return false;
                    }

                    cm.deleteCTF(cm.getCTF(args[1]));
                    sender.sendMessage(DARK_AQUA + "You have deleted the CTF named " + AQUA + args[1] + DARK_AQUA + "!");
                    return false;
                }

            }

            sender.sendMessage(mm.getHelp());
        }

        return false;
    }
}
