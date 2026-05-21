package it.unime.orion.powerups;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.combat.DoubleShotWeapon;

public final class DefaultPowerUpFactory implements PowerUpFactory {

    @Override
    public PowerUp create(PowerUpType type, double x, double y) {
        return switch (type) {
            case HEAL -> new HealPowerUp(GameAssets.createPowerUpView(type), x, y, 25);
            case SHIELD -> new ShieldPowerUp(GameAssets.createPowerUpView(type), x, y);
            case WEAPON_UPGRADE -> new WeaponUpgradePowerUp(GameAssets.createPowerUpView(type), x, y, DoubleShotWeapon::new, 8.0);
            case EXTRA_LIFE -> new ExtraLifePowerUp(GameAssets.createPowerUpView(type), x, y);
        };
    }
}
