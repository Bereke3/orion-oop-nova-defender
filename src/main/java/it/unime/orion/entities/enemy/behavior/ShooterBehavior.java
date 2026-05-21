package it.unime.orion.entities.enemy.behavior;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.level.EnemyTuning;

public final class ShooterBehavior extends AbstractEnemyShooterBehavior {

    public ShooterBehavior(double anchorX, double entryTargetY, EnemyTuning tuning) {
        super(anchorX, entryTargetY, tuning);
    }

    @Override
    protected double computeHorizontalVelocity(double moveTimer) {
        return Math.sin(moveTimer * 1.9) * 90;
    }

    @Override
    protected double computeVerticalSpeed(double moveTimer) {
        return Math.sin(moveTimer * 1.4) * 10;
    }

    @Override
    protected double getCooldownSeconds() {
        return 1.8;
    }

    @Override
    protected double getBulletWidth() {
        return 12;
    }

    @Override
    protected double getBulletHeight() {
        return 34;
    }

    @Override
    protected String getBulletAssetPath() {
        return GameAssets.getEnemyRedBulletAssetPath();
    }

    @Override
    protected double getBulletOffsetX() {
        return 42;
    }

    @Override
    protected double getBulletOffsetY() {
        return 58;
    }
}
