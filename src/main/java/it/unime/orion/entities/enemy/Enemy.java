package it.unime.orion.entities.enemy;

import it.unime.orion.entities.EntityType;
import it.unime.orion.entities.enemy.behavior.EnemyBehavior;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.entities.ship.Ship;
import it.unime.orion.world.GameWorld;
import javafx.scene.Node;

import java.util.Objects;

public abstract class Enemy extends Ship {

    private int hp;
    private final int maxHp;
    private final int scoreValue;
    private final EnemyBehavior behavior;

    protected Enemy(Node view,
                    double x,
                    double y,
                    int hp,
                    int contactDamage,
                    int scoreValue,
                    EnemyBehavior behavior) {
        super(EntityType.ENEMY, view, x, y, contactDamage);
        this.hp = hp;
        this.maxHp = hp;
        this.scoreValue = scoreValue;
        this.behavior = Objects.requireNonNull(behavior, "behavior");
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public final void updateBehavior(PlayerShip player, GameWorld world, double deltaSeconds) {
        behavior.update(this, player, world, deltaSeconds);
    }

    protected final EnemyBehavior getBehavior() {
        return behavior;
    }

    @Override
    protected void applyDamage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    public final boolean canShoot() {
        return behavior.canShoot();
    }

    public boolean isBoss() {
        return false;
    }
}
