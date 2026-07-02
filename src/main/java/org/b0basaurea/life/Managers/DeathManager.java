package org.b0basaurea.life.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.time.Duration;
import java.util.UUID;

public class DeathManager implements Listener {

    private final LivesManager livesManager;
    private final ScoreboardManager scoreboardManager;

    public DeathManager(LivesManager manager, ScoreboardManager scoreboardManager)
    {
        livesManager = manager;
        this.scoreboardManager = scoreboardManager;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        Player p = event.getEntity();
        livesManager.removeLives(p, 1);

        int lives = livesManager.getLives(p);

        scoreboardManager.updatePlayerTeam(p, lives);

        for(Player player : Bukkit.getOnlinePlayers())
        {
            player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        }

        if(lives <= 0)
        {
            p.setGameMode(GameMode.SPECTATOR);
            p.getWorld().strikeLightningEffect(p.getLocation());

            Title title = Title.title(
                    Component.text(p.getName(), NamedTextColor.RED),
                    Component.text("IS OUT!", NamedTextColor.GRAY),
                    Title.Times.times(
                            Duration.ofMillis(500),   // Fade in
                            Duration.ofSeconds(3),    // Stay
                            Duration.ofMillis(1000)   // Fade out
                    ));

            for(Player player : Bukkit.getOnlinePlayers())
            {
                player.showTitle(title);
            }
        }
    }
}
