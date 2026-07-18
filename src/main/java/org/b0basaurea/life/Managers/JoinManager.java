package org.b0basaurea.life.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinManager implements Listener {
    private LivesManager livesManager;
    private ScoreboardManager scoreboardManager;
    private KillManager killManager;

    public JoinManager(LivesManager manager, ScoreboardManager scoreboardManager, KillManager killManager)
    {
        livesManager = manager;
        this.scoreboardManager = scoreboardManager;
        this.killManager = killManager;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        int lives = livesManager.getLives(player);
        NamedTextColor color = scoreboardManager.getColorFromLives(lives);

        if(lives <= 0)
        {
            player.setGameMode(GameMode.SPECTATOR);
        } else if(lives == 1) {
            killManager.addPlayer(player);
        } else {
            player.setGameMode(GameMode.SURVIVAL);
        }

        scoreboardManager.updatePlayerTeam(player, lives);

        player.displayName(Component.text(player.getName(), color));

        event.joinMessage(
                Component.text()
                        .append(Component.text(player.getName(), color))
                        .append(Component.text(" joined the game", NamedTextColor.YELLOW))
                        .build());
    }
}
