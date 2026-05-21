package it.unime.orion.powerups;

import javafx.scene.Node;

public final class ExtraLifePowerUp extends PowerUp {

    public ExtraLifePowerUp(Node view, double x, double y) {
        super(view, x, y);
    }

    @Override
    public PowerUpType getPowerUpType() {
        return PowerUpType.EXTRA_LIFE;
    }

    @Override
    public void applyTo(PowerUpContext context) {
        context.gainLife();
    }
}
