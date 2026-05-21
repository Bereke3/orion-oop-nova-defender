package it.unime.orion.game;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.combat.BasicWeapon;
import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.entities.player.PlayerStats;

import java.util.Objects;

public final class DefaultPlayerFactory implements PlayerFactory {

    @Override
    public PlayerShip create(PlayerMovement movement, double worldWidth, double worldHeight) {
        Objects.requireNonNull(movement, "movement");

        PlayerStats stats = new PlayerStats(100);
        double spawnX = (worldWidth - GameAssets.PLAYER_WIDTH) / 2.0;
        double spawnY = worldHeight - GameAssets.PLAYER_HEIGHT - 14;

        return new PlayerShip(
                GameAssets.createPlayerView(),
                spawnX,
                spawnY,
                stats,
                movement,
                new BasicWeapon()
        );
    }
}
