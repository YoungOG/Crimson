package com.breakmc.crimson;


import com.breakmc.crimson.commands.BaseCommand;
import com.breakmc.crimson.listeners.CTFListeners;
import com.breakmc.crimson.region.Region;
import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.MongoClient;
import com.breakmc.crimson.managers.CTFManager;
import com.breakmc.crimson.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.UnknownHostException;
import java.util.logging.Level;

/**
 * Created by Alex on 8/24/2015.
 */
public class Crimson extends JavaPlugin {

    private static Crimson instance;
    private DB database;
    private CTFManager ctfManager;
    private MessageManager messageManager;

    public void onEnable() {
        instance = this;

        if (Bukkit.getPluginManager().getPlugin("Legacy") == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Crimson has been disabled, Legacy not found.");
        }

        setupDatabase();

        ctfManager = new CTFManager();
        messageManager = new MessageManager();
        ctfManager.loadCTFS();
        Region.loadAll();

        new BaseCommand();
        new CTFListeners();
    }

    public DB getDB() {
        return database;
    }

    public void onDisable() {
        ctfManager.removeFlags();
        ctfManager.checkAll();
        ctfManager.saveCTFS();
    }

    public void setupDatabase() {
        try {
            this.database = MongoClient.connect(new DBAddress(getConfig().getString("database.host"), getConfig().getString("database.database-name")));
            getLogger().log(Level.INFO, "Sucessfully connected to MongoDB.");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            getLogger().log(Level.INFO, "Failed to connect to MongoDB.");
        }
    }

    public static Crimson getInstance() {
        return instance;
    }

    public CTFManager getCtfManager() {
        return ctfManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }
}
