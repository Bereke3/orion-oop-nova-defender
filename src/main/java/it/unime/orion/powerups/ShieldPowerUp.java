package it.unime.orion.powerups;

import javafx.scene.Node;

public final class ShieldPowerUp extends PowerUp {

    public ShieldPowerUp(Node view, double x, double y) {
        super(view, x, y);
    }

    @Override
    public PowerUpType getPowerUpType() {
        return PowerUpType.SHIELD;
    }

    @Override
    public void applyTo(PowerUpContext context) {
        context.activateShield();
    }
}
