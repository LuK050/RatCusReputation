package org.plugin.Commands;
import org.plugin.RatCusReputation;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.*;
import java.lang.*;

public class Reputation implements CommandExecutor, TabCompleter {

    @Override public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player author = Bukkit.getPlayer(commandSender.getName());
        assert author != null;

        try {
            if (args.length == 0) {
                author.sendMessage("§c| Укажите аргумент");
                return false;
            } else {
                if (args[0].equals("top")) {
                    reputationTop(author);
                } else if (args[0].equals("get")) {
                    if (args.length == 1) senderReputation(author);
                    else playerReputation(args[1], author);
                } else if (args[0].equals("help")) {
                    help(author);
                } else {
                    author.sendMessage("§c| Укажите корректный аргумент");
                    return false;
                }
            }
            return true;
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
                        arguments.add("top");
                        arguments.add("get");
                        arguments.add("help");
                    }
                    case 2 -> {
                        if (args[0].equals("get")) {
                            ResultSet rs = RatCusReputation.connection.createStatement().executeQuery("SELECT * FROM reputation");
                            while (rs.next()) {
                                arguments.add(rs.getString("nick"));
                            }
                            arguments.add("");
                            rs.close();
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

    public static void senderReputation(Player player) throws SQLException {
        Statement stmt = RatCusReputation.connection.createStatement();
        String sql = "SELECT reputation FROM reputation WHERE uuid = " + '"' + player.getUniqueId() + '"';
        ResultSet rs = stmt.executeQuery(sql);

        long reputation = rs.getLong("reputation");

        player.sendMessage(String.format("§6| У вас %s очков репутации", reputation));
        rs.close(); stmt.close();
    }

    public static void playerReputation(String playerName, Player sender) throws SQLException {
        Statement stmt = RatCusReputation.connection.createStatement();
        try {
            String sql = "SELECT reputation FROM reputation WHERE nick = " + '"' + playerName + '"';
            ResultSet rs = stmt.executeQuery(sql);
            long reputation = rs.getLong("reputation");

            sender.sendMessage(String.format("§6| У игрока §e%s§6 %s очков репутации", playerName, reputation));
            rs.close(); stmt.close();
        } catch (SQLException e) {
            sender.sendMessage("§c| Указаного игрока нет в базе данных. Проверьте правильность написания");
            stmt.close();
        }
    }

    public static void reputationTop(Player player) throws SQLException {
        Plugin plugin = RatCusReputation.plugin;
        Statement stmt = RatCusReputation.connection.createStatement();
        ResultSet rs = RatCusReputation.connection.createStatement().executeQuery("SELECT * FROM reputation ORDER BY reputation DESC");
        StringBuilder top = new StringBuilder();
        int counter = 0;

        top.append("§6|----------------Топ репутации----------------\n");
        while (rs.next()) {
            counter++;
            top.append(String.format("§6| %s. §e%s§6 - %s\n", counter, rs.getString("nick"), rs.getLong("reputation")));

            if (counter == plugin.getConfig().getLong("topPlayerCount")) {
                break;
            }
        }
        top.append("\n");

        player.sendMessage(top.toString());
        rs.close(); stmt.close();
    }

    public static void help(Player player) {
        player.sendMessage("§6|-------------------§eRatCusHelp§6------------------------\n" +
                            "§6| /rep get §e[player/none]§6 - вывод своей/репутации игрока\n" +
                            "§6| /rep top - вывод топа игроков по репутации\n" +
                            "§6| /like §e[player]§6 - дать игроку +5 репутации (раз в сутки)\n" +
                            "§6| /dislike §e[player]§6 - снять с игрока -5 репутации (раз в сутки)\n"
                            );
    }
}
