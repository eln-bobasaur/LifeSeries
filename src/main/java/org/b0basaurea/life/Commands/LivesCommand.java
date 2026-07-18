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

        if(command.getName().equalsIgnoreCase("gift"))
        {
            if(!(sender instanceof Player player))
                return false;

            if(target.equals(player)) {
                sender.sendMessage("You can't gift yourself a life, silly.");
                return false;
            }

            if(livesManager.getLives(target) >= 4)
            {
                sender.sendMessage(target.displayName() + " already has max lives.");
                return false;
            }

            if (livesManager.getLives(player) <= 1) {
                player.sendMessage(
                        Component.text(
                                "You cannot give away your final life.",
                                NamedTextColor.RED
                        )
                );
                return true;
            }

            livesManager.addLives(target, 1);
            livesManager.removeLives((Player) sender, 1);
            scoreboardManager.updatePlayerTeam(target, livesManager.getLives(target));

            player.playEffect(EntityEffect.PROTECTED_FROM_DEATH);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation().add(0, 1.2, 0), 30, 0.6, 0.9, 0.6, 0.05);
            }, 20L);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                target.playEffect(EntityEffect.PROTECTED_FROM_DEATH);
                target.playSound(target.getLocation(), Sound.ITEM_TOTEM_USE, 0.8f, 1.0f);
            }, 40L);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                target.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, target.getLocation().add(0, 1.2, 0), 60, 0.6, 0.9, 0.6, 0.05);
            }, 50L);

            sender.sendMessage(ChatColor.GREEN + "You gave " + target.getName() + " a life.");
            target.sendMessage(ChatColor.GREEN + "You were given a life!");
            return true;
        }

        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        int amount = 1;

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

            if(livesManager.getLives(target) >= 4 || livesManager.getLives(target) + amount >= 4)
            {
                sender.sendMessage(target.name().append(Component.text(" already has max lives")));
                return false;
            }

            livesManager.addLives(target, amount);
        }

        if (command.getName().equalsIgnoreCase("removeLives")) {
            if(livesManager.getLives(target) <= 0 || livesManager.getLives(target) - amount <= 0)
            {
                sender.sendMessage(target.name().append(Component.text(" already has <= 0 lives")));
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

            if (command.getName().equalsIgnoreCase("setLives")) {
                return filter(List.of("0", "1", "2", "3", "4"), args[1]);
            }

            if (command.getName().equalsIgnoreCase("addLives")
                    || command.getName().equalsIgnoreCase("removeLives")) {
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