package it.unime.orion.systems;

import it.unime.orion.entities.effects.ExplosionEffect;
import it.unime.orion.entities.effects.PowerUpPickupEffect;
import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.events.EventBus;
import it.unime.orion.events.PowerUpCollectedEvent;
import it.unime.orion.powerups.PowerUpType;
import it.unime.orion.world.GameWorld;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class VisualEffectSystemTest {

    @Test
    void testEnemyDestroyedEventSpawnsExplosionEffect() {
        GameWorld world = new GameWorld();
        EventBus<EnemyDestroyedEvent> enemyDestroyedBus = new EventBus<>();
        EventBus<PowerUpCollectedEvent> powerUpCollectedBus = new EventBus<>();

        new VisualEffectSystem(world, enemyDestroyedBus, powerUpCollectedBus);
        enemyDestroyedBus.publish(new EnemyDestroyedEvent("enemy-1", 120, 160, 50, false));

        assertEquals(1, countEffects(world, ExplosionEffect.class));
    }

    @Test
    void testPowerUpCollectedEventSpawnsPickupEffect() {
        GameWorld world = new GameWorld();
        EventBus<EnemyDestroyedEvent> enemyDestroyedBus = new EventBus<>();
        EventBus<PowerUpCollectedEvent> powerUpCollectedBus = new EventBus<>();

        new VisualEffectSystem(world, enemyDestroyedBus, powerUpCollectedBus);
        powerUpCollectedBus.publish(new PowerUpCollectedEvent("powerup-1", PowerUpType.HEAL, 90, 140));

        assertEquals(1, countEffects(world, PowerUpPickupEffect.class));
    }

    private int countEffects(GameWorld world, Class<?> effectType) {
        int count = 0;
        for (var entity : world.getEntitiesView()) {
            if (effectType.isInstance(entity)) {
                count++;
            }
        }
        return count;
    }
}
