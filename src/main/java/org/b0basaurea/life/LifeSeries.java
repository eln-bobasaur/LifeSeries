package org.b0basaurea.life;

import org.b0basaurea.life.Commands.SessionCommands;
import org.b0basaurea.life.Managers.BoogeymanManager;
import org.b0basaurea.life.Commands.LivesCommand;
import org.b0basaurea.life.Managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class LifeSeries extends JavaPlugin {

    private LivesManager manager;
    private ScoreboardManager scoreboardManager;
    private SessionManager sessionManager;
    private BoogeymanManager boogeymanManager;
    private KillManager killManager;

    @Override
    public void onEnable() {
        scoreboardManager = new ScoreboardManager();
        manager = new LivesManager(this, scoreboardManager);
        sessionManager = new SessionManager();
        boogeymanManager = new BoogeymanManager(manager, this, scoreboardManager);
        killManager = new KillManager(boogeymanManager, manager, scoreboardManager);

        getServer().getPluginManager().registerEvents(new DeathManager(manager, scoreboardManager), this);
        getServer().getPluginManager().registerEvents(new JoinManager(manager, scoreboardManager, killManager), this);
        getServer().getPluginManager().registerEvents(new ChatManager(manager, scoreboardManager), this);


        getServer().getPluginManager().registerEvents(killManager, this);

        LivesCommand livesCommand = new LivesCommand(manager, scoreboardManager);
        SessionCommands sessionCommands = new SessionCommands(sessionManager, boogeymanManager);
        getCommand("lives").setExecutor(livesCommand);
        getCommand("setLives").setExecutor(livesCommand);
        getCommand("addLives").setExecutor(livesCommand);
        getCommand("removeLives").setExecutor(livesCommand);
        getCommand("gift").setExecutor(livesCommand);
        getCommand("session").setExecutor(sessionCommands);
        getCommand("session").setTabCompleter(sessionCommands);

        ActionBarManager actionBarManager = new ActionBarManager(this, manager, scoreboardManager);
        actionBarManager.start();
    }

    @Override
    public void onDisable() {
        manager.saveAllPlayers();
    }
}
