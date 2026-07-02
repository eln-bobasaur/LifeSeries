package org.b0basaurea.life.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BoogeymanManager  {

    private LivesManager livesManager;
    private final JavaPlugin plugin;
    private ScoreboardManager scoreboardManager;

    private static final long SECONDS = 20L;
    private static final long MINUTES = 60 * SECONDS;

    private boolean boogeyCountdownActive = false;
    private Player boogeyman;

    public BoogeymanManager(LivesManager livesManager, JavaPlugin plugin, ScoreboardManager scoreboardManager)
    {
        this.livesManager = livesManager;
        this.plugin = plugin;
        this.scoreboardManager = scoreboardManager;
    }

    public void startBoogeymanCountdown()
    {
        if(boogeyman != null || boogeyCountdownActive)
        {
            Bukkit.broadcast(Component.text("Boogeyman is already active or being chosen.", NamedTextColor.RED));
            return;
        }

        boogeyCountdownActive = true;

        Bukkit.broadcast(Component.text(
                "The Boogeyman will be chosen in 5 minutes...",
                NamedTextColor.RED
        ));

        // 4:00 - 1 minute warning
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcast(Component.text(
                    "The Boogeyman will be chosen in 1 minute...",
                    NamedTextColor.RED
            ));

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
            }
        }, 4 * MINUTES);

        // 4:45 - almost time warning
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcast(Component.text(
                    "The Boogeyman is about to be chosen...",
                    NamedTextColor.RED
            ));
        }, 4 * MINUTES + 45 * SECONDS);

        // 4:57, 4:58, 4:59 - countdown
        Bukkit.getScheduler().runTaskLater(plugin, () -> showCountdown(3), 4 * MINUTES + 57 * SECONDS);
        Bukkit.getScheduler().runTaskLater(plugin, () -> showCountdown(2), 4 * MINUTES + 58 * SECONDS);
        Bukkit.getScheduler().runTaskLater(plugin, () -> showCountdown(1), 4 * MINUTES + 59 * SECONDS);

        // 5:00 - reveal
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            chooseAndRevealBoogeyman();
            boogeyCountdownActive = false;
        }, 5 * MINUTES);
    }

    public void endBoogeyman() {
        if(boogeyman == null)
        {
            Bukkit.broadcast(Component.text("There is no active boogeyman.", NamedTextColor.RED));
            return;
        }

        livesManager.setLives(boogeyman, 1);
        scoreboardManager.updatePlayerTeam(boogeyman, livesManager.getLives(boogeyman));

        boogeyman.sendMessage(Component.text(
                "You failed to get your kill. You are now on red life...",
                NamedTextColor.RED
        ));

        boogeyman = null;
    }

    public void clearBoogeyman()
    {
        boogeyman = null;
        boogeyCountdownActive = false;
    }

    private void showCountdown(int seconds) {

        NamedTextColor color = switch (seconds)
        {
            case 1 -> NamedTextColor.RED;
            case 2 -> NamedTextColor.YELLOW;
            case 3 -> NamedTextColor.GREEN;
            default -> NamedTextColor.GRAY;
        };

        Title title = Title.title(
                Component.text(seconds, color),
                Component.empty(),
                Title.Times.times(
                        Duration.ofMillis(250),
                        Duration.ofSeconds(1),
                        Duration.ofMillis(250)
                )
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 1.0f);
        }
    }

    private void chooseAndRevealBoogeyman() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        //Removes red names and everyone who is already dead
        players.removeIf(player -> livesManager.getLives(player) <= 1);

        if (players.isEmpty()) {
            Bukkit.broadcast(Component.text("No valid players for Boogeyman selection.", NamedTextColor.RED));
            return;
        }

        int randomInt = ThreadLocalRandom.current().nextInt(players.size());
        boogeyman = players.get(randomInt);

        Title suspenseTitle = Title.title(
                Component.text("You are...", NamedTextColor.YELLOW),
                Component.empty(),
                Title.Times.times(
                        Duration.ofMillis(250),
                        Duration.ofSeconds(4),
                        Duration.ofMillis(250)
                )
        );

        Title notBoogeyTitle = Title.title(
                Component.text("NOT the Boogeyman", NamedTextColor.GREEN),
                Component.empty(),
                Title.Times.times(
                        Duration.ofMillis(1000),
                        Duration.ofSeconds(4),
                        Duration.ofMillis(1500)
                )
        );

        Title boogeyTitle = Title.title(
                Component.text("the boogeyman", NamedTextColor.DARK_RED),
                Component.empty(),
                Title.Times.times(
                        Duration.ofMillis(1000),
                        Duration.ofSeconds(4),
                        Duration.ofMillis(1500)
                )
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(suspenseTitle);
            player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.5f, 0.7f);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.equals(boogeyman)) {
                    player.showTitle(boogeyTitle);
                    player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 1.0f);
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 0.7f);

                    player.sendMessage(
                            Component.text("You are ", NamedTextColor.GRAY)
                                    .append(Component.text("THE BOOGEYMAN", NamedTextColor.RED))
                                    .append(Component.text("! Your task is to get a kill on a ", NamedTextColor.GRAY))
                                    .append(Component.text("green", NamedTextColor.GREEN))
                                    .append(Component.text(" or ", NamedTextColor.GRAY))
                                    .append(Component.text("yellow", NamedTextColor.YELLOW))
                                    .append(Component.text(" player by the end of the session. ", NamedTextColor.GRAY))
                                    .append(Component.text("Fail and you turn RED!", NamedTextColor.RED))
                    );
                } else {
                    player.showTitle(notBoogeyTitle);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.2f);
                }
            }, 60L);
        }
    }

    public Player getBoogeyman() {
        return boogeyman;
    }

    public void setBoogeyman(Player player) {
        this.boogeyman = player;
    }

    public boolean isBoogeyCountdownActive() {
        return boogeyCountdownActive;
    }

    public boolean hasActiveBoogeyman() {
        return boogeyman != null;
    }
}
