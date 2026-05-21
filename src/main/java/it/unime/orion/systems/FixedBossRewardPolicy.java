package it.unime.orion.systems;

import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.powerups.PowerUpType;

import java.util.Objects;
import java.util.Optional;

public final class FixedBossRewardPolicy implements BossRewardPolicy {

    private final Optional<PowerUpType> rewardType;

    public FixedBossRewardPolicy(Optional<PowerUpType> rewardType) {
        this.rewardType = Objects.requireNonNull(rewardType, "rewardType");
    }

    @Override
    public Optional<PowerUpType> getRewardType(EnemyDestroyedEvent event) {
        if (!event.isBossKill()) {
            return Optional.empty();
        }
        return rewardType;
    }
}
