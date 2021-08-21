package org.plugin.Commands;
import org.plugin.RatCusReputation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.*;

import java.sql.*;
import java.util.*;
import java.lang.*;


public class AdminReputation implements CommandExecutor, TabCompleter {

    @Override public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player author = Bukkit.getPlayer(commandSender.getName());
        assert author != null;

        try {
            if (args.length == 0) {
                author.sendMessage("§c| Укажите аргумент");
                return false;
            } else {
                if (args[0].equals("give") || args[0].equals("set")) {
                    if (args.length > 2) {
                        try {
                            if (args[0].equals("give")) give(author, args[1], Long.parseLong(args[2]));
                            else if (args[0].equals("set")) set(author, args[1], Long.parseLong(args[2]));
                        } catch (NumberFormatException e) {
                            author.sendMessage("§c| Укажите корректное кол-во очков репутации");
                            return false;
                        }
                        return true;
                    } else if (args.length == 1) {
                        author.sendMessage("§c| Укажите игрока");
                    } else {
                        author.sendMessage("§c| Укажите кол-во очков репутации");
                    }
                    return false;
                } else if (args[0].equals("reload")) {
                    pluginReload(author);
                    return true;
                } else if (args[0].equals("help")) {
                    help(author);
                    return true;
                } else {
                    author.sendMessage("§c| Укажите аргумент");
                    return false;
                }
            }
        } catch (SQLException e) {
            RatCusReputation.plugin.getLogger().info(e.getMessage());
        }
        return false;
    }

    @Override public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            List<String> arguments = new ArrayList<>();

            try {
                switch (args.length) {
                    case 1 -> {
                        arguments.add("give");
                        arguments.add("set");
                        arguments.add("reload");
                        arguments.add("help");
                    }
                    case 2 -> {
                        if (args[0].equals("give") || args[0].equals("set")) {
                            ResultSet rs = RatCusReputation.connection.createStatement().executeQuery("SELECT * FROM reputation");
                            while (rs.next()) {
                                arguments.add(rs.getString("nick"));
                            }
                            arguments.add("");
                            rs.close();
                        }
                    }
                    case 3 -> {
                        if (args[0].equals("give") || args[0].equals("set")) {
                            arguments.add("5");
                            arguments.add("-5");
                        }
                    }
                }
            } catch (SQLException e) {
                RatCusReputation.plugin.getLogger().info(e.getMessage());
            }
            return arguments;
        }
        return null;
    }

    public static void give(Player sender, String playerName, long points) throws SQLException {
        Player author = Bukkit.getPlayer(sender.getName());

        assert author != null;
        Statement stmt = RatCusReputation.connection.createStatement();
        try {
            String sql = "UPDATE reputation SET reputation = reputation + " + points + " WHERE nick = " + '"' + playerName + '"';
            stmt.execute(sql);

            sql = "SELECT reputation FROM reputation WHERE nick = " + '"' + playerName + '"';
            ResultSet rs = stmt.executeQuery(sql);
            long reputation = rs.getLong("reputation");

            author.sendMessage(String.format("§6| Вы успешно выдали %s очков репутации игроку §e%s§6. Теперь у него §e%s§6 очков репутации", points, playerName, reputation));
            stmt.close();
        } catch (SQLException e) {
            author.sendMessage("§c| Указаного игрока нет в базе данных. Проверьте правильность написания");
            stmt.close();
        }
    }

    public static void set(Player sender, String playerName, long points) throws SQLException {
        Player author = Bukkit.getPlayer(sender.getName());

        assert author != null;
        Statement stmt = RatCusReputation.connection.createStatement();
        try {
            String sql = "UPDATE reputation SET reputation = " + points + " WHERE nick = " + '"' + playerName + '"';
            stmt.execute(sql);

            sql = "SELECT reputation FROM reputation WHERE nick = " + '"' + playerName + '"';
            ResultSet rs = stmt.executeQuery(sql);
            long reputation = rs.getLong("reputation");

            author.sendMessage(String.format("§6| Вы успешно установили кол-во очков репутации игроку §e%s§6 на §e%s§6.", playerName, reputation));
            stmt.close();
        } catch (SQLException e) {
            author.sendMessage("§c| Указаного игрока нет в базе данных. Проверьте правильность написания");
            stmt.close();
        }
    }

    public static void help(Player player) {
        player.sendMessage("§6|-------------------§eAdminRatCusHelp§6--------------------\n" +
                "§6| /repa give §e[player] [number/-number]§6 - выдача очков репутации игроку\n" +
                "§6| /repa set §e[player] [number/-number]§6 - установка кол-ва очков репутации у игрока\n" +
                "§6| /repa reload - перезагрузка плагина\n"
        );
    }

    public static void pluginReload(Player player) {
        player.sendMessage("§6| Перезагрузка...");
        Bukkit.reload(); RatCusReputation.plugin.reloadConfig();
        player.sendMessage(String.format("§6| Плагин §e%s§6 и конфиг успешно перезагружены!", RatCusReputation.plugin.getName()));
    }
}
