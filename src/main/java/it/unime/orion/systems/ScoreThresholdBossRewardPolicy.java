package it.unime.orion.systems;

import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.errors.InvalidGameConfigurationException;
import it.unime.orion.powerups.PowerUpType;

import java.util.Objects;
import java.util.Optional;

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
