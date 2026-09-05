package it.unime.orion.combat;

import it.unime.orion.entities.GameEntity;
import javafx.scene.Node;

public abstract class Projectile extends GameEntity {

    private final int damage;
    private final double lifetimeSeconds;
    private double timeLeft;

    protected Projectile(Node view,
                         double x,
                         double y,
                         int damage,
                         double lifetimeSeconds) {
        super(view, x, y);
        this.damage = damage;
        this.lifetimeSeconds = lifetimeSeconds;
        this.timeLeft = lifetimeSeconds;
    }

    public int getDamage() {
        return damage;
    }

    public double getLifetimeSeconds() {
        return lifetimeSeconds;
    }

    public double getTimeLeft() {
        return timeLeft;
    }

    public boolean isExpired() {
        return timeLeft <= 0;
    }

    @Override
    public void update(double deltaSeconds) {
        super.update(deltaSeconds);
        timeLeft -= deltaSeconds;
    }
}
