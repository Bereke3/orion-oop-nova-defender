package it.unime.orion.game;

import it.unime.orion.combat.BasicWeapon;
import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.entities.player.PlayerStats;
import it.unime.orion.world.GameWorld;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class GameplayRuntimeTest {

    @Test
    void testDebugDamageAffectsPlayerWhileRuntimeIsActive() {
        GameWorld world = new GameWorld();
        GameSession session = new GameSession();
        PlayerShip player = createPlayer();
        try (GameplayRuntime runtime = new GameplayRuntime(world, session, player, 900)) {
            runtime.applyDebugDamageToPlayer(15);

            assertEquals(85, player.getHp());
        }
    }

    @Test
    void testClosedRuntimeStopsDispatchingDebugDamage() {
        GameWorld world = new GameWorld();
        GameSession session = new GameSession();
        PlayerShip player = createPlayer();
        GameplayRuntime runtime = new GameplayRuntime(world, session, player, 900);
        runtime.close();

        runtime.applyDebugDamageToPlayer(15);

        assertEquals(100, player.getHp());
    }

    private PlayerShip createPlayer() {
        return new PlayerShip(
                new Rectangle(60, 40),
                420,
                520,
                new PlayerStats(100),
                new PlayerMovement(250, 0, 900, 0, 600),
                new BasicWeapon()
        );
    }
}
