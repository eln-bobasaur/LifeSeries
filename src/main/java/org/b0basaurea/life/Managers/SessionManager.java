package org.b0basaurea.life.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

public class SessionManager {

    private final KillManager killManager;
    private final BoogeymanManager boogeymanManager;
    private final LivesManager livesManager;

    private boolean sessionActive;

    public SessionManager(
            KillManager killManager,
            BoogeymanManager boogeymanManager,
            LivesManager livesManager
    ) {
        this.killManager = killManager;
        this.boogeymanManager = boogeymanManager;
        this.livesManager = livesManager;
    }

    public boolean startSession() {
        if (sessionActive) {
            return false;
        }

        sessionActive = true;

        killManager.resetSessionKills();
        //boogeymanManager.startBoogeymanCountdown();

        Bukkit.broadcast(
                Component.text(
                        "The session has started!",
                        NamedTextColor.GREEN
                )
        );

        return true;
    }

    public boolean stopSession() {
        if (!sessionActive) {
            return false;
        }

        sessionActive = false;

        if (boogeymanManager.hasActiveBoogeyman()) {
            boogeymanManager.endBoogeyman();
        } else {
            boogeymanManager.cureBoogeyman();
        }

        livesManager.saveAllPlayers();

        Bukkit.broadcast(
                Component.text(
                        "The session has ended!",
                        NamedTextColor.RED
                )
        );

        return true;
    }

    public boolean isSessionActive() {
        return sessionActive;
    }
}