package it.unime.orion.entities.player;

import it.unime.orion.errors.InvalidGameConfigurationException;

public final class PlayerMovement {

    private final double speed;

    private final double minX;
    private final double maxX;

    private final double minY;
    private final double maxY;

    public PlayerMovement(double speed,
                          double minX, double maxX,
                          double minY, double maxY) {
        if (speed <= 0) {
            throw new InvalidGameConfigurationException("Player speed must be > 0");
        }
        if (minX > maxX) {
            throw new InvalidGameConfigurationException("minX must be <= maxX");
        }
        if (minY > maxY) {
            throw new InvalidGameConfigurationException("minY must be <= maxY");
        }

        this.speed = speed;
        this.minX = minX;
        this.maxX = maxX;

        this.minY = minY;
        this.maxY = maxY;
    }

    public double getSpeed() {
        return speed;
    }

    public double clampX(double x) {
        return Math.max(minX, Math.min(maxX, x));
    }

    public double clampY(double y) {
        return Math.max(minY, Math.min(maxY, y));
    }
}
