package it.unime.orion.combat;

import javafx.scene.Node;

public final class PlayerBullet extends Projectile {

    private static final double LIFETIME_SECONDS = 2.4;

    public PlayerBullet(Node view, double x, double y) {
        super(view, x, y, 1, LIFETIME_SECONDS);
        setVelocity(0, -350);
    }
}
