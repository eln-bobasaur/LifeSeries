package org.b0basaurea.life.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.fenum.qual.SwingElementOrientation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    private final List<BukkitTask> countdownTasks = new ArrayList<>();

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
                "The Boogeyman will be chosen in about 5 minutes...",
                NamedTextColor.RED
        ));

        // 4:00 - 1 minute warning
        schedule(() -> {
            Bukkit.broadcast(Component.text(
                    "The Boogeyman will be chosen in 1 minute...",
                    NamedTextColor.RED
            ));

            for (Player p : Bukkit.getOnlinePlayers()) {

                p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
            }
        }, 4 * MINUTES);

        // 4:45 - almost time warning
        schedule(() -> {
            Bukkit.broadcast(Component.text(
                    "The Boogeyman is about to be chosen...",
                    NamedTextColor.RED
            ));
        }, 4 * MINUTES + 45 * SECONDS);

        // 4:57, 4:58, 4:59 - countdown
        schedule(() -> showCountdown(3), 4 * MINUTES + 55 * SECONDS); // 4 * SECONDS
        schedule(() -> showCountdown(2), 4 * MINUTES + 57 * SECONDS); // 6 * SECONDS
        schedule(() -> showCountdown(1), 4 * MINUTES + 59 * SECONDS); // 8 * SECONDS

        // 5:00 - reveal
        schedule(() -> {
            chooseAndRevealBoogeyman();
            boogeyCountdownActive = false;
            countdownTasks.clear();
        }, 5 * MINUTES);
    }

    public void endBoogeyman() {
        if(boogeyman == null)
        {
            Bukkit.broadcast(Component.text("There is no active boogeyman.", NamedTextColor.RED));
            return;
        }

        livesManager.setLives(boogeyman, 1);

        boogeyman.sendMessage(Component.text(
                "You failed to get your kill. You are now on red life...",
                NamedTextColor.RED
        ));

        cancelCountdown();
        boogeyman = null;
    }

    public void cureBoogeyman() {
        if(boogeyman == null)
        {
            Bukkit.broadcast(Component.text("There is no active boogeyman.", NamedTextColor.RED));
            return;
        }

        Title title = Title.title(
                Component.text("You are cured!", NamedTextColor.GREEN),
                Component.empty(),
                Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofSeconds(2),
                        Duration.ofMillis(500)
                )
        );

        boogeyman.showTitle(title);
        cancelCountdown();

        boogeyman = null;
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
                        Duration.ofMillis(200),
                        Duration.ofMillis(1400),
                        Duration.ofMillis(300)
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
            Bukkit.broadcast(
                    Component.text(
                            "No valid players for Boogeyman selection.",
                            NamedTextColor.RED
                    )
            );

            boogeyman = null;
            boogeyCountdownActive = false;
            countdownTasks.clear();
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
                Component.text("NOT THE BOOGEYMAN", NamedTextColor.GREEN),
                Component.empty(),
                Title.Times.times(
                        Duration.ofMillis(1000),
                        Duration.ofSeconds(4),
                        Duration.ofMillis(1500)
                )
        );

        Title boogeyTitle = Title.title(
                Component.text("THE BOOGEYMAN", NamedTextColor.DARK_RED),
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

            schedule( () -> {
                if (player.equals(boogeyman)) {
                    player.showTitle(boogeyTitle);
                    player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 1.0f);
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 0.7f);

                    player.sendMessage(
                            Component.text("You are ", NamedTextColor.GRAY)
                                    .append(Component.text("THE BOOGEYMAN", NamedTextColor.RED))
                                    .append(Component.text("! Your task is to get a kill on a ", NamedTextColor.GRAY))
                                    .append(Component.text("purple", NamedTextColor.DARK_PURPLE))
                                    .append(Component.text(", ", NamedTextColor.GRAY))
                                    .append(Component.text("green", NamedTextColor.GREEN))
                                    .append(Component.text(",", NamedTextColor.GRAY))
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

    private void schedule(Runnable action, long delay) {
        BukkitTask task =
                Bukkit.getScheduler().runTaskLater(
                        plugin,
                        action,
                        delay
                );

        countdownTasks.add(task);
    }

    private void cancelCountdown() {
        for (BukkitTask task : countdownTasks) {
            task.cancel();
        }

        countdownTasks.clear();
        boogeyCountdownActive = false;
    }
}
