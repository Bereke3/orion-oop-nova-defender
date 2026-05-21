package it.unime.orion.entities.enemy.behavior;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.combat.EnemyBullet;
import it.unime.orion.entities.enemy.Enemy;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.level.EnemyTuning;
import it.unime.orion.world.GameWorld;

public abstract class AbstractEnemyShooterBehavior implements EnemyBehavior {

    private final double anchorX;
    private final double entryTargetY;
    private final double speedMultiplier;
    private final double fireRateMultiplier;

    private double shootCooldownLeft;
    private double moveTimer;
    private boolean enteredFormation;

    protected AbstractEnemyShooterBehavior(double anchorX, double entryTargetY, EnemyTuning tuning) {
        this.anchorX = anchorX;
        this.entryTargetY = entryTargetY;
        this.speedMultiplier = tuning.getSpeedMultiplier();
        this.fireRateMultiplier = tuning.getFireRateMultiplier();
        this.shootCooldownLeft = getAdjustedCooldown(getInitialCooldownSeconds());
    }

    @Override
    public final void update(Enemy enemy, PlayerShip player, GameWorld world, double deltaSeconds) {
        moveTimer += deltaSeconds;
        shootCooldownLeft -= deltaSeconds;

        if (!enteredFormation) {
            double xDiff = anchorX - enemy.getX();
            double yDiff = entryTargetY - enemy.getY();

            if (yDiff > 6) {
                enemy.setVelocity(clamp(xDiff * 2.2, -90 * speedMultiplier, 90 * speedMultiplier),
                        getEntrySpeed() * speedMultiplier);
                return;
            }

            enteredFormation = true;
            shootCooldownLeft = Math.min(shootCooldownLeft, getAdjustedCooldown(getCooldownSeconds()));
        }

        enemy.setVelocity(
                computeHorizontalVelocity(moveTimer) * speedMultiplier,
                computeVerticalSpeed(moveTimer) * speedMultiplier
        );

        if (!enemy.isAlive() || shootCooldownLeft > 0) {
            return;
        }

        fire(enemy, world);
        shootCooldownLeft = getAdjustedCooldown(getCooldownSeconds());
    }

    @Override
    public final boolean canShoot() {
        return true;
    }

    protected double getInitialCooldownSeconds() {
        return getCooldownSeconds();
    }

    protected double getEntrySpeed() {
        return 110;
    }

    protected abstract double computeHorizontalVelocity(double moveTimer);

    protected double computeVerticalSpeed(double moveTimer) {
        return 0;
    }

    protected abstract double getCooldownSeconds();

    protected void fire(Enemy enemy, GameWorld world) {
        spawnBullet(
                world,
                enemy,
                getBulletWidth(),
                getBulletHeight(),
                getBulletAssetPath(),
                getBulletOffsetX(),
                getBulletOffsetY()
        );
    }

    protected abstract double getBulletWidth();

    protected abstract double getBulletHeight();

    protected abstract String getBulletAssetPath();

    protected abstract double getBulletOffsetX();

    protected abstract double getBulletOffsetY();

    protected final void spawnBullet(GameWorld world,
                                     Enemy enemy,
                                     double bulletWidth,
                                     double bulletHeight,
                                     String assetPath,
                                     double offsetX,
                                     double offsetY) {
        world.addEntity(new EnemyBullet(
                GameAssets.createSpriteView(assetPath, bulletWidth, bulletHeight),
                enemy.getX() + offsetX,
                enemy.getY() + offsetY
        ));
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double getAdjustedCooldown(double baseCooldown) {
        return baseCooldown / fireRateMultiplier;
    }
}
