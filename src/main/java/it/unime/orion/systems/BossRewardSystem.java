package it.unime.orion.systems;

import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.events.EventBus;
import it.unime.orion.events.EventSubscription;
import it.unime.orion.powerups.DefaultPowerUpFactory;
import it.unime.orion.powerups.PowerUpFactory;
import it.unime.orion.world.GameWorld;

import java.util.Objects;

public final class BossRewardSystem implements AutoCloseable {

    private final GameWorld world;
    private final PowerUpFactory powerUpFactory;
    private BossRewardPolicy bossRewardPolicy;
    private final EventSubscription enemyDestroyedSubscription;

    public BossRewardSystem(GameWorld world, EventBus<EnemyDestroyedEvent> enemyDestroyedBus) {
        this(world, enemyDestroyedBus, new DefaultPowerUpFactory(), new DefaultBossRewardPolicy());
    }

    public BossRewardSystem(GameWorld world,
                            EventBus<EnemyDestroyedEvent> enemyDestroyedBus,
                            PowerUpFactory powerUpFactory,
                            BossRewardPolicy bossRewardPolicy) {
        this.world = Objects.requireNonNull(world, "world");
        this.powerUpFactory = Objects.requireNonNull(powerUpFactory, "powerUpFactory");
        this.bossRewardPolicy = Objects.requireNonNull(bossRewardPolicy, "bossRewardPolicy");
        this.enemyDestroyedSubscription = Objects.requireNonNull(enemyDestroyedBus, "enemyDestroyedBus")
                .subscribe(this::handleEnemyDestroyed);
    }

    public void setBossRewardPolicy(BossRewardPolicy bossRewardPolicy) {
        this.bossRewardPolicy = Objects.requireNonNull(bossRewardPolicy, "bossRewardPolicy");
    }

    private void handleEnemyDestroyed(EnemyDestroyedEvent event) {
        bossRewardPolicy.getRewardType(event)
                .map(type -> powerUpFactory.create(type, event.getX(), event.getY()))
                .ifPresent(world::addEntity);
    }

    @Override
    public void close() {
        enemyDestroyedSubscription.unsubscribe();
    }
}
