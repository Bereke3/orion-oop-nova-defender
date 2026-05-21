package it.unime.orion.systems;

import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.events.EventBus;
import it.unime.orion.events.EventSubscription;
import it.unime.orion.powerups.DefaultPowerUpFactory;
import it.unime.orion.powerups.PowerUp;
import it.unime.orion.powerups.PowerUpFactory;
import it.unime.orion.powerups.PowerUpType;
import it.unime.orion.world.GameWorld;

import java.util.Objects;
import java.util.Random;

public final class PowerUpDropSystem implements AutoCloseable {

    private final GameWorld world;
    private final Random random;
    private final PowerUpFactory powerUpFactory;
    private final PowerUpDropPolicy dropPolicy;
    private final EventSubscription enemyDestroyedSubscription;

    public PowerUpDropSystem(GameWorld world, EventBus<EnemyDestroyedEvent> enemyDestroyedBus) {
        this(world, enemyDestroyedBus, new DefaultPowerUpFactory(), new PowerUpDropPolicy(0.35, 4), new Random());
    }

    public PowerUpDropSystem(GameWorld world,
                             EventBus<EnemyDestroyedEvent> enemyDestroyedBus,
                             PowerUpFactory powerUpFactory,
                             PowerUpDropPolicy dropPolicy,
                             Random random) {
        this.world = Objects.requireNonNull(world, "world");
        Objects.requireNonNull(enemyDestroyedBus, "enemyDestroyedBus");
        this.powerUpFactory = Objects.requireNonNull(powerUpFactory, "powerUpFactory");
        this.dropPolicy = Objects.requireNonNull(dropPolicy, "dropPolicy");
        this.random = Objects.requireNonNull(random, "random");

        this.enemyDestroyedSubscription = enemyDestroyedBus.subscribe(this::handleEnemyDestroyed);
    }

    private void handleEnemyDestroyed(EnemyDestroyedEvent event) {
        if (!dropPolicy.shouldDrop(countAlivePowerUps(), event.isBossKill(), random.nextDouble())) {
            return;
        }

        PowerUpType type = PowerUpType.randomType(random);
        world.addEntity(powerUpFactory.create(type, event.getX(), event.getY()));
    }

    private int countAlivePowerUps() {
        int count = 0;

        for (var entity : world.getEntitiesView()) {
            if (entity instanceof PowerUp) {
                count++;
            }
        }

        return count;
    }

    @Override
    public void close() {
        enemyDestroyedSubscription.unsubscribe();
    }
}
