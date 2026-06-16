package it.unime.orion.systems;

import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.events.EventBus;
import it.unime.orion.powerups.ExtraLifePowerUp;
import it.unime.orion.powerups.PowerUp;
import it.unime.orion.powerups.PowerUpFactory;
import it.unime.orion.powerups.PowerUpType;
import it.unime.orion.powerups.ShieldPowerUp;
import it.unime.orion.world.GameWorld;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public final class BossRewardSystemTest {

    @Test
    void testBossRewardSystemDropsRewardOnlyForBossKills() {
        GameWorld world = new GameWorld();
        EventBus<EnemyDestroyedEvent> enemyDestroyedBus = new EventBus<>();
        PowerUpFactory factory = (type, x, y) -> new ExtraLifePowerUp(new Rectangle(24, 24), x, y);

        try (BossRewardSystem rewardSystem = new BossRewardSystem(world, enemyDestroyedBus, factory, new DefaultBossRewardPolicy())) {
            enemyDestroyedBus.publish(new EnemyDestroyedEvent("enemy-1", 100, 140, 50, false));
            assertEquals(0, countPowerUps(world));

            enemyDestroyedBus.publish(new EnemyDestroyedEvent("boss-1", 160, 180, 1500, true));
            assertEquals(1, countPowerUps(world));
        }
    }

    @Test
    void testClosingRewardSystemStopsFutureDrops() {
        GameWorld world = new GameWorld();
        EventBus<EnemyDestroyedEvent> enemyDestroyedBus = new EventBus<>();
        PowerUpFactory factory = (type, x, y) -> new ExtraLifePowerUp(new Rectangle(24, 24), x, y);

        BossRewardSystem rewardSystem = new BossRewardSystem(world, enemyDestroyedBus, factory, new DefaultBossRewardPolicy());
        rewardSystem.close();

        enemyDestroyedBus.publish(new EnemyDestroyedEvent("boss-1", 160, 180, 1500, true));
        assertEquals(0, countPowerUps(world));
    }

    @Test
    void testRewardPolicyCanBeReconfiguredAtRuntime() {
        GameWorld world = new GameWorld();
        EventBus<EnemyDestroyedEvent> enemyDestroyedBus = new EventBus<>();
        PowerUpFactory factory = (type, x, y) -> switch (type) {
            case EXTRA_LIFE -> new ExtraLifePowerUp(new Rectangle(24, 24), x, y);
            case SHIELD -> new ShieldPowerUp(new Rectangle(24, 24), x, y);
            default -> new ExtraLifePowerUp(new Rectangle(24, 24), x, y);
        };

        try (BossRewardSystem rewardSystem = new BossRewardSystem(world, enemyDestroyedBus, factory, new DefaultBossRewardPolicy())) {
            rewardSystem.setBossRewardPolicy(new FixedBossRewardPolicy(Optional.of(PowerUpType.SHIELD)));

            enemyDestroyedBus.publish(new EnemyDestroyedEvent("boss-1", 160, 180, 1500, true));
            assertTrue(world.getEntitiesView().stream().anyMatch(ShieldPowerUp.class::isInstance));

            rewardSystem.setBossRewardPolicy(new FixedBossRewardPolicy(Optional.empty()));
            enemyDestroyedBus.publish(new EnemyDestroyedEvent("boss-2", 200, 200, 1500, true));
            assertEquals(1, countPowerUps(world));
        }
    }

    @Test
    void testNewPolicyExtendsRewardSystemWithoutChangingConsumerCode() {
        GameWorld world = new GameWorld();
        EventBus<EnemyDestroyedEvent> enemyDestroyedBus = new EventBus<>();
        PowerUpFactory factory = (type, x, y) -> new ShieldPowerUp(new Rectangle(24, 24), x, y);

        try (BossRewardSystem rewardSystem = new BossRewardSystem(
                world,
                enemyDestroyedBus,
                factory,
                new ScoreThresholdBossRewardPolicy(2000, PowerUpType.SHIELD)
        )) {
            enemyDestroyedBus.publish(new EnemyDestroyedEvent("boss-1", 160, 180, 2500, true));
            enemyDestroyedBus.publish(new EnemyDestroyedEvent("boss-2", 200, 220, 1500, true));

            assertEquals(1, countPowerUps(world));
            assertTrue(world.getEntitiesView().stream().anyMatch(ShieldPowerUp.class::isInstance));
        }
    }

    private int countPowerUps(GameWorld world) {
        int count = 0;

        for (var entity : world.getEntitiesView()) {
            if (entity instanceof PowerUp) {
                count++;
            }
        }

        return count;
    }
}
