package it.unime.orion.events;

import it.unime.orion.powerups.PowerUpType;

import java.util.Objects;

public final class PowerUpCollectedEvent {

    private final String powerUpId;
    private final PowerUpType powerUpType;
    private final double x;
    private final double y;

    public PowerUpCollectedEvent(String powerUpId, PowerUpType powerUpType, double x, double y) {
        this.powerUpId = Objects.requireNonNull(powerUpId, "powerUpId");
        this.powerUpType = Objects.requireNonNull(powerUpType, "powerUpType");
        this.x = x;
        this.y = y;
    }

    public String getPowerUpId() {
        return powerUpId;
    }

    public PowerUpType getPowerUpType() {
        return powerUpType;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
