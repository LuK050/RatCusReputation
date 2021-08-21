package org.plugin.Commands;
import org.plugin.RatCusReputation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.*;

import java.sql.*;
import java.util.*;
import java.lang.*;
import java.util.Date;

public class LikeCommands {

    public class Like implements CommandExecutor, TabCompleter {

        @Override public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
            Player author = Bukkit.getPlayer(commandSender.getName());
            Plugin plugin = RatCusReputation.plugin;
            long date = new Date().getTime();
            assert author != null;

            try {
                Statement stmt = RatCusReputation.connection.createStatement();
                if (args.length == 0) {
                    author.sendMessage("§c| Укажите игрока");
                    return false;
                } else {
                    if (author.getDisplayName().equals(args[0])) {
                        author.sendMessage("§c| Вы не можете выдавать очки репутации себе");
                        return false;
                    }
                    try {
                        String sql = "SELECT lastLike FROM reputation WHERE nick = " + '"' + author.getDisplayName() + '"';
                        ResultSet rs = stmt.executeQuery(sql);
                        if (rs.getLong("lastLike") <= date) {
                            sql = "UPDATE reputation SET reputation = reputation + " + plugin.getConfig().getLong("likeCountPoints") + " WHERE nick = " + '"' + args[0] + '"';
                            stmt.execute(sql);
                            sql = "UPDATE reputation SET lastLike = " + (date + (plugin.getConfig().getLong("likeDelay") * 60 * 60 * 1000)) + " WHERE nick = " + '"' + author.getDisplayName() + '"';
                            stmt.execute(sql);

                            author.sendMessage(String.format("§6| Вы успешно дали %s очков репутации игроку §e%s§6. Приходите снова через %s часа", plugin.getConfig().getLong("likeCountPoints"),  args[0], plugin.getConfig().getLong("likeDelay")));
                        } else {
                            long timeRemove = rs.getLong("lastLike");
                            author.sendMessage(String.format("§c| Вы уже отдали сегодня очки репутации. Приходите снова через %s часа", (long) Math.ceil(((timeRemove - date) / 1000 / 60 / 60))));
                        }
                        stmt.close();
                    } catch (SQLException e) {
                        author.sendMessage("§c| Указаного игрока нет в базе данных. Проверьте правильность написания");
                        stmt.close();
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
                            ResultSet rs = RatCusReputation.connection.createStatement().executeQuery("SELECT * FROM reputation");
                            while (rs.next()) {
                                if (rs.getString("nick").equals(commandSender.getName())) continue;
                                arguments.add(rs.getString("nick"));
                            }
                            rs.close();
                        }
                    }
                } catch (SQLException e) {
                    RatCusReputation.plugin.getLogger().info(e.getMessage());
                }
                return arguments;
            }
            return null;
        }
    }

    public class Dislike implements CommandExecutor, TabCompleter {

        @Override public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
            Player author = Bukkit.getPlayer(commandSender.getName());
            Plugin plugin = RatCusReputation.plugin;
            long date = new Date().getTime();
            assert author != null;

            try {
                Statement stmt = RatCusReputation.connection.createStatement();
                if (args.length == 0) {
                    author.sendMessage("§c| Укажите игрока");
                    return false;
                } else {
                    if (author.getDisplayName().equals(args[0])) {
                        author.sendMessage("§c| Вы не можете снимать очки репутации у себя");
                        return false;
                    }
                    try {
                        String sql = "SELECT lastDislike FROM reputation WHERE nick = " + '"' + author.getDisplayName() + '"';
                        ResultSet rs = stmt.executeQuery(sql);
                        if (rs.getLong("lastDislike") <= date) {
                            sql = "UPDATE reputation SET reputation = reputation - " + plugin.getConfig().getLong("likeCountPoints") + " WHERE nick = " + '"' + args[0] + '"';
                            stmt.execute(sql);
                            sql = "UPDATE reputation SET lastDislike = " + (date + (plugin.getConfig().getLong("likeDelay") * 60 * 60 * 1000)) + " WHERE nick = " + '"' + author.getDisplayName() + '"';
                            stmt.execute(sql);

                            author.sendMessage(String.format("§6| Вы успешно сняли %s очков репутации с игрока §e%s§6. Приходите снова через %s часа", plugin.getConfig().getLong("likeCountPoints"), args[0], plugin.getConfig().getLong("likeDelay")));
                        } else {
                            long timeRemove = rs.getLong("lastDislike");
                            author.sendMessage(String.format("§c| Вы уже снимали сегодня очки репутации. Приходите снова через %s часа", (long) Math.ceil(((timeRemove - date) / 1000 / 60 / 60))));
                        }
                        stmt.close();
                    } catch (SQLException e) {
                        author.sendMessage("§c| Указаного игрока нет в базе данных. Проверьте правильность написания");
                        stmt.close();
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
                            ResultSet rs = RatCusReputation.connection.createStatement().executeQuery("SELECT * FROM reputation");
                            while (rs.next()) {
                                if (rs.getString("nick").equals(commandSender.getName())) continue;
                                arguments.add(rs.getString("nick"));
                            }
                            rs.close();
                        }
                    }
                } catch (SQLException e) {
                    RatCusReputation.plugin.getLogger().info(e.getMessage());
                }
                return arguments;
            }
            return null;
        }
    }
}
