package it.unime.orion.systems;

import it.unime.orion.entities.GameEntity;
import it.unime.orion.entities.effects.ExplosionEffect;
import it.unime.orion.entities.effects.PowerUpPickupEffect;
import it.unime.orion.entities.effects.VisualEffect;
import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.events.EventBus;
import it.unime.orion.events.EventSubscription;
import it.unime.orion.events.PowerUpCollectedEvent;
import it.unime.orion.world.GameWorld;

import java.util.ArrayList;
import java.util.Objects;

public final class VisualEffectSystem implements AutoCloseable {

    private final GameWorld world;
    private final EventSubscription enemyDestroyedSubscription;
    private final EventSubscription powerUpCollectedSubscription;

    public VisualEffectSystem(GameWorld world,
                              EventBus<EnemyDestroyedEvent> enemyDestroyedBus,
                              EventBus<PowerUpCollectedEvent> powerUpCollectedBus) {
        this.world = Objects.requireNonNull(world, "world");
        this.enemyDestroyedSubscription = Objects.requireNonNull(enemyDestroyedBus, "enemyDestroyedBus")
                .subscribe(this::handleEnemyDestroyed);
        this.powerUpCollectedSubscription = Objects.requireNonNull(powerUpCollectedBus, "powerUpCollectedBus")
                .subscribe(this::handlePowerUpCollected);
    }

    public void update() {
        for (GameEntity entity : new ArrayList<>(world.getEntitiesView())) {
            if (entity instanceof VisualEffect effect && effect.isExpired()) {
                world.removeEntity(effect);
            }
        }
    }

    private void handlePowerUpCollected(PowerUpCollectedEvent event) {
        world.addEntity(new PowerUpPickupEffect(event.getX(), event.getY(), event.getPowerUpType()));
    }

    private void handleEnemyDestroyed(EnemyDestroyedEvent event) {
        world.addEntity(new ExplosionEffect(event.getX(), event.getY(), event.isBossKill()));
    }

    @Override
    public void close() {
        enemyDestroyedSubscription.unsubscribe();
        powerUpCollectedSubscription.unsubscribe();
    }
}
