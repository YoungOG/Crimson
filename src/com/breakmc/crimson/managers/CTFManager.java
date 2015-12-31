package com.breakmc.crimson.managers;

import com.breakmc.crimson.Crimson;
import com.breakmc.crimson.objects.CTF;
import com.breakmc.crimson.region.Region;
import com.breakmc.crimson.region.Selection;
import com.breakmc.crimson.utils.LocationSerialization;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;

public class CTFManager {

    private Crimson main = Crimson.getInstance();
    private HashSet<CTF> ctfs = new HashSet<>();
    private ArrayList<Region> regions = new ArrayList<>();
    private DBCollection cCollection = main.getDB().getCollection("ctfs");

    public void loadCTFS() {
        DBCursor dbc = cCollection.find();
        Bukkit.getLogger().log(Level.INFO, "Preparing to load " + dbc.count() + " ctfs.");

        while (dbc.hasNext()) {
            BasicDBObject dbo = (BasicDBObject) dbc.next();

            CTF ctf = new CTF(dbo.getString("id"));

            if (dbo.getString("itemstack") != null) {
                ctf.setItemstack(new ItemStack(Material.getMaterial(dbo.getString("itemstack"))));
            }

            if (dbo.getString("location") != null) {
                ctf.setLocation(LocationSerialization.deserializeLocation(dbo.getString("location")));
            }

            if (dbo.getString("data") != null) {
                ctf.getItemstack().setDurability(Short.valueOf(dbo.getString("data")));
            }

            if (dbo.getString("color") != null) {
                ctf.setColor(ChatColor.valueOf(dbo.getString("color")));
            }

            if (dbo.getString("angelic") != null) {
                ctf.setAngelic(dbo.getBoolean("angelic"));
            }

            ctfs.add(ctf);
        }
    }

    public void saveCTFS() {
        Bukkit.getLogger().log(Level.INFO, "Preparing to save " + getCTFS().size() + " ctfs.");

        for (CTF ctf : getCTFS()) {
            DBCursor dbc = cCollection.find(new BasicDBObject("id", ctf.getID()));
            BasicDBObject dbo = new BasicDBObject("id", ctf.getID());

            if (ctf.getLocation() != null) {
                dbo.put("location", LocationSerialization.serializeLocation(ctf.getLocation()));
            }

            if (ctf.getItemstack() != null) {
                dbo.put("itemstack", ctf.getItemstack().getType().toString());
                dbo.put("data", String.valueOf(ctf.getItemstack().getDurability()));
            }

            dbo.put("color", ctf.getColor().name());
            dbo.put("angelic", ctf.isAngelic());

            if (dbc.hasNext()) {
                cCollection.update(dbc.getQuery(), dbo);
            } else {
                cCollection.insert(dbo);
            }
        }
    }

    public void removeFlags() {
        for (CTF ctf : getCTFS()) {
            if (ctf.getItem() != null) {
                ctf.getItem().remove();
            }
        }
    }

    public void checkAll() {
        for (CTF ctf : getCTFS()) {
            if (ctf.isActive()) {
                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "The CTF " + ctf.getColor() + ctf.getID() + ChatColor.DARK_AQUA + " has been cancelled due to a reload/restart.");
                ctf.setCarrier(null);
            }
        }
    }

    public void deleteCTF(CTF ctf) {
        DBCursor dbc = cCollection.find(new BasicDBObject("id", ctf.getID()));

        while (dbc.hasNext()) {
            cCollection.remove(dbc.next());
        }

        ctfs.remove(ctf);
    }

    public CTF getCTF(String ID) {
        for (CTF ctf : getCTFS()) {
            if (ctf.getID().equalsIgnoreCase(ID)) {
                return ctf;
            }
        }
        return null;
    }

    public HashSet<CTF> getCTFS() {
        return ctfs;
    }

    public ArrayList<Region> getRegions() {
        return regions;
    }

    public Region getRegion(CTF ctf) {
        for (Region region : regions) {
            if (region.getName().equalsIgnoreCase(ctf.getID())) {
                return region;
            }
        }

        return null;
    }
}
