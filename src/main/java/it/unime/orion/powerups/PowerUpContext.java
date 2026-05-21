package it.unime.orion.powerups;

import it.unime.orion.combat.Weapon;

/**
 * Narrow domain-facing context exposed to power-ups so they can apply effects
 * without depending on the full controller or runtime implementation.
 */
public interface PowerUpContext {

    void healPlayer(int amount);

    void activateShield();

    void activateTemporaryWeapon(Weapon weapon, double durationSeconds);

    boolean gainLife();
}
