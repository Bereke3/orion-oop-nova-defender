package it.unime.orion.powerups;

import it.unime.orion.combat.Weapon;

public interface PowerUpContext {

    void healPlayer(int amount);

    void activateShield();

    void activateTemporaryWeapon(Weapon weapon, double durationSeconds);

    boolean gainLife();
}
