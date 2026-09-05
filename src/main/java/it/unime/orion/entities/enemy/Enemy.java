package it.unime.orion.entities.enemy;

import it.unime.orion.entities.enemy.behavior.EnemyBehavior;
import it.unime.orion.entities.enemy.behavior.HeavyBehavior;
import it.unime.orion.entities.enemy.behavior.ShooterBehavior;
import it.unime.orion.entities.enemy.behavior.SwarmBehavior;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.entities.ship.Ship;
import it.unime.orion.level.EnemyTuning;
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
        super(view, x, y, contactDamage);
        this.hp = hp;
        this.maxHp = hp;
        this.scoreValue = scoreValue;
        this.behavior = Objects.requireNonNull(behavior, "behavior");
    }

    public static Enemy createSwarm(Node view, double x, double y, double entryTargetY, EnemyTuning tuning) {
        Objects.requireNonNull(tuning, "tuning");
        return create(view, x, y, 1, 10, 100, new SwarmBehavior(x, entryTargetY, tuning.getSpeedMultiplier()));
    }

    public static Enemy createShooter(Node view, double x, double y, double entryTargetY, EnemyTuning tuning) {
        return create(view, x, y, 2, 10, 175, new ShooterBehavior(x, entryTargetY, Objects.requireNonNull(tuning, "tuning")));
    }

    public static Enemy createHeavy(Node view, double x, double y, double entryTargetY, EnemyTuning tuning) {
        return create(view, x, y, 3, 15, 300, new HeavyBehavior(x, entryTargetY, Objects.requireNonNull(tuning, "tuning")));
    }

    private static Enemy create(Node view,
                                double x,
                                double y,
                                int hp,
                                int contactDamage,
                                int scoreValue,
                                EnemyBehavior behavior) {
        return new Enemy(view, x, y, hp, contactDamage, scoreValue, behavior) {
        };
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
