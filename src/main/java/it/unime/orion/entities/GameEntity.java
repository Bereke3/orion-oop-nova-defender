package it.unime.orion.entities;

import javafx.geometry.Bounds;
import javafx.scene.Node;

import java.util.Objects;
import java.util.UUID;

public abstract class GameEntity implements Updatable, Renderable, Collidable {

    private final String id = UUID.randomUUID().toString();
    private final EntityType type;

    private double x;
    private double y;

    private double vx;
    private double vy;

    private final Node view;

    protected GameEntity(EntityType type, Node view, double x, double y) {
        this.type = Objects.requireNonNull(type, "type");
        this.view = Objects.requireNonNull(view, "view");
        this.x = x;
        this.y = y;
        syncView();
    }

    public final String getId() {
        return id;
    }

    public final EntityType getType() {
        return type;
    }

    public final double getX() {
        return x;
    }

    public final double getY() {
        return y;
    }

    public final void setVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    protected final double getVelocityX() {
        return vx;
    }

    protected final double getVelocityY() {
        return vy;
    }

    protected final void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        syncView();
    }

    private void syncView() {
        view.setLayoutX(x);
        view.setLayoutY(y);
    }

    @Override
    public final Node getView() {
        return view;
    }

    @Override
    public Bounds getCollisionBounds() {
        return view.getBoundsInParent();
    }

    @Override
    public void update(double deltaSeconds) {
        x += vx * deltaSeconds;
        y += vy * deltaSeconds;
        syncView();
    }
}
