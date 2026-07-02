package org.b0basaurea.life.Managers;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager {

    private final Scoreboard scoreboard;

    public ScoreboardManager()
    {
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        createTeam("green", NamedTextColor.GREEN);
        createTeam("yellow", NamedTextColor.YELLOW);
        createTeam("red", NamedTextColor.RED);
        createTeam("dead", NamedTextColor.GRAY);
    }

    private void createTeam(String name, NamedTextColor color)
    {
        Team team = scoreboard.getTeam(name);

        if(team == null)
        {
            team = scoreboard.registerNewTeam(name);
        }

        team.color(color);
    }

    public void updatePlayerTeam(Player player, int lives)
    {
        removeFromLifeTeams(player);

        if(lives >= 3)
        {
            scoreboard.getTeam("green").addEntry(player.getName());
        } else if(lives == 2)
        {
            scoreboard.getTeam("yellow").addEntry(player.getName());
        } else if(lives == 1)
        {
            scoreboard.getTeam("red").addEntry(player.getName());
        } else {
            scoreboard.getTeam("dead").addEntry(player.getName());
        }
    }

    private void removeFromLifeTeams(Player player)
    {
        String name = player.getName();

        scoreboard.getTeam("green").removeEntry(name);
        scoreboard.getTeam("yellow").removeEntry(name);
        scoreboard.getTeam("red").removeEntry(name);
        scoreboard.getTeam("dead").removeEntry(name);
    }

    public NamedTextColor getColorFromLives(int lives) {
        if (lives >= 3) return NamedTextColor.GREEN;
        if (lives == 2) return NamedTextColor.YELLOW;
        if (lives == 1) return NamedTextColor.RED;
        return NamedTextColor.GRAY;
    }
}
