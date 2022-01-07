package me.dylan.storingchest.listeners;

import me.dylan.storingchest.StoringChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.plaf.nimbus.State;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class SqlSetterGetter implements Listener {

    StoringChest plugin = StoringChest.getPlugin(StoringChest.class);
    HashMap<UUID, Integer> openedchests = new HashMap<>();
    public static int opened;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        createPlayer(player.getUniqueId());
        openedchests.put(e.getPlayer().getUniqueId(), 0);
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent e){
        if(e.getInventory().getType().equals(InventoryType.CHEST)){
            opened += 1;
            openedchests.put(e.getPlayer().getUniqueId(), openedchests.get(openedchests.get(e.getPlayer().getUniqueId()) + 1));

        }
    }

    public void save(UUID uuid){
        new BukkitRunnable(){
            @Override
            public void run(){
                try{
                    if(!playerExists(uuid)){
                        PreparedStatement statement = plugin.getConnection()
                                .prepareStatement("INSERT INTO " + plugin.table + " (UUID,CHESTS) VALUES (?,?)");
                        statement.setString(1, uuid.toString());
                        statement.setInt(2, openedchests.get(uuid));
                        openedchests.remove(uuid);
                        statement.executeUpdate();
                    }else{
                        PreparedStatement update = plugin.getConnection()
                                .prepareStatement("UPDATE STORINGCHEST SET CHESTS= CHESTS + ? WHERE UUID=?");
                        update.setInt(1, opened);
                        update.setString(2, uuid.toString());
                        openedchests.remove(uuid);
                        update.executeUpdate();
                        opened = 0;
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 2400);
    }

    public boolean playerExists(UUID uuid){
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());

            ResultSet results = statement.executeQuery();
            if(results.next()){
                return true;
            }

        }catch(SQLException e){
             e.printStackTrace();
        }
        return false;
    }

    public void createPlayer(final UUID uuid){
        try{
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            if(playerExists(uuid) == false){
                PreparedStatement insert = plugin.getConnection()
                        .prepareStatement("INSERT INTO " + plugin.table + " (UUID,CHESTS) VALUES (?,?)");

                insert.setString(1, uuid.toString());
                insert.setInt(2, 0);
                insert.executeUpdate();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}

