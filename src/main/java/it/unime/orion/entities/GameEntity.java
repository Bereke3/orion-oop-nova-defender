package it.unime.orion.entities;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.Objects;
import java.util.UUID;

public abstract class GameEntity {

    private final String id = UUID.randomUUID().toString();

    private double x;
    private double y;

    private double vx;
    private double vy;

    private final Node view;

    protected GameEntity(Node view, double x, double y) {
        this.view = Objects.requireNonNull(view, "view");
        this.x = x;
        this.y = y;
        syncView();
    }

    public final String getId() {
        return id;
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

    public final void attachTo(Pane parent) {
        Objects.requireNonNull(parent, "parent").getChildren().add(view);
    }

    public final boolean detachFrom(Pane parent) {
        return Objects.requireNonNull(parent, "parent").getChildren().remove(view);
    }

    public final boolean isAttachedTo(Pane parent) {
        return Objects.requireNonNull(parent, "parent").getChildren().contains(view);
    }

    public final void bringToFront() {
        view.toFront();
    }

    protected final double getViewWidth() {
        return view.getBoundsInLocal().getWidth();
    }

    protected final void setViewOpacity(double opacity) {
        view.setOpacity(opacity);
    }

    protected final void setViewScale(double scaleX, double scaleY) {
        view.setScaleX(scaleX);
        view.setScaleY(scaleY);
    }

    protected final void setViewRotation(double angleDegrees) {
        view.setRotate(angleDegrees);
    }

    public Bounds getCollisionBounds() {
        return view.getBoundsInParent();
    }

    public void update(double deltaSeconds) {
        x += vx * deltaSeconds;
        y += vy * deltaSeconds;
        syncView();
    }
}
