package org.b0basaurea.life.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillManager implements Listener {

    private final BoogeymanManager boogeymanManager;
    private final LivesManager livesManager;

    private final Map<UUID, Integer> redLifeKills = new HashMap<>();

    public KillManager(
            BoogeymanManager boogeymanManager,
            LivesManager livesManager
    ) {
        this.boogeymanManager = boogeymanManager;
        this.livesManager = livesManager;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getPlayer();
        Player killer = victim.getKiller();

        if (killer == null || killer.equals(victim)) {
            return;
        }

        handleBoogeymanKill(killer);
        handleRedLifeKill(killer, victim);
    }

    private void handleBoogeymanKill(Player killer) {
        if (!killer.equals(boogeymanManager.getBoogeyman())) {
            return;
        }

        boogeymanManager.cureBoogeyman();

        Title title = Title.title(
                Component.text(
                        "You are cured!",
                        NamedTextColor.GREEN
                ),
                Component.empty(),
                Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofSeconds(2),
                        Duration.ofMillis(500)
                )
        );

        killer.showTitle(title);
    }

    private void handleRedLifeKill(
            Player killer,
            Player victim
    ) {
        if (livesManager.getLives(killer) != 1) {
            return;
        }

        if (livesManager.getLives(victim) < 2) {
            return;
        }

        UUID killerId = killer.getUniqueId();

        int newKillCount =
                redLifeKills.getOrDefault(killerId, 0) + 1;

        if (newKillCount >= 2) {
            livesManager.addLives(killer, 1);
            redLifeKills.remove(killerId);

            killer.sendMessage(
                    Component.text(
                            "You earned a life back!",
                            NamedTextColor.GREEN
                    )
            );

            return;
        }

        redLifeKills.put(killerId, newKillCount);

        killer.sendMessage(
                Component.text(
                        "Get 1 more qualifying kill to regain a life.",
                        NamedTextColor.RED
                )
        );
    }

    public void addPlayer(Player player) {
        redLifeKills.putIfAbsent(player.getUniqueId(), 0);
    }

    public void resetSessionKills() {
        redLifeKills.clear();
    }
}