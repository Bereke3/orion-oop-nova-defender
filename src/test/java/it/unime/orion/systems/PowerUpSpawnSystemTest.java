package it.unime.orion.systems;

import it.unime.orion.powerups.ExtraLifePowerUp;
import it.unime.orion.powerups.PowerUp;
import it.unime.orion.world.GameWorld;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class PowerUpSpawnSystemTest {

    @Test
    void testSpawnSettingsCanDisableAndReEnableAmbientPowerUps() {
        GameWorld world = new GameWorld();
        PowerUpSpawnSystem spawnSystem = new PowerUpSpawnSystem(
                world,
                900,
                60,
                1.0,
                1,
                (type, x, y) -> new ExtraLifePowerUp(new Rectangle(24, 24), x, y),
                new Random(0)
        );

        spawnSystem.applySettings(1.0, 0);
        spawnSystem.update(1.1);
        assertEquals(0, countPowerUps(world));

        spawnSystem.applySettings(1.0, 1);
        spawnSystem.update(1.1);
        assertEquals(1, countPowerUps(world));
    }

    private int countPowerUps(GameWorld world) {
        int count = 0;
        for (var entity : world.getEntitiesView()) {
            if (entity instanceof PowerUp) {
                count++;
            }
        }
        return count;
    }
}
