package it.unime.orion.level;

import it.unime.orion.errors.InvalidGameConfigurationException;
import it.unime.orion.powerups.PowerUpType;

import java.util.Objects;
import java.util.Optional;

public final class LevelRuntimeTuning {

    private final double ambientPowerUpSpawnIntervalSeconds;
    private final int maxAmbientPowerUps;
    private final Optional<PowerUpType> bossRewardType;

    public LevelRuntimeTuning(double ambientPowerUpSpawnIntervalSeconds,
                              int maxAmbientPowerUps,
                              Optional<PowerUpType> bossRewardType) {
        if (ambientPowerUpSpawnIntervalSeconds <= 0) {
            throw new InvalidGameConfigurationException("ambientPowerUpSpawnIntervalSeconds must be > 0");
        }
        if (maxAmbientPowerUps < 0) {
            throw new InvalidGameConfigurationException("maxAmbientPowerUps must be >= 0");
        }
        this.ambientPowerUpSpawnIntervalSeconds = ambientPowerUpSpawnIntervalSeconds;
        this.maxAmbientPowerUps = maxAmbientPowerUps;
        this.bossRewardType = Objects.requireNonNull(bossRewardType, "bossRewardType");
    }

    public double getAmbientPowerUpSpawnIntervalSeconds() {
        return ambientPowerUpSpawnIntervalSeconds;
    }

    public int getMaxAmbientPowerUps() {
        return maxAmbientPowerUps;
    }

    public Optional<PowerUpType> getBossRewardType() {
        return bossRewardType;
    }

    public static LevelRuntimeTuning defaultTuning() {
        return new LevelRuntimeTuning(8.0, 2, Optional.of(PowerUpType.EXTRA_LIFE));
    }
}
