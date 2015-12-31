package com.breakmc.crimson.managers;

import code.breakmc.legacy.Legacy;
import code.breakmc.legacy.teams.Team;
import code.breakmc.legacy.teams.TeamManager;
import com.breakmc.crimson.Crimson;
import com.breakmc.crimson.objects.CTF;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Alex on 8/24/2015.
 */

public class MessageManager {

    private Crimson main = Crimson.getInstance();
    private CTFManager cm = main.getCtfManager();
    private Legacy legacy = Legacy.getInstance();
    private TeamManager tm = legacy.getTeamManager();

    public String addLine(String string, String anotherString) {
        String newstring = string + "\n" + anotherString;
        return ChatColor.translateAlternateColorCodes('&', newstring);
    }

    public String addTo(String string, String anotherString) {
        String newstring = string + " " + anotherString;
        return ChatColor.translateAlternateColorCodes('&', newstring);
    }
    public String newString(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }


    public String getCaptureMessage(Player player, CTF ctf) {
        if (tm.getTeam(player.getUniqueId()) == null) {
            return newString("&b" + player.getName() + " &3has successfully captured the " + ctf.getColor() + ctf.getID() + "&3 flag!");
        } else {
            Team team = tm.getTeam(player.getUniqueId());
            team.setValorPoints(team.getValorPoints() + 1);
            return newString("&b" + player.getName() + " &3(&b" + team.getName() + "&3) has sucessfully captured the " + ctf.getColor() + ctf.getID() + "&3 flag!");
        }
    }

    public String getPickupMessage(Player player, CTF ctf) {
        if (tm.getTeam(player.getUniqueId()) == null) {
            return newString("&b" + player.getName() + " &3has picked up the " + ctf.getColor() + ctf.getID() + "&3 flag!");
        } else {
            Team team = tm.getTeam(player.getUniqueId());
            return newString("&b" + player.getName() + " &3(&b" + team.getName() + "&3) has picked up the " + ctf.getColor() + ctf.getID() + "&3 flag!");
        }
    }


    public String getCTFStatus(CTF ctf) {
        String info;

        if (ctf.getLocation() == null) {
            if (ctf.isActive()) {
                info = newString(ctf.getColor() + ctf.getID()  + ChatColor.BOLD + " &r&b> &3Location: &fNot set&3, &3Status: &fActive&3." );
            } else {
                info = newString(ctf.getColor()  + ctf.getID()   + ChatColor.BOLD + " &r&b> &3Location: &fNot set&3, &3Status: &fInactive&3." );
            }
        } else {
            if (ctf.isActive()) {
                info = newString(ctf.getColor() + ctf.getID() + ChatColor.BOLD + " &r&b> &3Location: &f[" + ctf.getLocation().getBlockX() + ", " + ctf.getLocation().getBlockY() + ", " + ctf.getLocation().getBlockZ() +", " + ctf.getLocation().getWorld().getName() + "]&3, "  + "&3Status: &fActive&3." );
            } else {
                info = newString(ctf.getColor() + ctf.getID() + ChatColor.BOLD + " &r&b> &3Location: &f[" + ctf.getLocation().getBlockX() + ", " + ctf.getLocation().getBlockY() + ", " + ctf.getLocation().getBlockZ() +", " + ctf.getLocation().getWorld().getName() + "]&3, "  + "&3Status: &fInactive&3." );
            }
        }

        return info;
    }


    public ArrayList<String> validColors() {
        ArrayList<String> validcolors = new ArrayList<>();
        validcolors.add("GOLD");
        validcolors.add("WHITE");
        validcolors.add("YELLOW");
        validcolors.add("DARK_PURPLE");
        validcolors.add("LIGHT_PURPLE");
        validcolors.add("AQUA");
        validcolors.add("DARK_AQUA");
        validcolors.add("GREEN");
        validcolors.add("DARK_GREEN");
        validcolors.add("DARK_RED");
        validcolors.add("RED");
        validcolors.add("GRAY");
        validcolors.add("DARK_BLUE");
        validcolors.add("BLACK");
        return validcolors;
    }


    public String getCTFs() {
        String ctfs = newString("&8&m-----------------------------------------------------");
        ctfs = addLine(ctfs, "&3CTFs &f[" + cm.getCTFS().size() + "]");
        ctfs = addLine(ctfs, " ");

        for (CTF ctf : cm.getCTFS()) {
            ctfs = addLine(ctfs, getCTFStatus(ctf));
        }

        ctfs = addLine(ctfs, "&8&m-----------------------------------------------------");

        return ctfs;
    }

    public String getHelp() {
        String help = newString("&8&m-----------------------------------------------------");
        help = addLine(help, "&3CTF Help &f(Page 1/1)");
        help = addLine(help, "&b/ctf create (name) > &7Creates a CTF with the name specified");
        help = addLine(help, "&b/ctf delete (name) > &7Deletes the CTF specified");
        help = addLine(help, "&b/ctf set (name) > &7Sets the specified CTF flag to spawn where you're standing");
        help = addLine(help , "&b/ctf setcolor (name) (color) > &7Sets the specified CTF color to the specified color");
        help = addLine(help, "&b/ctf angelic (name) > &7Toggles angelic mode for a specified CTF");
        help = addLine(help, "&b/ctf list > &7Lists all CTF's and their status");
        help = addLine(help, "&8&m-----------------------------------------------------");
        return help;
    }



}
