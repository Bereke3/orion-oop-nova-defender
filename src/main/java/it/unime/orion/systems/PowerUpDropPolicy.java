package it.unime.orion.systems;

import it.unime.orion.errors.InvalidGameConfigurationException;

public final class PowerUpDropPolicy {

    private final double dropChance;
    private final int maxPowerUpsAlive;

    public PowerUpDropPolicy(double dropChance, int maxPowerUpsAlive) {
        if (dropChance < 0 || dropChance > 1) {
            throw new InvalidGameConfigurationException("dropChance must be in [0, 1]");
        }
        if (maxPowerUpsAlive < 0) {
            throw new InvalidGameConfigurationException("maxPowerUpsAlive must be >= 0");
        }
        this.dropChance = dropChance;
        this.maxPowerUpsAlive = maxPowerUpsAlive;
    }

    public boolean shouldDrop(int alivePowerUps, boolean bossKill, double roll) {
        if (alivePowerUps >= maxPowerUpsAlive) {
            return false;
        }
        if (bossKill) {
            return true;
        }
        return roll < dropChance;
    }

    public double getDropChance() {
        return dropChance;
    }

    public int getMaxPowerUpsAlive() {
        return maxPowerUpsAlive;
    }
}
