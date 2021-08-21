package org.plugin.Handlers;
import org.plugin.RatCusReputation;

import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.*;

import java.sql.*;
import java.lang.*;
import java.util.Date;

public class onPlayerJoin implements Listener {

    @EventHandler public static void onPlayerServerJoin(PlayerJoinEvent event) throws SQLException {
        Date date = new Date();
        Player player = event.getPlayer();
        Connection connection = RatCusReputation.connection;

        Statement stmt = connection.createStatement();
        try {
            String sql = "SELECT nick FROM reputation WHERE uuid = " + '"' + player.getUniqueId() + '"';
            ResultSet rs = stmt.executeQuery(sql);
            String nick = rs.getString("nick");
            if (!nick.equals(player.getDisplayName()))
                sql = "UPDATE reputation SET nick = " + '"' + player.getDisplayName() + '"' + " WHERE uuid = " + '"' + player.getUniqueId() + '"';

            rs.close(); stmt.close();
        } catch (SQLException e) {
            String sql = "INSERT INTO reputation (nick, uuid, reputation, lastLike, lastDislike) VALUES (" + '"' + player.getDisplayName() + '"' + ", " + '"' + player.getUniqueId() + '"' + ", " + "500" + ", " + date.getTime() + ", " + date.getTime() + ")";
            stmt.execute(sql);
            stmt.close();
        }
    }
}
