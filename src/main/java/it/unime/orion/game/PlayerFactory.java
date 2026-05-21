package it.unime.orion.game;

import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;

public interface PlayerFactory {

    PlayerShip create(PlayerMovement movement, double worldWidth, double worldHeight);
}
