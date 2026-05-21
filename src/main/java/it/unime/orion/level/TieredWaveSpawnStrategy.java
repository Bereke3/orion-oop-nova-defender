package it.unime.orion.level;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.entities.enemy.Enemy;
import it.unime.orion.entities.enemy.EnemyType1Swarm;
import it.unime.orion.entities.enemy.EnemyType2Shooter;
import it.unime.orion.entities.enemy.EnemyType3Heavy;
import it.unime.orion.world.GameWorld;

import java.util.Objects;

public final class TieredWaveSpawnStrategy implements WaveSpawnStrategy {

    @Override
    public void spawnWave(GameWorld world, double worldWidth, int waveIndex, Wave wave, EnemyTuning enemyTuning) {
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(wave, "wave");
        Objects.requireNonNull(enemyTuning, "enemyTuning");

        spawnSwarmEnemies(world, worldWidth, waveIndex, wave.getSwarmCount(), enemyTuning);
        spawnShooterEnemies(world, worldWidth, waveIndex, wave.getShooterCount(), enemyTuning);
        spawnHeavyEnemies(world, worldWidth, waveIndex, wave.getHeavyCount(), enemyTuning);
    }

    private void spawnSwarmEnemies(GameWorld world, double worldWidth, int waveIndex, int count, EnemyTuning tuning) {
        int columns = Math.min(5, count);
        double[] xPositions = buildRowXPositions(worldWidth, columns, 80, GameAssets.SWARM_WIDTH);

        for (int i = 0; i < count; i++) {
            int row = i / columns;
            int column = i % columns;
            double spawnX = xPositions[column] + (row % 2 == 0 ? 0 : 24);
            double spawnY = -GameAssets.SWARM_HEIGHT - row * 78 - waveIndex * 28;
            double entryTargetY = 62 + row * 48;
            Enemy enemy = new EnemyType1Swarm(
                    GameAssets.createSwarmEnemyView(),
                    spawnX,
                    spawnY,
                    entryTargetY,
                    tuning
            );
            world.addEntity(enemy);
        }
    }

    private void spawnShooterEnemies(GameWorld world, double worldWidth, int waveIndex, int count, EnemyTuning tuning) {
        int columns = Math.min(4, count);
        double[] xPositions = buildRowXPositions(worldWidth, columns, 120, GameAssets.SHOOTER_WIDTH);

        for (int i = 0; i < count; i++) {
            int row = i / columns;
            int column = i % columns;
            double spawnX = xPositions[column];
            double spawnY = -GameAssets.SHOOTER_HEIGHT - row * 102 - waveIndex * 34;
            double entryTargetY = 150 + row * 58;
            Enemy enemy = new EnemyType2Shooter(
                    GameAssets.createShooterEnemyView(),
                    spawnX,
                    spawnY,
                    entryTargetY,
                    tuning
            );
            world.addEntity(enemy);
        }
    }

    private void spawnHeavyEnemies(GameWorld world, double worldWidth, int waveIndex, int count, EnemyTuning tuning) {
        int columns = Math.min(3, count);
        double[] xPositions = buildRowXPositions(worldWidth, columns, 170, GameAssets.HEAVY_WIDTH);

        for (int i = 0; i < count; i++) {
            int row = i / columns;
            int column = i % columns;
            double spawnX = xPositions[column];
            double spawnY = -GameAssets.HEAVY_HEIGHT - row * 120 - waveIndex * 42;
            double entryTargetY = 240 + row * 66;
            Enemy enemy = new EnemyType3Heavy(
                    GameAssets.createHeavyEnemyView(),
                    spawnX,
                    spawnY,
                    entryTargetY,
                    tuning
            );
            world.addEntity(enemy);
        }
    }

    private double[] buildRowXPositions(double worldWidth, int count, double sideMargin, double spriteWidth) {
        if (count == 1) {
            return new double[]{(worldWidth - spriteWidth) / 2.0};
        }

        double minX = sideMargin;
        double maxX = worldWidth - sideMargin;
        double step = (maxX - minX) / (count - 1);
        double[] positions = new double[count];

        for (int i = 0; i < count; i++) {
            positions[i] = minX + step * i;
        }

        return positions;
    }
}
