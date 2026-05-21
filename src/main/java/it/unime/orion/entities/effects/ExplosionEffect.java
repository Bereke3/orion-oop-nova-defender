package it.unime.orion.entities.effects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public final class ExplosionEffect extends VisualEffect {

    private final double scaleGrowth;

    public ExplosionEffect(double x, double y, boolean bossExplosion) {
        this(
                x,
                y,
                bossExplosion ? 30 : 16,
                bossExplosion ? 0.85 : 0.45,
                bossExplosion ? Color.rgb(255, 69, 0, 0.92) : Color.rgb(255, 196, 0, 0.78),
                bossExplosion ? 2.6 : 1.7
        );
    }

    private ExplosionEffect(double x,
                            double y,
                            double radius,
                            double durationSeconds,
                            Color fillColor,
                            double scaleGrowth) {
        super(createView(radius, fillColor), x, y, durationSeconds);
        this.scaleGrowth = scaleGrowth;
        applyFrame(0);
    }

    private static Circle createView(double radius, Color fillColor) {
        Circle circle = new Circle(radius);
        circle.setCenterX(radius);
        circle.setCenterY(radius);
        circle.setFill(new RadialGradient(
                0,
                0,
                0.45,
                0.45,
                0.75,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.color(1.0, 1.0, 1.0, Math.min(1.0, fillColor.getOpacity() + 0.08))),
                new Stop(0.42, fillColor),
                new Stop(1.0, Color.color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), 0.0))
        ));
        circle.setMouseTransparent(true);
        return circle;
    }

    @Override
    protected void applyFrame(double progress) {
        double scale = 1.0 + (scaleGrowth * progress);
        getView().setScaleX(scale);
        getView().setScaleY(scale);
        getView().setOpacity(1.0 - progress);
    }
}
