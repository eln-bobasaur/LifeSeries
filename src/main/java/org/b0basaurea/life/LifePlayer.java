package org.b0basaurea.life;

import java.util.UUID;

public class LifePlayer {

    public static final int MIN_LIVES = 0;
    public static final int MAX_LIVES = 4;

    private final UUID uuid;
    private int lives;

    public LifePlayer(UUID uuid, int lives) {
        this.uuid = uuid;
        setLives(lives);
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = Math.max(
                MIN_LIVES,
                Math.min(MAX_LIVES, lives)
        );
    }

    public void addLives(int amount) {
        setLives(lives + amount);
    }

    public void removeLives(int amount) {
        setLives(lives - amount);
    }

    public boolean isOut() {
        return lives <= 0;
    }
}