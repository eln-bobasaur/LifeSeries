package org.b0basaurea.life.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.b0basaurea.life.LifeSeries;
import org.b0basaurea.life.Managers.LivesManager;
import org.b0basaurea.life.Managers.ScoreboardManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LivesCommand implements CommandExecutor, TabCompleter {

    private final LivesManager livesManager;
    private final ScoreboardManager scoreboardManager;
    private final LifeSeries plugin;

    public LivesCommand(LivesManager livesManager, ScoreboardManager scoreboardManager, LifeSeries plugin) {
        this.livesManager = livesManager;
        this.scoreboardManager = scoreboardManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("lives")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this.");
                return true;
            }

            sender.sendMessage("You have " + livesManager.getLives(player) + " lives.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player> [amount]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "That player is not online or does not exist.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("givelife")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(
                        ChatColor.RED + "Only players can give lives."
                );
                return true;
            }

            if (target.equals(player)) {
                player.sendMessage(
                        ChatColor.RED + "You cannot give yourself a life."
                );
                return true;
            }

            if (livesManager.getLives(player) <= 1) {
                player.sendMessage(
                        ChatColor.RED + "You cannot give away your final life."
                );
                return true;
            }

            if (livesManager.getLives(target) >= 4) {
                player.sendMessage(
                        ChatColor.RED
                                + target.getName()
                                + " already has the maximum number of lives."
                );
                return true;
            }

            livesManager.removeLives(player, 1);
            livesManager.addLives(target, 1);

            player.sendMessage(
                    ChatColor.GREEN
                            + "You gave "
                            + target.getName()
                            + " one of your lives."
            );

            target.sendMessage(
                    ChatColor.GREEN
                            + player.getName()
                            + " gave you a life!"
            );

            return true;
        }

        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        int amount = 1;

        if (amount < 0) {
            sender.sendMessage(
                    ChatColor.RED + "Amount cannot be negative."
            );
            return true;
        }

        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Amount must be a number.");
                return true;
            }
        }

        if (command.getName().equalsIgnoreCase("setLives")) {
            livesManager.setLives(target, amount);
        }

        if (command.getName().equalsIgnoreCase("addLives")) {

            if(livesManager.getLives(target) + amount > 4)
            {
                sender.sendMessage(target.name().append(Component.text(" already has max lives")));
                return false;
            }

            livesManager.addLives(target, amount);
        }

        if (command.getName().equalsIgnoreCase("removeLives")) {
            if(livesManager.getLives(target) - amount <= 0)
            {
                sender.sendMessage(target.name().append(Component.text(" can not make someone have negative lives")));
                return false;
            }

            livesManager.removeLives(target, amount);
        }

        int lives = livesManager.getLives(target);
        scoreboardManager.updatePlayerTeam(target, lives);

        if (lives <= 0) {
            target.setGameMode(GameMode.SPECTATOR);
        } else if (target.getGameMode() == GameMode.SPECTATOR) {
            target.setGameMode(GameMode.SURVIVAL);
        }

        sender.sendMessage(ChatColor.GREEN + target.getName() + " now has " + lives + " lives.");
        return true;
    }


    //Tab completer
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {

        // First argument: player name
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        // Second argument: amount
        if (args.length == 2) {
            if (command.getName().equalsIgnoreCase("setlives")) {
                return filter(
                        List.of("0", "1", "2", "3", "4"),
                        args[1]
                );
            }

            if (command.getName().equalsIgnoreCase("addlives")
                    || command.getName().equalsIgnoreCase("removelives")) {
                return filter(List.of("1", "2", "3", "4"), args[1]);
            }
        }

        return List.of();
    }

    private List<String> filter(List<String> options, String input) {
        return options.stream()
                .filter(option -> option.startsWith(input))
                .toList();
    }
}