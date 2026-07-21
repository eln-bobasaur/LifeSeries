package org.b0basaurea.life.Commands;

import org.b0basaurea.life.Managers.BoogeymanManager;
import org.b0basaurea.life.Managers.SessionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SessionCommands implements CommandExecutor, TabCompleter {

    private SessionManager sessionManager;
    private BoogeymanManager boogeymanManager;

    public SessionCommands(SessionManager sessionManager, BoogeymanManager boogeymanManager)
    {
        this.sessionManager = sessionManager;
        this.boogeymanManager = boogeymanManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if(!sender.isOp())
        {
            sender.sendMessage("You do not have permission for this command.");
            return true;
        }

        if(args.length == 0)
        {
            sender.sendMessage("Usage:");
            sender.sendMessage("/session start");
            sender.sendMessage("/session stop(or end)");
            sender.sendMessage("/session boogey start");
            sender.sendMessage("/session boogey end");
            sender.sendMessage("/session boogey done");
            sender.sendMessage("/session boogey clean");
            return true;
        }

        switch (args[0].toLowerCase())
        {
            case "start" -> {
                if (!sessionManager.startSession()) {
                    sender.sendMessage("A session is already active.");
                    return true;
                }

                sender.sendMessage("Session started.");
            }

            case "stop", "end" -> {
                if (!sessionManager.stopSession()) {
                    sender.sendMessage("There is no active session.");
                    return true;
                }

                sender.sendMessage("Session stopped.");
                boogeymanManager.endBoogeyman();
            }

            case "boogey" -> {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /session boogey <start|end|clear>");
                    return true;
                }

                switch (args[1].toLowerCase()) {
                    case "start" -> {
                        boogeymanManager.startBoogeymanCountdown();
                        sender.sendMessage("Boogeyman countdown started.");
                    }

                    case "end" -> {
                        boogeymanManager.endBoogeyman();
                        sender.sendMessage("Boogeyman ended.");
                    }

                    case "done" -> {
                        boogeymanManager.cureBoogeyman();
                        sender.sendMessage("Boogeyman done.");
                    }

                    case "clear" -> {
                        boogeymanManager.cureBoogeyman();
                        sender.sendMessage("Boogeyman cleared");
                    }

                    default -> sender.sendMessage("Usage: /session boogey <start|end|clear>");
                }
            }

            default -> sender.sendMessage("Unknown subcommand. Use /session start, stop, or boogey.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!sender.isOp())
            return List.of();

        if(args.length == 1)
        {
            return filter(List.of("start", "stop", "end", "boogey"), args[0]);
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("boogey"))
        {
            return filter(List.of("start", "end", "clear", "done"), args[1]);
        }

        return List.of();
    }

    private List<String> filter(List<String> options, String input) {
        return options.stream()
                .filter(option -> option.toLowerCase().startsWith(input.toLowerCase()))
                .toList();
    }
}
