package it.unime.orion.game;

import it.unime.orion.combat.Weapon;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.powerups.PowerUpContext;

import java.util.Objects;

public final class GamePowerUpContext implements PowerUpContext {

    private final PlayerShip player;
    private final GameSession session;

    public GamePowerUpContext(PlayerShip player, GameSession session) {
        this.player = Objects.requireNonNull(player, "player");
        this.session = Objects.requireNonNull(session, "session");
    }

    @Override
    public void healPlayer(int amount) {
        player.getStats().heal(amount);
    }

    @Override
    public void activateShield() {
        player.getStats().activateShield();
    }

    @Override
    public void activateTemporaryWeapon(Weapon weapon, double durationSeconds) {
        player.activateTemporaryWeapon(weapon, durationSeconds);
    }

    @Override
    public boolean gainLife() {
        return session.gainLife();
    }
}
