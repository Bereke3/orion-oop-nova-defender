package it.unime.orion.entities.effects;

import it.unime.orion.entities.EntityType;
import it.unime.orion.entities.GameEntity;
import javafx.scene.Node;

public abstract class VisualEffect extends GameEntity {

    private final double durationSeconds;
    private double elapsedSeconds;

    protected VisualEffect(Node view, double x, double y, double durationSeconds) {
        super(EntityType.EFFECT, view, x, y);

        if (durationSeconds <= 0) {
            throw new IllegalArgumentException("durationSeconds must be > 0");
        }

        this.durationSeconds = durationSeconds;
        applyFrame(0);
    }

    public final boolean isExpired() {
        return elapsedSeconds >= durationSeconds;
    }

    public final double getProgress() {
        return Math.min(1.0, elapsedSeconds / durationSeconds);
    }

    @Override
    public void update(double deltaSeconds) {
        super.update(deltaSeconds);

        if (deltaSeconds > 0) {
            elapsedSeconds = Math.min(durationSeconds, elapsedSeconds + deltaSeconds);
        }

        applyFrame(getProgress());
    }

    protected abstract void applyFrame(double progress);
}
