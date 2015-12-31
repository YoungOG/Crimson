package com.breakmc.crimson.region;

import org.bukkit.Location;

public class Selection {

    public Location location1;
    public Location location2;

    public Selection(final Location loc1, final Location loc2) {
        this.location1 = loc1;
        this.location2 = loc2;
    }

    public Location getLocation1() {
        return this.location1;
    }

    public void setLocation1(final Location location1) {
        this.location1 = location1;
    }

    public Location getLocation2() {
        return this.location2;
    }

    public void setLocation2(final Location location2) {
        this.location2 = location2;
    }
}
