package org.b0basaurea.life.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ActionBarManager {

    private final JavaPlugin plugin;
    private final LivesManager livesManager;
    private final ScoreboardManager scoreboardManager;

    public ActionBarManager(JavaPlugin plugin,
                            LivesManager livesManager,
                            ScoreboardManager scoreboardManager) {
        this.plugin = plugin;
        this.livesManager = livesManager;
        this.scoreboardManager = scoreboardManager;
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {

                int lives = livesManager.getLives(player);

                player.sendActionBar(
                        Component.text("Lives: ", NamedTextColor.GRAY)
                                .append(Component.text(lives,
                                        scoreboardManager.getColorFromLives(lives)))
                );
            }

        }, 0L, 10L);
    }
}