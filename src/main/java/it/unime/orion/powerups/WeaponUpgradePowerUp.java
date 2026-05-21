package it.unime.orion.powerups;

import it.unime.orion.combat.Weapon;
import javafx.scene.Node;

import java.util.Objects;
import java.util.function.Supplier;

public final class WeaponUpgradePowerUp extends PowerUp {

    private final double durationSeconds;
    private final Supplier<? extends Weapon> weaponFactory;

    public WeaponUpgradePowerUp(Node view,
                                double x,
                                double y,
                                Supplier<? extends Weapon> weaponFactory,
                                double durationSeconds) {
        super(view, x, y);
        this.weaponFactory = Objects.requireNonNull(weaponFactory, "weaponFactory");
        this.durationSeconds = durationSeconds;
    }

    @Override
    public PowerUpType getPowerUpType() {
        return PowerUpType.WEAPON_UPGRADE;
    }

    @Override
    public void applyTo(PowerUpContext context) {
        context.activateTemporaryWeapon(weaponFactory.get(), durationSeconds);
    }
}
