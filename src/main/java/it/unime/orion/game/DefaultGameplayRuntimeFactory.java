package it.unime.orion.game;

import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.world.GameWorld;

public final class DefaultGameplayRuntimeFactory implements GameplayRuntimeFactory {

    @Override
    public GameplayRuntime create(GameWorld world, GameSession session, PlayerShip player, double worldWidth) {
        return new GameplayRuntime(world, session, player, worldWidth);
    }
}
