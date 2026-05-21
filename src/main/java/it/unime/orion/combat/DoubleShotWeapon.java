package it.unime.orion.combat;

import it.unime.orion.assets.GameAssets;

import java.util.List;

public final class DoubleShotWeapon implements Weapon {

    private static final double COOLDOWN_SECONDS = 0.45;

    @Override
    public List<Projectile> fire(double originX, double originY) {
        Projectile leftBullet = new PlayerBullet(GameAssets.createDoublePlayerBulletView(), originX - 18, originY);
        Projectile rightBullet = new PlayerBullet(GameAssets.createDoublePlayerBulletView(), originX + 18, originY);

        return List.of(leftBullet, rightBullet);
    }

    @Override
    public double getCooldownSeconds() {
        return COOLDOWN_SECONDS;
    }

    @Override
    public String getDisplayName() {
        return "Double Shot";
    }
}
