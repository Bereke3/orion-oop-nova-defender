package it.unime.orion.game;

import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.world.GameWorld;

public interface GameplayRuntimeFactory {

    GameplayRuntime create(GameWorld world, GameSession session, PlayerShip player, double worldWidth);
}
