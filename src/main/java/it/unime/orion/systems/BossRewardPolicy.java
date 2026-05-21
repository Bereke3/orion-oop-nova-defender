package it.unime.orion.systems;

import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.powerups.PowerUpType;

import java.util.Optional;

public interface BossRewardPolicy {
    Optional<PowerUpType> getRewardType(EnemyDestroyedEvent event);
}
