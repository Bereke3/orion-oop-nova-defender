package it.unime.orion.entities.player;

import it.unime.orion.entities.Damageable;
import it.unime.orion.entities.EntityType;
import it.unime.orion.entities.GameEntity;
import javafx.scene.Node;

import java.util.Objects;

public final class PlayerShip extends GameEntity implements Damageable {

    private final PlayerStats stats;
    private final PlayerMovement movement;

    public PlayerShip(Node view,
                      double x,
                      double y,
                      PlayerStats stats,
                      PlayerMovement movement) {

        super(EntityType.PLAYER, view, x, y);

        this.stats = Objects.requireNonNull(stats, "stats");
        this.movement = Objects.requireNonNull(movement, "movement");
    }

    public PlayerStats getStats() {
        return stats;
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        x = movement.clampX(x);
        y = movement.clampY(y);

        syncView();
    }

    @Override
    public void takeDamage(int amount) {
        stats.damage(amount);
    }

    @Override
    public boolean isAlive() {
        return stats.isAlive();
    }
}