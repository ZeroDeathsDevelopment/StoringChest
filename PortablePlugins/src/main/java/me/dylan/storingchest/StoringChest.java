package me.dylan.storingchest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public final class StoringChest extends JavaPlugin {

    private Connection connection;
    public String host, database, username, password, table;
    public int port;


    @Override
    public void onEnable() {

        getConfig().options().copyDefaults();
        saveDefaultConfig();


        host = getConfig().getString("host");
        database = getConfig().getString("database");
        username = getConfig().getString("database");
        password = getConfig().getString("password");
        table = getConfig().getString("table");
        port = getConfig().getInt("port");
        mySqlSetup();
    }



    public void mySqlSetup() {
        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                setConnection(
                        DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database,
                                this.username, this.password));
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MYSQL CONNECTED");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    public Connection getConnection(){
        return connection;
    }

    public void setConnection(Connection connection){
        this.connection = connection;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
