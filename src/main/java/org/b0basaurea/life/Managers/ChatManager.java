package org.b0basaurea.life.Managers;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatManager implements Listener {

    private final LivesManager livesManager;
    private final ScoreboardManager scoreboardManager;

    public ChatManager(LivesManager livesManager, ScoreboardManager scoreboardManager) {
        this.livesManager = livesManager;
        this.scoreboardManager = scoreboardManager;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        int lives = livesManager.getLives(player);
        NamedTextColor color = scoreboardManager.getColorFromLives(lives);

        event.renderer((source, sourceDisplayName, message, viewer) ->
                Component.text()
                        .append(Component.text("<"))
                        .append(Component.text(source.getName(), color))
                        .append(Component.text("> "))
                        .append(message)
                        .build()
        );
    }
}