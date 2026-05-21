package it.unime.orion.systems;

import it.unime.orion.errors.InvalidGameConfigurationException;
import it.unime.orion.powerups.DefaultPowerUpFactory;
import it.unime.orion.powerups.PowerUp;
import it.unime.orion.powerups.PowerUpFactory;
import it.unime.orion.powerups.PowerUpType;
import it.unime.orion.world.GameWorld;

import java.util.Objects;
import java.util.Random;

public final class PowerUpSpawnSystem {

    private final GameWorld world;
    private final Random random;

    private final double worldWidth;
    private final double spawnY;

    private double spawnIntervalSeconds;
    private int maxPowerUpsAlive;
    private final PowerUpFactory powerUpFactory;

    private double timer;

    public PowerUpSpawnSystem(GameWorld world, double worldWidth) {
        this(world, worldWidth, 60, 8.0, 2, new DefaultPowerUpFactory(), new Random());
    }

    public PowerUpSpawnSystem(GameWorld world,
                              double worldWidth,
                              double spawnY,
                              double spawnIntervalSeconds,
                              int maxPowerUpsAlive,
                              PowerUpFactory powerUpFactory,
                              Random random) {
        this.world = Objects.requireNonNull(world, "world");
        this.powerUpFactory = Objects.requireNonNull(powerUpFactory, "powerUpFactory");
        this.random = Objects.requireNonNull(random, "random");
        if (worldWidth <= 80) {
            throw new InvalidGameConfigurationException("worldWidth must be > 80");
        }
        if (spawnY < 0) {
            throw new InvalidGameConfigurationException("spawnY must be >= 0");
        }
        if (spawnIntervalSeconds <= 0) {
            throw new InvalidGameConfigurationException("spawnIntervalSeconds must be > 0");
        }
        if (maxPowerUpsAlive < 0) {
            throw new InvalidGameConfigurationException("maxPowerUpsAlive must be >= 0");
        }
        this.worldWidth = worldWidth;
        this.spawnY = spawnY;
        applySettings(spawnIntervalSeconds, maxPowerUpsAlive);
    }

    public void update(double dt) {
        timer += dt;

        if (timer < spawnIntervalSeconds) {
            return;
        }

        timer = 0.0;

        if (countAlivePowerUps() >= maxPowerUpsAlive) {
            return;
        }

        PowerUp powerUp = createRandomPowerUp();
        world.addEntity(powerUp);
    }

    public void applySettings(double spawnIntervalSeconds, int maxPowerUpsAlive) {
        if (spawnIntervalSeconds <= 0) {
            throw new InvalidGameConfigurationException("spawnIntervalSeconds must be > 0");
        }
        if (maxPowerUpsAlive < 0) {
            throw new InvalidGameConfigurationException("maxPowerUpsAlive must be >= 0");
        }
        this.spawnIntervalSeconds = spawnIntervalSeconds;
        this.maxPowerUpsAlive = maxPowerUpsAlive;
        this.timer = 0.0;
    }

    private int countAlivePowerUps() {
        int count = 0;

        for (var entity : world.getEntitiesView()) {
            if (entity instanceof PowerUp) {
                count++;
            }
        }

        return count;
    }

    private PowerUp createRandomPowerUp() {
        double x = 40 + random.nextDouble() * (worldWidth - 80);
        PowerUpType type = PowerUpType.randomType(random);
        return powerUpFactory.create(type, x, spawnY);
    }
}
