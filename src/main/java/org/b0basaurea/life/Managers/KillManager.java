package org.b0basaurea.life.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

public class KillManager implements Listener {

    public BoogeymanManager boogey;
    private LivesManager livesManager;
//    private IndirectKillManager indirectKillManager;
    private ScoreboardManager scoreboardManager;

    private HashMap<UUID, Integer> kills = new HashMap<>();

    public KillManager(BoogeymanManager boogey, LivesManager livesManager, ScoreboardManager scoreboardManager)
    {
        this.boogey = boogey;
        this.livesManager = livesManager;
        this.scoreboardManager = scoreboardManager;
//        this.indirectKillManager = indirectKillManager;

        for(Player player : Bukkit.getOnlinePlayers())
        {
            kills.put(player.getUniqueId(), 0);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        Player killer = event.getPlayer().getKiller();
        Player victim = event.getPlayer();

        if (killer == null || killer.equals(victim)) {
            return;
        }

//        if (killer == null) {
//            killer = indirectKillManager.getIndirectKiller(event.getPlayer());
//        }


        if(killer == boogey.getBoogeyman() && killer != event.getPlayer())
        {
            boogey.setBoogeyman(null);

            Title title = Title.title(
                    Component.text("You are cured!", NamedTextColor.GREEN),
                    Component.empty(),
                    Title.Times.times(
                            Duration.ofMillis(500),
                            Duration.ofSeconds(2),
                            Duration.ofMillis(500)
                    )
            );

            killer.showTitle(title);
        }

        if(livesManager.getLives(killer) <= 1 && livesManager.getLives(event.getPlayer()) >= 2) //If red name, allow them to gain one life back after 2 kills. Resets every session
        {
            killer.sendMessage("Take " + (2 - kills.get(killer.getUniqueId())) + " lives and you'll get a life back...");

            UUID killerId = killer.getUniqueId();
            int newKills = kills.getOrDefault(killerId, 0) + 1;
            kills.put(killerId, newKills);

            if (newKills >= 2) {
                livesManager.addLives(killer, 1);
                kills.put(killerId, 0);

                killer.sendMessage(
                        Component.text(
                                "You earned a life back!",
                                NamedTextColor.GREEN
                        )
                );
            } else {
                killer.sendMessage(
                        Component.text(
                                "Get 1 more qualifying kill to regain a life.",
                                NamedTextColor.RED
                        )
                );
            }
        }
    }

    public void addToKill(Player p)
    {
        kills.put(p.getUniqueId(), 0);
    }
}
