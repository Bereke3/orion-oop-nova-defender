package it.unime.orion.entities.effects;

import it.unime.orion.powerups.PowerUpType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class PowerUpPickupEffect extends VisualEffect {

    private static final double SIZE = 32;

    public PowerUpPickupEffect(double x, double y, PowerUpType powerUpType) {
        super(createView(powerUpType), x - 4, y - 4, 0.40);
        setVelocity(0, -24);
        applyFrame(0);
    }

    private static Rectangle createView(PowerUpType powerUpType) {
        Rectangle rectangle = new Rectangle(SIZE, SIZE);
        rectangle.setArcWidth(14);
        rectangle.setArcHeight(14);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(colorFor(powerUpType));
        rectangle.setStrokeWidth(3);
        rectangle.setMouseTransparent(true);
        return rectangle;
    }

    private static Color colorFor(PowerUpType powerUpType) {
        return switch (powerUpType) {
            case HEAL -> Color.DEEPSKYBLUE;
            case SHIELD -> Color.GOLD;
            case WEAPON_UPGRADE -> Color.HOTPINK;
            case EXTRA_LIFE -> Color.RED;
        };
    }

    @Override
    protected void applyFrame(double progress) {
        double scale = 1.0 + (1.2 * progress);
        getView().setScaleX(scale);
        getView().setScaleY(scale);
        getView().setRotate(90 * progress);
        getView().setOpacity(1.0 - progress);
    }
}
