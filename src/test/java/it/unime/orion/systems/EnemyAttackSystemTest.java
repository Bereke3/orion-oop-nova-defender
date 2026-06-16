package it.unime.orion.systems;

import it.unime.orion.combat.BasicWeapon;
import it.unime.orion.entities.enemy.Enemy;
import it.unime.orion.entities.enemy.behavior.EnemyBehavior;
import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.entities.player.PlayerStats;
import it.unime.orion.world.GameWorld;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class EnemyAttackSystemTest {

    @Test
    void testSystemSkipsFaultyEnemyAndContinuesUpdatingOthers() {
        GameWorld world = new GameWorld();
        PlayerShip player = createPlayer();
        AtomicInteger healthyUpdates = new AtomicInteger();

        Enemy faultyEnemy = new TestEnemy(new EnemyBehavior() {
            @Override
            public void update(Enemy enemy, PlayerShip target, GameWorld targetWorld, double deltaSeconds) {
                throw new IllegalStateException("Simulated behavior failure");
            }

            @Override
            public boolean canShoot() {
                return false;
            }
        });
        Enemy healthyEnemy = new TestEnemy(new EnemyBehavior() {
            @Override
            public void update(Enemy enemy, PlayerShip target, GameWorld targetWorld, double deltaSeconds) {
                healthyUpdates.incrementAndGet();
            }

            @Override
            public boolean canShoot() {
                return true;
            }
        });

        world.addEntity(faultyEnemy);
        world.addEntity(healthyEnemy);

        EnemyAttackSystem system = new EnemyAttackSystem(world, player);
        Logger logger = Logger.getLogger(EnemyAttackSystem.class.getName());
        Level previousLevel = logger.getLevel();

        try {
            logger.setLevel(Level.OFF);
            assertDoesNotThrow(() -> system.update(0.016));
            assertEquals(1, healthyUpdates.get());
        } finally {
            logger.setLevel(previousLevel);
        }
    }

    private PlayerShip createPlayer() {
        return new PlayerShip(
                new Rectangle(60, 40),
                420,
                520,
                new PlayerStats(100),
                new PlayerMovement(250, 0, 900, 0, 600),
                new BasicWeapon()
        );
    }

    private static final class TestEnemy extends Enemy {

        private TestEnemy(EnemyBehavior behavior) {
            super(new Rectangle(32, 28), 100, 100, 10, 5, 50, behavior);
        }
    }
}
