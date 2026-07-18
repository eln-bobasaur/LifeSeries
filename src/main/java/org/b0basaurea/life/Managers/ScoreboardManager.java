package org.b0basaurea.life.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager {

    private final Scoreboard scoreboard;
    private final Objective livesObjective;

    public ScoreboardManager() {
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        createTeam("purple", NamedTextColor.DARK_PURPLE);
        createTeam("green", NamedTextColor.GREEN);
        createTeam("yellow", NamedTextColor.YELLOW);
        createTeam("red", NamedTextColor.RED);
        createTeam("dead", NamedTextColor.GRAY);

        Objective objective = scoreboard.getObjective("lives");

        if (objective == null) {
            objective = scoreboard.registerNewObjective(
                    "lives",
                    Criteria.DUMMY,
                    Component.text("Lives")
            );
        }

        objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        this.livesObjective = objective;
    }

    private void createTeam(String name, NamedTextColor color) {
        Team team = scoreboard.getTeam(name);

        if (team == null) {
            team = scoreboard.registerNewTeam(name);
        }

        team.color(color);
    }

    public void updatePlayerTeam(Player player, int lives) {
        removeFromLifeTeams(player);

        if (lives >= 4) {
            scoreboard.getTeam("purple").addEntry(player.getName());
        } else if (lives == 3) {
            scoreboard.getTeam("green").addEntry(player.getName());
        } else if (lives == 2) {
            scoreboard.getTeam("yellow").addEntry(player.getName());
        } else if (lives == 1) {
            scoreboard.getTeam("red").addEntry(player.getName());
        } else {
            scoreboard.getTeam("dead").addEntry(player.getName());
        }

        updatePlayerLives(player, lives);
    }

    public void updatePlayerLives(Player player, int lives) {
        livesObjective.getScore(player.getName()).setScore(lives);
    }

    private void removeFromLifeTeams(Player player) {
        String name = player.getName();

        scoreboard.getTeam("purple").removeEntry(name);
        scoreboard.getTeam("green").removeEntry(name);
        scoreboard.getTeam("yellow").removeEntry(name);
        scoreboard.getTeam("red").removeEntry(name);
        scoreboard.getTeam("dead").removeEntry(name);
    }

    public NamedTextColor getColorFromLives(int lives) {
        if (lives >= 4) return NamedTextColor.DARK_PURPLE;
        if (lives == 3) return NamedTextColor.GREEN;
        if (lives == 2) return NamedTextColor.YELLOW;
        if (lives == 1) return NamedTextColor.RED;
        return NamedTextColor.GRAY;
    }
}