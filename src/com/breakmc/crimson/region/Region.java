package com.breakmc.crimson.region;


import com.breakmc.crimson.Crimson;
import com.breakmc.crimson.managers.CTFManager;
import com.breakmc.crimson.utils.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class Region {

    private CTFManager ctfManager = Crimson.getInstance().getCtfManager();
    private Selection selection;
    private String name;

    public Region(final String name) {
        this.selection = new Selection(null, null);
        this.name = name;
        this.load();
        ctfManager.getRegions().add(this);
    }

    public String getName() {
        return this.name;
    }

    public static void loadAll() {
        FileConfiguration rfc = Crimson.getInstance().getConfig();

        if (rfc.contains("regions")) {
            for (final String regionName : rfc.getConfigurationSection("regions.").getKeys(false)) {
                new Region(regionName);
            }
        }
    }

    public void load() {
        FileConfiguration rfc = Crimson.getInstance().getConfig();
        Selection selection = new Selection(null, null);

        if (rfc.contains("regions." + this.name)) {
            selection.setLocation1(Utils.retrieveLocation(rfc, "regions." + this.name + ".1"));
            selection.setLocation2(Utils.retrieveLocation(rfc, "regions." + this.name + ".2"));
            this.setSelection(selection);
        }
    }

    public Selection getSelection() {
        return this.selection;
    }

    public void setSelection(Selection selection) {
        this.selection = selection;
        this.updateSelection();
    }

    public void updateSelection() {
        FileConfiguration rfc = Crimson.getInstance().getConfig();

        if (this.getSelection() != null) {
            if (this.getSelection().getLocation1() != null) {
                Utils.writeLocation(rfc, "regions." + this.name + ".1", this.getSelection().getLocation1());
            }
            if (this.getSelection().getLocation2() != null) {
                Utils.writeLocation(rfc, "regions." + this.name + ".2", this.getSelection().getLocation2());
            }

            Crimson.getInstance().saveConfig();
        }
    }

    public static Region get(String name) {
        for (final Region r : Crimson.getInstance().getCtfManager().getRegions()) {
            if (r.name.equalsIgnoreCase(name)) {
                return r;
            }
        }

        return null;
    }

    public static Region get(Location location) {
        for (Region r : Crimson.getInstance().getCtfManager().getRegions()) {
            if (r.getSelection() != null && r.getSelection().getLocation1() != null && r.getSelection().getLocation2() != null && location.getWorld() == r.getSelection().getLocation1().getWorld()) {
                int minX = Utils.min(r.getSelection().getLocation1().getBlockX(), r.getSelection().getLocation2().getBlockX());
                int minY = Utils.min(r.getSelection().getLocation1().getBlockY(), r.getSelection().getLocation2().getBlockY());
                int minZ = Utils.min(r.getSelection().getLocation1().getBlockZ(), r.getSelection().getLocation2().getBlockZ());
                int maxX = Utils.max(r.getSelection().getLocation1().getBlockX(), r.getSelection().getLocation2().getBlockX());
                int maxY = Utils.max(r.getSelection().getLocation1().getBlockY(), r.getSelection().getLocation2().getBlockY());
                int maxZ = Utils.max(r.getSelection().getLocation1().getBlockZ(), r.getSelection().getLocation2().getBlockZ());

                if (minX <= location.getBlockX() && location.getBlockX() <= maxX && minY <= location.getBlockY() && location.getBlockY() <= maxY && minZ <= location.getBlockZ() && location.getBlockZ() <= maxZ) {
                    return r;
                }
            }
        }

        return null;
    }
}
