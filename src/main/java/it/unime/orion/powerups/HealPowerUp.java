package it.unime.orion.powerups;

import javafx.scene.Node;

public final class HealPowerUp extends PowerUp {

    private final int healAmount;

    public HealPowerUp(Node view, double x, double y, int healAmount) {
        super(view, x, y);
        this.healAmount = healAmount;
    }

    @Override
    public PowerUpType getPowerUpType() {
        return PowerUpType.HEAL;
    }

    @Override
    public void applyTo(PowerUpContext context) {
        context.healPlayer(healAmount);
    }
}
