package com.breakmc.crimson.region;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class RegionUser {

    public static HashMap<String, RegionUser> map;
    private String name;
    private Selection selection;
    private boolean exceptTeleportation;

    static {
        RegionUser.map = new HashMap<>();
    }

    public RegionUser(final String name) {
        this.name = "";
        this.selection = new Selection(null, null);
        this.exceptTeleportation = false;
        this.name = name;
        RegionUser.map.put(name.toLowerCase(), this);
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isExceptTeleportation() {
        return this.exceptTeleportation;
    }

    public void setExceptTeleportation(final boolean exceptTeleportation) {
        this.exceptTeleportation = exceptTeleportation;
    }

    public Selection getSelection() {
        return this.selection;
    }

    public void setSelection(final Selection selection) {
        this.selection = selection;
    }

    public static RegionUser getRegionUser(final Player player) {
        return getRegionUser(player.getName());
    }

    public static RegionUser getRegionUser(final String name) {
        if (RegionUser.map.containsKey(name.toLowerCase())) {
            return RegionUser.map.get(name.toLowerCase());
        }

        return new RegionUser(name.toLowerCase());
    }
}
