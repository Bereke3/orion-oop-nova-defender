package it.unime.orion.world;

import it.unime.orion.entities.GameEntity;
import it.unime.orion.errors.EntityLifecycleException;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class GameWorldLifecycleTest {

    @Test
    void testAddingSameEntityTwiceThrowsLifecycleException() {
        GameWorld world = new GameWorld();
        GameEntity entity = new DummyEntity();

        world.addEntity(entity);

        try {
            world.addEntity(entity);
            fail("Expected EntityLifecycleException");
        } catch (EntityLifecycleException expected) {
            assertTrue(expected.getMessage().contains("already added"));
        }
    }

    @Test
    void testRemovingUnknownEntityReturnsFalse() {
        GameWorld world = new GameWorld();
        assertFalse(world.removeEntity(new DummyEntity()));
    }

    private static final class DummyEntity extends GameEntity {

        private DummyEntity() {
            super(new Rectangle(10, 10), 0, 0);
        }

        @Override
        public void update(double deltaSeconds) {
        }
    }
}
