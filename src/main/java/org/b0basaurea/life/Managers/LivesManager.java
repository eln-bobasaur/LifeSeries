package org.b0basaurea.life.Managers;

import org.b0basaurea.life.LifePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Score;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LivesManager {

    private final JavaPlugin plugin;
    private final Map<UUID, LifePlayer> players = new HashMap<>();

    private File file;
    private FileConfiguration config;

    private ScoreboardManager manager;

    public LivesManager(JavaPlugin plugin, ScoreboardManager scoreboardManager) {
        this.plugin = plugin;
        manager = scoreboardManager;
        setupFile();
        loadAllPlayers();
    }

    private void setupFile() {
        file = new File(plugin.getDataFolder(), "lives.yml");

        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public LifePlayer getLifePlayer(Player player) {
        UUID uuid = player.getUniqueId();

        return players.computeIfAbsent(uuid, id -> {
            int lives = config.getInt("players." + id + ".lives", 3);
            return new LifePlayer(id, lives);
        });
    }

    public void addLives(Player player, int amount) {
        LifePlayer lifePlayer = getLifePlayer(player);
        lifePlayer.addLives(amount);
        savePlayer(lifePlayer);
        manager.updatePlayerTeam(player, amount);
    }

    public void removeLives(Player player, int amount) {
        LifePlayer lifePlayer = getLifePlayer(player);
        lifePlayer.removeLives(amount);
        savePlayer(lifePlayer);
        manager.updatePlayerTeam(player, amount);
    }

    public void setLives(Player player, int amount) {
        LifePlayer lifePlayer = getLifePlayer(player);
        lifePlayer.setLives(amount);
        savePlayer(lifePlayer);
        manager.updatePlayerTeam(player, amount);
    }

    public int getLives(Player player) {
        return getLifePlayer(player).getLives();
    }

    public void savePlayer(LifePlayer lifePlayer) {
        String path = "players." + lifePlayer.getUuid();

        config.set(path + ".lives", lifePlayer.getLives());

        saveFile();
    }

    public void saveAllPlayers() {
        for (LifePlayer lifePlayer : players.values()) {
            String path = "players." + lifePlayer.getUuid();
            config.set(path + ".lives", lifePlayer.getLives());
        }

        saveFile();
    }

    private void loadAllPlayers() {
        if (!config.contains("players")) return;

        for (String uuidString : config.getConfigurationSection("players").getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            int lives = config.getInt("players." + uuidString + ".lives", 3);

            players.put(uuid, new LifePlayer(uuid, lives));
        }
    }

    private void saveFile() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}