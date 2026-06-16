package it.unime.orion.systems;

import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.errors.InvalidGameConfigurationException;
import it.unime.orion.powerups.PowerUpType;

import java.util.Objects;
import java.util.Optional;

/**
 * Optional alternative reward policy added as an extension example: it can be
 * plugged into the existing boss reward system without modifying that system.
 */
public final class ScoreThresholdBossRewardPolicy implements BossRewardPolicy {

    private final int minimumBossScoreValue;
    private final PowerUpType rewardType;

    public ScoreThresholdBossRewardPolicy(int minimumBossScoreValue, PowerUpType rewardType) {
        if (minimumBossScoreValue < 0) {
            throw new InvalidGameConfigurationException("minimumBossScoreValue must be >= 0");
        }
        this.minimumBossScoreValue = minimumBossScoreValue;
        this.rewardType = Objects.requireNonNull(rewardType, "rewardType");
    }

    @Override
    public Optional<PowerUpType> getRewardType(EnemyDestroyedEvent event) {
        Objects.requireNonNull(event, "event");
        if (!event.isBossKill() || event.getScoreValue() < minimumBossScoreValue) {
            return Optional.empty();
        }
        return Optional.of(rewardType);
    }
}
