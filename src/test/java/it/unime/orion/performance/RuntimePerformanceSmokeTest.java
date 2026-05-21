package it.unime.orion.performance;

import it.unime.orion.combat.BasicWeapon;
import it.unime.orion.combat.EnemyBullet;
import it.unime.orion.combat.PlayerBullet;
import it.unime.orion.entities.GameEntity;
import it.unime.orion.entities.enemy.EnemyType2Shooter;
import it.unime.orion.entities.enemy.EnemyType3Heavy;
import it.unime.orion.entities.enemy.EnemyType1Swarm;
import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.entities.player.PlayerStats;
import it.unime.orion.events.DamageEvent;
import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.events.EventBus;
import it.unime.orion.events.PowerUpCollectedEvent;
import it.unime.orion.level.EnemyTuning;
import it.unime.orion.powerups.HealPowerUp;
import it.unime.orion.powerups.PowerUpContext;
import it.unime.orion.systems.CollisionSystem;
import it.unime.orion.systems.EnemyAttackSystem;
import it.unime.orion.systems.VisualEffectSystem;
import it.unime.orion.world.GameWorld;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class RuntimePerformanceSmokeTest {

    private static final double WORLD_WIDTH = 900;
    private static final double WORLD_HEIGHT = 600;
    private static final int WARMUP_ITERATIONS = 60;
    private static final int MEASURED_ITERATIONS = 240;
    private static final int STABILITY_ITERATIONS = 1000;
    private static final int TOTAL_ENTITIES = 161;
    private static final double MAX_AVERAGE_COLLISION_MS = 6.0;
    private static final double MAX_AVERAGE_SIMULATION_FRAME_MS = 8.0;
    private static final double FRAME_DT = 0.016;
    private static final Path METRICS_PATH = Path.of("target", "performance-metrics.txt");

    @Test
    void testCollisionSystemPerformanceAndStabilityUnderLoad() throws IOException {
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            createScenario().collisionSystem().handleCollisions(WORLD_HEIGHT);
        }

        Scenario measured = createScenario();
        long startedAt = System.nanoTime();
        for (int i = 0; i < MEASURED_ITERATIONS; i++) {
            measured.collisionSystem().handleCollisions(WORLD_HEIGHT);
        }
        long elapsedNanos = System.nanoTime() - startedAt;
        double averageMillis = elapsedNanos / 1_000_000.0 / MEASURED_ITERATIONS;

        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            Scenario warmupScenario = createScenario();
            try {
                warmupScenario.runSimulationFrame(FRAME_DT);
            } finally {
                warmupScenario.close();
            }
        }

        long simulationStartedAt = System.nanoTime();
        for (int i = 0; i < MEASURED_ITERATIONS; i++) {
            Scenario scenario = createScenario();
            try {
                scenario.runSimulationFrame(FRAME_DT);
            } finally {
                scenario.close();
            }
        }
        long simulationElapsedNanos = System.nanoTime() - simulationStartedAt;
        double averageSimulationFrameMillis = simulationElapsedNanos / 1_000_000.0 / MEASURED_ITERATIONS;

        int successfulRuns = 0;
        for (int i = 0; i < STABILITY_ITERATIONS; i++) {
            createScenario().collisionSystem().handleCollisions(WORLD_HEIGHT);
            successfulRuns++;
        }

        String metrics = """
                Collision benchmark
                -------------------
                Scenario entities: %d
                Warm-up iterations: %d
                Measured iterations: %d
                Average collision frame time (ms): %.4f
                Average simulation frame time (ms): %.4f
                Stability runs completed: %d / %d
                """.formatted(TOTAL_ENTITIES,
                WARMUP_ITERATIONS,
                MEASURED_ITERATIONS,
                averageMillis,
                averageSimulationFrameMillis,
                successfulRuns,
                STABILITY_ITERATIONS);
        Files.writeString(METRICS_PATH, metrics);
        System.out.println(metrics);

        assertEquals(STABILITY_ITERATIONS, successfulRuns, "All stability runs should complete");
        assertTrue(averageMillis < MAX_AVERAGE_COLLISION_MS,
                String.format(Locale.ROOT,
                        "Average collision frame time %.4f ms exceeded %.2f ms",
                        averageMillis,
                        MAX_AVERAGE_COLLISION_MS));
        assertTrue(averageSimulationFrameMillis < MAX_AVERAGE_SIMULATION_FRAME_MS,
                String.format(Locale.ROOT,
                        "Average simulation frame time %.4f ms exceeded %.2f ms",
                        averageSimulationFrameMillis,
                        MAX_AVERAGE_SIMULATION_FRAME_MS));
    }

    private Scenario createScenario() {
        GameWorld world = new GameWorld();
        PlayerShip player = new PlayerShip(
                new Rectangle(60, 40),
                420,
                520,
                new PlayerStats(10),
                new PlayerMovement(250, 0, WORLD_WIDTH - 60, 0, WORLD_HEIGHT - 40),
                new BasicWeapon(),
                0
        );
        world.addEntity(player);

        EnemyTuning tuning = new EnemyTuning(1.0, 1.0);
        for (int i = 0; i < 20; i++) {
            double x = 20 + (i % 10) * 80;
            double y = 40 + (i / 10) * 70;
            world.addEntity(new EnemyType1Swarm(new Rectangle(40, 28), x, y, y, tuning));
        }

        for (int i = 0; i < 10; i++) {
            double x = 40 + (i % 5) * 150;
            double y = 80 + (i / 5) * 60;
            world.addEntity(new EnemyType2Shooter(new Rectangle(48, 34), x, y, y, tuning));
        }

        for (int i = 0; i < 10; i++) {
            double x = 60 + (i % 5) * 150;
            double y = 150 + (i / 5) * 70;
            world.addEntity(new EnemyType3Heavy(new Rectangle(56, 38), x, y, y, tuning));
        }

        for (int i = 0; i < 80; i++) {
            double x = 10 + (i % 20) * 42;
            double y = 260 + (i / 20) * 22;
            world.addEntity(new PlayerBullet(new Rectangle(6, 14), x, y));
        }

        for (int i = 0; i < 30; i++) {
            double x = 30 + (i % 10) * 75;
            double y = 120 + (i / 10) * 55;
            world.addEntity(new EnemyBullet(new Rectangle(6, 14), x, y));
        }

        for (int i = 0; i < 10; i++) {
            double x = 40 + i * 70;
            double y = 430;
            world.addEntity(new HealPowerUp(new Rectangle(20, 20), x, y, 2));
        }

        EventBus<DamageEvent> damageBus = new EventBus<>();
        EventBus<EnemyDestroyedEvent> enemyDestroyedBus = new EventBus<>();
        EventBus<PowerUpCollectedEvent> powerUpCollectedBus = new EventBus<>();

        CollisionSystem collisionSystem = new CollisionSystem(
                world,
                player,
                damageBus,
                enemyDestroyedBus,
                powerUpCollectedBus,
                new NoOpPowerUpContext()
        );
        EnemyAttackSystem enemyAttackSystem = new EnemyAttackSystem(world, player);
        VisualEffectSystem visualEffectSystem = new VisualEffectSystem(world, enemyDestroyedBus, powerUpCollectedBus);
        return new Scenario(world, collisionSystem, enemyAttackSystem, visualEffectSystem);
    }

    private record Scenario(GameWorld world,
                            CollisionSystem collisionSystem,
                            EnemyAttackSystem enemyAttackSystem,
                            VisualEffectSystem visualEffectSystem) implements AutoCloseable {

        void runSimulationFrame(double dt) {
            enemyAttackSystem.update(dt);
            for (GameEntity entity : new ArrayList<>(world.getEntitiesView())) {
                entity.update(dt);
            }
            collisionSystem.handleCollisions(WORLD_HEIGHT);
            visualEffectSystem.update();
        }

        @Override
        public void close() {
            visualEffectSystem.close();
        }
    }

    private static final class NoOpPowerUpContext implements PowerUpContext {

        @Override
        public void healPlayer(int amount) {
        }

        @Override
        public void activateShield() {
        }

        @Override
        public void activateTemporaryWeapon(it.unime.orion.combat.Weapon weapon, double durationSeconds) {
        }

        @Override
        public boolean gainLife() {
            return false;
        }
    }
}
