package it.unime.orion.combat;

import javafx.scene.Node;

public final class EnemyBullet extends Projectile {

    private static final double LIFETIME_SECONDS = 4.0;

    public EnemyBullet(Node view, double x, double y) {
        super(view, x, y, 10, LIFETIME_SECONDS);
        setVelocity(0, 220);
    }
}
