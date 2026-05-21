package it.unime.orion.engine;

import it.unime.orion.entities.GameEntity;
import it.unime.orion.world.GameWorld;
import javafx.animation.AnimationTimer;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public final class GameLoop extends AnimationTimer {

    @FunctionalInterface
    public interface PreUpdate {
        void run(double deltaSeconds);
    }

    @FunctionalInterface
    public interface PostUpdate {
        void run(double deltaSeconds);
    }

    private final GameWorld world;
    private long lastNs = -1;

    private PreUpdate preUpdate = dt -> { };
    private PostUpdate postUpdate = dt -> { };
    private BooleanSupplier shouldAdvanceWorld = () -> true;

    public GameLoop(GameWorld world) {
        this.world = Objects.requireNonNull(world, "world");
    }

    public void setPreUpdate(PreUpdate preUpdate) {
        this.preUpdate = Objects.requireNonNull(preUpdate, "preUpdate");
    }

    public void setPostUpdate(PostUpdate postUpdate) {
        this.postUpdate = Objects.requireNonNull(postUpdate, "postUpdate");
    }

    public void setShouldAdvanceWorld(BooleanSupplier shouldAdvanceWorld) {
        this.shouldAdvanceWorld = Objects.requireNonNull(shouldAdvanceWorld, "shouldAdvanceWorld");
    }

    @Override
    public void handle(long nowNs) {
        if (lastNs < 0) {
            lastNs = nowNs;
            return;
        }
        double deltaSeconds = (nowNs - lastNs) / 1_000_000_000.0;
        lastNs = nowNs;

        // Let the controller update input and gameplay systems before entity movement.
        preUpdate.run(deltaSeconds);

        if (shouldAdvanceWorld.getAsBoolean()) {
            for (GameEntity e : world.getEntitiesView()) {
                e.update(deltaSeconds);
            }
        }

        postUpdate.run(deltaSeconds);
    }
}
