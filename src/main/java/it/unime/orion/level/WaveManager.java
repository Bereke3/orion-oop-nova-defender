package it.unime.orion.level;

import it.unime.orion.entities.GameEntity;
import it.unime.orion.entities.enemy.Enemy;
import it.unime.orion.world.GameWorld;

import java.util.List;
import java.util.Objects;

public final class WaveManager {

    private final GameWorld world;
    private final double worldWidth;
    private final List<Wave> waves;
    private final EnemyTuning enemyTuning;
    private final WaveSpawnStrategy waveSpawnStrategy;

    private int currentWaveIndex = -1;
    private boolean waveSpawned = false;

    public WaveManager(GameWorld world, double worldWidth, List<Wave> waves, EnemyTuning enemyTuning) {
        this(world, worldWidth, waves, enemyTuning, new TieredWaveSpawnStrategy());
    }

    public WaveManager(GameWorld world,
                       double worldWidth,
                       List<Wave> waves,
                       EnemyTuning enemyTuning,
                       WaveSpawnStrategy waveSpawnStrategy) {
        this.world = Objects.requireNonNull(world, "world");
        this.worldWidth = worldWidth;
        this.waves = Objects.requireNonNull(waves, "waves");
        this.enemyTuning = Objects.requireNonNull(enemyTuning, "enemyTuning");
        this.waveSpawnStrategy = Objects.requireNonNull(waveSpawnStrategy, "waveSpawnStrategy");
    }

    public int getCurrentWaveNumber() {
        return currentWaveIndex + 1;
    }

    public boolean hasMoreWaves() {
        return currentWaveIndex + 1 < waves.size();
    }

    public void startNextWave() {
        if (!hasMoreWaves()) {
            return;
        }

        currentWaveIndex++;
        waveSpawned = true;

        Wave wave = waves.get(currentWaveIndex);
        waveSpawnStrategy.spawnWave(world, worldWidth, currentWaveIndex, wave, enemyTuning);
    }

    public boolean isCurrentWaveCleared() {
        if (!waveSpawned) {
            return false;
        }

        for (GameEntity entity : world.getEntitiesView()) {
            if (entity instanceof Enemy) {
                return false;
            }
        }
        return true;
    }

}
