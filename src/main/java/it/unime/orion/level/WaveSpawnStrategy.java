package it.unime.orion.level;

import it.unime.orion.world.GameWorld;

/**
 * Controls how one logical wave definition is translated into concrete enemy
 * placements inside the active world.
 */
public interface WaveSpawnStrategy {

    void spawnWave(GameWorld world, double worldWidth, int waveIndex, Wave wave, EnemyTuning enemyTuning);
}
