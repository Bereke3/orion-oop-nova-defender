package it.unime.orion.level;

import it.unime.orion.errors.InvalidGameConfigurationException;

public final class EnemyTuning {

    private final double speedMultiplier;
    private final double fireRateMultiplier;

    public EnemyTuning(double speedMultiplier, double fireRateMultiplier) {
        if (speedMultiplier <= 0) {
            throw new InvalidGameConfigurationException("speedMultiplier must be > 0");
        }
        if (fireRateMultiplier <= 0) {
            throw new InvalidGameConfigurationException("fireRateMultiplier must be > 0");
        }
        this.speedMultiplier = speedMultiplier;
        this.fireRateMultiplier = fireRateMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public double getFireRateMultiplier() {
        return fireRateMultiplier;
    }
}
