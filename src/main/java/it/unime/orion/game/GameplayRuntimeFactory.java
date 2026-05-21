package it.unime.orion.game;

import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.world.GameWorld;

/**
 * Creates the per-run gameplay runtime that wires active systems, event buses,
 * and player-specific runtime dependencies together.
 */
public interface GameplayRuntimeFactory {

    GameplayRuntime create(GameWorld world, GameSession session, PlayerShip player, double worldWidth);
}
