package it.unime.orion.combat;

import it.unime.orion.assets.GameAssets;

import java.util.List;

public final class BasicWeapon implements Weapon {

    private static final double COOLDOWN_SECONDS = 0.30;

    @Override
    public List<Projectile> fire(double originX, double originY) {
        Projectile bullet = new PlayerBullet(GameAssets.createBasicPlayerBulletView(), originX, originY);
        return List.of(bullet);
    }

    @Override
    public double getCooldownSeconds() {
        return COOLDOWN_SECONDS;
    }

    @Override
    public String getDisplayName() {
        return "Basic Weapon";
    }
}
