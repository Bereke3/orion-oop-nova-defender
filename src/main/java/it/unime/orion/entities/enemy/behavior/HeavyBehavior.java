package it.unime.orion.entities.enemy.behavior;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.level.EnemyTuning;

public final class HeavyBehavior extends AbstractEnemyShooterBehavior {

    public HeavyBehavior(double anchorX, double entryTargetY, EnemyTuning tuning) {
        super(anchorX, entryTargetY, tuning);
    }

    @Override
    protected double computeHorizontalVelocity(double moveTimer) {
        return Math.cos(moveTimer * 1.2) * 60;
    }

    @Override
    protected double computeVerticalSpeed(double moveTimer) {
        return Math.sin(moveTimer * 0.9) * 6;
    }

    @Override
    protected double getCooldownSeconds() {
        return 2.8;
    }

    @Override
    protected double getBulletWidth() {
        return 13;
    }

    @Override
    protected double getBulletHeight() {
        return 36;
    }

    @Override
    protected String getBulletAssetPath() {
        return GameAssets.getEnemyCrimsonBulletAssetPath();
    }

    @Override
    protected double getBulletOffsetX() {
        return 55;
    }

    @Override
    protected double getBulletOffsetY() {
        return 74;
    }
}
