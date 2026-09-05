package it.unime.orion.level;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.entities.enemy.Enemy;
import it.unime.orion.world.GameWorld;

import java.util.Objects;

public final class AlternatingFlankWaveSpawnStrategy implements WaveSpawnStrategy {

    @Override
    public void spawnWave(GameWorld world, double worldWidth, int waveIndex, Wave wave, EnemyTuning enemyTuning) {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(wave, "wave");
        Objects.requireNonNull(enemyTuning, "enemyTuning");

        spawnSwarmFlanks(world, worldWidth, waveIndex, wave.getSwarmCount(), enemyTuning);
        spawnShooterArc(world, worldWidth, waveIndex, wave.getShooterCount(), enemyTuning);
        spawnHeavyCenterline(world, worldWidth, waveIndex, wave.getHeavyCount(), enemyTuning);
    }

    private void spawnSwarmFlanks(GameWorld world, double worldWidth, int waveIndex, int count, EnemyTuning tuning) {
        for (int i = 0; i < count; i++) {
            boolean leftSide = i % 2 == 0;
            int row = i / 2;
            double spawnX = leftSide ? 70 + row * 18 : worldWidth - GameAssets.SWARM_WIDTH - 70 - row * 18;
            double spawnY = -GameAssets.SWARM_HEIGHT - row * 64 - waveIndex * 20;
            double entryTargetY = 80 + row * 38;
            Enemy enemy = Enemy.createSwarm(
                    GameAssets.createSwarmEnemyView(),
                    spawnX,
                    spawnY,
                    entryTargetY,
                    tuning
            );
            world.addEntity(enemy);
        }
    }

    private void spawnShooterArc(GameWorld world, double worldWidth, int waveIndex, int count, EnemyTuning tuning) {
        if (count <= 0) {
            return;
        }

        double centerX = worldWidth / 2.0;
        for (int i = 0; i < count; i++) {
            double offset = (i - (count - 1) / 2.0) * 118;
            double spawnX = centerX + offset - GameAssets.SHOOTER_WIDTH / 2.0;
            double spawnY = -GameAssets.SHOOTER_HEIGHT - i * 86 - waveIndex * 28;
            double entryTargetY = 150 + Math.abs(offset) * 0.12;
            Enemy enemy = Enemy.createShooter(
                    GameAssets.createShooterEnemyView(),
                    spawnX,
                    spawnY,
                    entryTargetY,
                    tuning
            );
            world.addEntity(enemy);
        }
    }

    private void spawnHeavyCenterline(GameWorld world, double worldWidth, int waveIndex, int count, EnemyTuning tuning) {
        if (count <= 0) {
            return;
        }

        double centerX = (worldWidth - GameAssets.HEAVY_WIDTH) / 2.0;
        for (int i = 0; i < count; i++) {
            double horizontalOffset = (i % 2 == 0 ? -1 : 1) * (28 + (i / 2) * 68);
            double spawnX = centerX + horizontalOffset;
            double spawnY = -GameAssets.HEAVY_HEIGHT - i * 118 - waveIndex * 36;
            double entryTargetY = 240 + i * 40;
            Enemy enemy = Enemy.createHeavy(
                    GameAssets.createHeavyEnemyView(),
                    spawnX,
                    spawnY,
                    entryTargetY,
                    tuning
            );
            world.addEntity(enemy);
        }
    }
}
