package it.unime.orion.engine;

import it.unime.orion.entities.GameEntity;
import it.unime.orion.world.GameWorld;
import javafx.animation.AnimationTimer;

import java.util.Objects;

public final class GameLoop extends AnimationTimer {

    @FunctionalInterface
    public interface PreUpdate {
        void run(double deltaSeconds);
    }

    private final GameWorld world;
    private long lastNs = -1;

    private PreUpdate preUpdate = dt -> { };

    public GameLoop(GameWorld world) {
        this.world = Objects.requireNonNull(world, "world");
    }

    public void setPreUpdate(PreUpdate preUpdate) {
        this.preUpdate = Objects.requireNonNull(preUpdate, "preUpdate");
    }

    @Override
    public void handle(long nowNs) {
        if (lastNs < 0) {
            lastNs = nowNs;
            return;
        }
        double deltaSeconds = (nowNs - lastNs) / 1_000_000_000.0;
        lastNs = nowNs;

        // hook before entities update (input, AI, etc.)
        preUpdate.run(deltaSeconds);

        for (GameEntity e : world.getEntitiesView()) {
            e.update(deltaSeconds);
        }
    }
}