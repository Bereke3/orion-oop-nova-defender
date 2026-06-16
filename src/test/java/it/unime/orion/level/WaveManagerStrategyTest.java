package it.unime.orion.level;

import it.unime.orion.world.GameWorld;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class WaveManagerStrategyTest {

    @Test
    void testWaveManagerDelegatesSpawningToConfiguredStrategy() {
        GameWorld world = new GameWorld();
        SpyWaveSpawnStrategy strategy = new SpyWaveSpawnStrategy();
        WaveManager waveManager = new WaveManager(
                world,
                900,
                List.of(new Wave(2, 1, 0)),
                new EnemyTuning(1.0, 1.0),
                strategy
        );

        waveManager.startNextWave();

        assertEquals(1, strategy.invocationCount);
        assertEquals(0, strategy.lastWaveIndex);
        assertEquals(2, strategy.lastWave.getSwarmCount());
        assertSame(world, strategy.lastWorld);
    }

    @Test
    void testRealStrategiesProduceDifferentLayoutsForSameWave() {
        Wave wave = new Wave(4, 2, 1);
        EnemyTuning tuning = new EnemyTuning(1.0, 1.0);

        List<String> tieredLayout = spawnLayout(new TieredWaveSpawnStrategy(), wave, tuning);
        List<String> alternatingLayout = spawnLayout(new AlternatingFlankWaveSpawnStrategy(), wave, tuning);

        assertEquals(7, tieredLayout.size());
        assertEquals(tieredLayout.size(), alternatingLayout.size());
        assertNotEquals(tieredLayout, alternatingLayout);
    }

    private List<String> spawnLayout(WaveSpawnStrategy strategy, Wave wave, EnemyTuning tuning) {
        GameWorld world = new GameWorld();
        WaveManager waveManager = new WaveManager(world, 900, List.of(wave), tuning, strategy);
        waveManager.startNextWave();

        return world.getEntitiesView().stream()
                .map(entity -> entity.getClass().getSimpleName()
                        + "@"
                        + roundToTenth(entity.getX())
                        + ","
                        + roundToTenth(entity.getY()))
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    private double roundToTenth(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static final class SpyWaveSpawnStrategy implements WaveSpawnStrategy {
        private int invocationCount;
        private int lastWaveIndex = -1;
        private Wave lastWave;
        private GameWorld lastWorld;

        @Override
        public void spawnWave(GameWorld world, double worldWidth, int waveIndex, Wave wave, EnemyTuning enemyTuning) {
            invocationCount++;
            lastWaveIndex = waveIndex;
            lastWave = wave;
            lastWorld = world;
        }
    }
}
