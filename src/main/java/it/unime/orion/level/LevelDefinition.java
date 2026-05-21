package it.unime.orion.level;

import it.unime.orion.entities.boss.BossTuning;
import it.unime.orion.errors.InvalidGameConfigurationException;

import java.util.List;
import java.util.Objects;

public final class LevelDefinition {

    private final int levelNumber;
    private final List<Wave> waves;
    private final EnemyTuning enemyTuning;
    private final BossTuning bossTuning;
    private final String bossAssetKey;
    private final WaveSpawnStrategy waveSpawnStrategy;
    private final LevelRuntimeTuning runtimeTuning;

    public LevelDefinition(int levelNumber,
                           List<Wave> waves,
                           EnemyTuning enemyTuning,
                           BossTuning bossTuning,
                           String bossAssetKey) {
        this(levelNumber, waves, enemyTuning, bossTuning, bossAssetKey, new TieredWaveSpawnStrategy(), LevelRuntimeTuning.defaultTuning());
    }

    public LevelDefinition(int levelNumber,
                           List<Wave> waves,
                           EnemyTuning enemyTuning,
                           BossTuning bossTuning,
                           String bossAssetKey,
                           WaveSpawnStrategy waveSpawnStrategy) {
        this(levelNumber, waves, enemyTuning, bossTuning, bossAssetKey, waveSpawnStrategy, LevelRuntimeTuning.defaultTuning());
    }

    public LevelDefinition(int levelNumber,
                           List<Wave> waves,
                           EnemyTuning enemyTuning,
                           BossTuning bossTuning,
                           String bossAssetKey,
                           WaveSpawnStrategy waveSpawnStrategy,
                           LevelRuntimeTuning runtimeTuning) {
        if (levelNumber <= 0) {
            throw new InvalidGameConfigurationException("levelNumber must be > 0");
        }
        Objects.requireNonNull(waves, "waves");
        if (waves.isEmpty()) {
            throw new InvalidGameConfigurationException("A level must contain at least one wave");
        }
        this.levelNumber = levelNumber;
        this.waves = List.copyOf(waves);
        this.enemyTuning = Objects.requireNonNull(enemyTuning, "enemyTuning");
        this.bossTuning = Objects.requireNonNull(bossTuning, "bossTuning");
        this.bossAssetKey = Objects.requireNonNull(bossAssetKey, "bossAssetKey");
        this.waveSpawnStrategy = Objects.requireNonNull(waveSpawnStrategy, "waveSpawnStrategy");
        this.runtimeTuning = Objects.requireNonNull(runtimeTuning, "runtimeTuning");
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public List<Wave> getWaves() {
        return waves;
    }

    public EnemyTuning getEnemyTuning() {
        return enemyTuning;
    }

    public BossTuning getBossTuning() {
        return bossTuning;
    }

    public String getBossAssetKey() {
        return bossAssetKey;
    }

    public WaveSpawnStrategy getWaveSpawnStrategy() {
        return waveSpawnStrategy;
    }

    public LevelRuntimeTuning getRuntimeTuning() {
        return runtimeTuning;
    }
}
