package org.b0basaurea.life.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

public class SessionManager {

    private boolean sessionActive = false;

    public void startSession() {
        if(sessionActive)
            return;

        sessionActive = true;

        Bukkit.broadcast(
                Component.text("The session has started!", NamedTextColor.GREEN));
    }

    public void stopSession()
    {
        if(!sessionActive)
            return;

        sessionActive = false;

        Bukkit.broadcast(
                Component.text("The session has ended!", NamedTextColor.RED));
    }

    public boolean isSessionActive() {
        return sessionActive;
    }
}
