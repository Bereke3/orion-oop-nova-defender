package it.unime.orion.systems;

import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.powerups.PowerUpType;

import java.util.Optional;

public final class DefaultBossRewardPolicy implements BossRewardPolicy {

    @Override
    public Optional<PowerUpType> getRewardType(EnemyDestroyedEvent event) {
        if (event.isBossKill()) {
            return Optional.of(PowerUpType.EXTRA_LIFE);
        }
        return Optional.empty();
    }
}
