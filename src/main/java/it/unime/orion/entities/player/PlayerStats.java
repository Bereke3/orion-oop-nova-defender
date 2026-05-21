package it.unime.orion.entities.player;

import it.unime.orion.errors.InvalidGameConfigurationException;

public final class PlayerStats {

    private int hp;
    private final int maxHp;

    private boolean shield;
    private double invulnerabilitySecondsLeft;

    public PlayerStats(int maxHp) {
        if (maxHp <= 0) {
            throw new InvalidGameConfigurationException("Player maxHp must be > 0");
        }
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public boolean hasShield() {
        return shield;
    }

    public boolean isInvulnerable() {
        return invulnerabilitySecondsLeft > 0;
    }

    public double getInvulnerabilitySecondsLeft() {
        return invulnerabilitySecondsLeft;
    }

    public void activateShield() {
        shield = true;
    }

    public void deactivateShield() {
        shield = false;
    }

    public void activateInvulnerability(double durationSeconds) {
        if (durationSeconds < 0) {
            throw new IllegalArgumentException("durationSeconds must be >= 0");
        }
        invulnerabilitySecondsLeft = Math.max(invulnerabilitySecondsLeft, durationSeconds);
    }

    public void update(double deltaSeconds) {
        if (deltaSeconds < 0) {
            throw new IllegalArgumentException("deltaSeconds must be >= 0");
        }
        if (invulnerabilitySecondsLeft > 0) {
            invulnerabilitySecondsLeft = Math.max(0, invulnerabilitySecondsLeft - deltaSeconds);
        }
    }

    public void restoreFullHealth() {
        hp = maxHp;
    }

    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("heal amount must be >= 0");
        }
        hp = Math.min(maxHp, hp + amount);
    }

    public void damage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("damage amount must be >= 0");
        }
        if (hp == 0) {
            return;
        }

        if (isInvulnerable()) {
            return;
        }

        if (shield) {
            shield = false;
            return;
        }

        hp = Math.max(0, hp - amount);
    }

    public boolean isAlive() {
        return hp > 0;
    }
}
