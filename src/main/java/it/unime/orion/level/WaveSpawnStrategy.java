package it.unime.orion.level;

import it.unime.orion.world.GameWorld;

public interface WaveSpawnStrategy {

    void spawnWave(GameWorld world, double worldWidth, int waveIndex, Wave wave, EnemyTuning enemyTuning);
}
