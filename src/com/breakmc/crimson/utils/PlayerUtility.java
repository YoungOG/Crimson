package com.breakmc.crimson.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtility {

    public static double getHealth(Player p) {
        return p.getHealth();
    }

    public static Player[] getOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    public static boolean hasInventorySpace(Inventory inventory, org.bukkit.inventory.ItemStack is) {
        Inventory inv = Bukkit.createInventory(null, inventory.getSize());

        for (int i = 0; i < inv.getSize(); i++) {
            if (inventory.getItem(i) != null) {
                org.bukkit.inventory.ItemStack item = inventory.getItem(i).clone();
                inv.setItem(i, item);
            }
        }

        return inv.addItem(new org.bukkit.inventory.ItemStack[]{is.clone()}).size() <= 0;
    }

    public static List<String> toList(Player[] array) {
        List<String> list = new ArrayList<>();
        for (Player t : array) {
            list.add(t.getName());
        }
        return list;
    }
}
