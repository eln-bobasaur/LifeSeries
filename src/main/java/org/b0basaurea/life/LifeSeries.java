package org.b0basaurea.life;

import org.b0basaurea.life.Commands.SessionCommands;
import org.b0basaurea.life.Managers.BoogeymanManager;
import org.b0basaurea.life.Commands.LivesCommand;
import org.b0basaurea.life.Managers.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class LifeSeries extends JavaPlugin {

    private LivesManager manager;
    private ScoreboardManager scoreboardManager;
    private SessionManager sessionManager;
    private BoogeymanManager boogeymanManager;
    private KillManager killManager;
//    private IndirectKillManager indirectKillManager;

    @Override
    public void onEnable() {
        scoreboardManager = new ScoreboardManager();
        manager = new LivesManager(this, scoreboardManager);
        sessionManager = new SessionManager(killManager, boogeymanManager, manager);
        boogeymanManager = new BoogeymanManager(manager, this, scoreboardManager);
//        indirectKillManager = new IndirectKillManager(this);
        killManager = new KillManager(boogeymanManager, manager);

        getServer().getPluginManager().registerEvents(new DeathManager(manager, scoreboardManager), this);
        getServer().getPluginManager().registerEvents(new JoinManager(manager, scoreboardManager, killManager), this);
        getServer().getPluginManager().registerEvents(new ChatManager(manager, scoreboardManager), this);


        getServer().getPluginManager().registerEvents(killManager, this);

        LivesCommand livesCommand = new LivesCommand(manager, scoreboardManager, this);
        SessionCommands sessionCommands = new SessionCommands(sessionManager, boogeymanManager);
        //Commands
        getCommand("lives").setExecutor(livesCommand);
        getCommand("setLives").setExecutor(livesCommand);
        getCommand("addLives").setExecutor(livesCommand);
        getCommand("removeLives").setExecutor(livesCommand);
        getCommand("givelife").setExecutor(livesCommand);
        getCommand("session").setExecutor(sessionCommands);

        //Tab completers
        getCommand("session").setTabCompleter(sessionCommands);
        getCommand("lives").setTabCompleter(livesCommand);
        getCommand("setLives").setTabCompleter(livesCommand);
        getCommand("addLives").setTabCompleter(livesCommand);
        getCommand("removeLives").setTabCompleter(livesCommand);
        getCommand("givelife").setTabCompleter(livesCommand);

        ActionBarManager actionBarManager = new ActionBarManager(this, manager, scoreboardManager);
        actionBarManager.start();

        //TNT Recipe
        NamespacedKey key = NamespacedKey.minecraft("tnt");

        Bukkit.removeRecipe(key);

        ShapedRecipe recipe = new ShapedRecipe(
                key,
                new ItemStack(Material.TNT)
        );

        recipe.shape(
                "PSP",
                "SGS",
                "PSP"
        );

        recipe.setIngredient('P', Material.PAPER);
        recipe.setIngredient('S', Material.SAND);
        recipe.setIngredient('G', Material.GUNPOWDER);

        Bukkit.addRecipe(recipe);
    }

    @Override
    public void onDisable() {
        manager.saveAllPlayers();
    }
}
