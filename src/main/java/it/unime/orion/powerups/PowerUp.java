package it.unime.orion.powerups;

import it.unime.orion.entities.GameEntity;
import javafx.scene.Node;

public abstract class PowerUp extends GameEntity {

    protected PowerUp(Node view, double x, double y) {
        super(view, x, y);
        setVelocity(0, 80);
    }

    public final double getSpawnX() {
        return getX();
    }

    public final double getSpawnY() {
        return getY();
    }

    public final String getDisplayName() {
        return getPowerUpType().getDisplayName();
    }

    public abstract PowerUpType getPowerUpType();

    public abstract void applyTo(PowerUpContext context);
}
