package org.plugin;
import org.plugin.Commands.*;
import org.plugin.Handlers.*;

import org.bukkit.Bukkit;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.io.File;

public class RatCusReputation extends JavaPlugin {
    public static Connection connection;
    public static Plugin plugin;

    public void onEnable() {
        // Создание конфига и копирование
        File config = new File(getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }

        // Подключение к базе данных и создание файла, если его нет
        String url = "jdbc:sqlite:./plugins/" + this.getName() + "/reputation.db";
        Connection conn = null;
        try {
            String sql = "CREATE TABLE IF NOT EXISTS reputation (\n" +
                    "nick TEXT NOT NULL,\n" +
                    "uuid TEXT NOT NULL,\n" +
                    "reputation LONG NOT NULL,\n" +
                    "lastLike LONG NOT NULL,\n" +
                    "lastDislike LONG NOT NULL\n" +
                    ");";

            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            getLogger().info(e.getMessage());
        }

        // Инициализация ивентов
        Bukkit.getPluginManager().registerEvents(new onPlayerJoin(), this);

        // Инициализация команд
        AdminReputation admrep = new AdminReputation();
        LikeCommands.Like like = new LikeCommands().new Like();
        LikeCommands.Dislike dislike = new LikeCommands().new Dislike();
        Reputation rep = new Reputation();

        getCommand("reputation").setExecutor(rep);
        getCommand("reputation").setTabCompleter(rep);

        getCommand("repadmin").setExecutor(admrep);
        getCommand("repadmin").setTabCompleter(admrep);

        getCommand("like").setExecutor(like);
        getCommand("like").setTabCompleter(like);

        getCommand("dislike").setExecutor(dislike);
        getCommand("dislike").setTabCompleter(dislike);

        connection = conn;
        plugin = this;
    }
}
