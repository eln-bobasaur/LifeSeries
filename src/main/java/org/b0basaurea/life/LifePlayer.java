package org.b0basaurea.life;

import java.util.UUID;

public class LifePlayer {

    private final UUID uuid;
    private int lives;

    public LifePlayer(UUID uuid, int lives) {
        this.uuid = uuid;
        this.lives = lives;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = Math.max(lives, 0);
    }

    public void addLives(int amount) {
        this.lives += amount;
    }

    public void removeLives(int amount) {
        this.lives = Math.max(this.lives - amount, 0);
    }

    public boolean isOut() {
        return lives <= 0;
    }
}