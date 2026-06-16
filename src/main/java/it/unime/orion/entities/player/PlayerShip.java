package it.unime.orion.entities.player;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.combat.Projectile;
import it.unime.orion.combat.Weapon;
import it.unime.orion.entities.EntityType;
import it.unime.orion.entities.ship.Ship;
import javafx.scene.Node;

import java.util.List;
import java.util.Objects;

public final class PlayerShip extends Ship {

    private static final double DEFAULT_RESPAWN_INVULNERABILITY_SECONDS = 1.8;

    private final PlayerStats stats;
    private final PlayerMovement movement;
    private final double spawnX;
    private final double spawnY;
    private final double respawnInvulnerabilitySeconds;

    private final Weapon defaultWeapon;
    private Weapon weapon;

    private double weaponCooldownLeft;
    private double weaponUpgradeTimeLeft;

    public PlayerShip(Node view,
                      double x,
                      double y,
                      PlayerStats stats,
                      PlayerMovement movement,
                      Weapon weapon) {
        this(view, x, y, stats, movement, weapon, DEFAULT_RESPAWN_INVULNERABILITY_SECONDS);
    }

    public PlayerShip(Node view,
                      double x,
                      double y,
                      PlayerStats stats,
                      PlayerMovement movement,
                      Weapon weapon,
                      double respawnInvulnerabilitySeconds) {
        super(EntityType.PLAYER, view, x, y, 0);

        this.stats = Objects.requireNonNull(stats, "stats");
        this.movement = Objects.requireNonNull(movement, "movement");
        this.weapon = Objects.requireNonNull(weapon, "weapon");
        this.defaultWeapon = weapon;
        if (respawnInvulnerabilitySeconds < 0) {
            throw new IllegalArgumentException("respawnInvulnerabilitySeconds must be >= 0");
        }
        this.spawnX = x;
        this.spawnY = y;
        this.respawnInvulnerabilitySeconds = respawnInvulnerabilitySeconds;
    }

    public boolean canFire() {
        return weaponCooldownLeft <= 0;
    }

    public double getWeaponCooldownLeft() {
        return weaponCooldownLeft;
    }

    public void activateTemporaryWeapon(Weapon weapon, double durationSeconds) {
        this.weapon = Objects.requireNonNull(weapon, "weapon");
        this.weaponUpgradeTimeLeft = Math.max(0, durationSeconds);
    }

    public boolean hasTemporaryWeaponUpgrade() {
        return weaponUpgradeTimeLeft > 0;
    }

    public double getWeaponUpgradeTimeLeft() {
        return weaponUpgradeTimeLeft;
    }

    public double getRespawnInvulnerabilitySeconds() {
        return respawnInvulnerabilitySeconds;
    }

    public void heal(int amount) {
        stats.heal(amount);
    }

    public void activateShield() {
        stats.activateShield();
    }

    public boolean hasShield() {
        return stats.hasShield();
    }

    public boolean isInvulnerable() {
        return stats.isInvulnerable();
    }

    public String getCurrentWeaponDisplayName() {
        return weapon.getDisplayName();
    }

    public void respawn() {
        setPosition(spawnX, spawnY);
        setVelocity(0, 0);
        weapon = defaultWeapon;
        weaponCooldownLeft = 0;
        weaponUpgradeTimeLeft = 0;
        stats.deactivateShield();
        stats.restoreFullHealth();
        stats.activateInvulnerability(respawnInvulnerabilitySeconds);
        applyInvulnerabilityVisual();
    }

    public List<Projectile> fire() {
        if (!canFire()) {
            return List.of();
        }

        double originX = getX() + (getViewWidth() / 2.0)
                - (GameAssets.PLAYER_BULLET_WIDTH / 2.0);
        // Spawn bullets fully above the ship nose so they do not visually cover the player sprite.
        double originY = getY() - GameAssets.PLAYER_BULLET_HEIGHT - 4;
        weaponCooldownLeft = weapon.getCooldownSeconds();
        return weapon.fire(originX, originY);
    }

    @Override
    public int getHp() {
        return stats.getHp();
    }

    @Override
    public int getMaxHp() {
        return stats.getMaxHp();
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        setPosition(movement.clampX(getX()), movement.clampY(getY()));
        stats.update(dt);
        applyInvulnerabilityVisual();

        if (weaponCooldownLeft > 0) {
            weaponCooldownLeft = Math.max(0, weaponCooldownLeft - dt);
        }

        if (weaponUpgradeTimeLeft > 0) {
            weaponUpgradeTimeLeft -= dt;
            if (weaponUpgradeTimeLeft <= 0) {
                weaponUpgradeTimeLeft = 0;
                weapon = defaultWeapon;
            }
        }
    }

    @Override
    protected void applyDamage(int amount) {
        stats.damage(amount);
    }

    private void applyInvulnerabilityVisual() {
        if (stats.isInvulnerable()) {
            double blinkPhase = stats.getInvulnerabilitySecondsLeft() * 10.0;
            setViewOpacity(((int) blinkPhase % 2 == 0) ? 0.45 : 1.0);
        } else {
            setViewOpacity(1.0);
        }
    }
}
