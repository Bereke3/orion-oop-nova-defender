package it.unime.orion.entities.player;

public final class PlayerMovement {

    private final double speed;

    private final double minX;
    private final double maxX;

    private final double minY;
    private final double maxY;

    public PlayerMovement(double speed,
                          double minX, double maxX,
                          double minY, double maxY) {

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