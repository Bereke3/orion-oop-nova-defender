package it.unime.orion.entities.enemy.behavior;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.entities.enemy.Enemy;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.world.GameWorld;

public final class SwarmBehavior implements EnemyBehavior {

    private final double anchorX;
    private final double entryTargetY;
    private final double speedMultiplier;

    private double moveTimer;
    private boolean enteredFormation;

    public SwarmBehavior(double anchorX, double entryTargetY, double speedMultiplier) {
        this.anchorX = anchorX;
        this.entryTargetY = entryTargetY;
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public void update(Enemy enemy, PlayerShip player, GameWorld world, double deltaSeconds) {
        moveTimer += deltaSeconds;

        if (!enteredFormation) {
            double xDiff = anchorX - enemy.getX();
            double yDiff = entryTargetY - enemy.getY();

            if (yDiff > 6) {
                enemy.setVelocity(clamp(xDiff * 2.4, -85 * speedMultiplier, 85 * speedMultiplier),
                        130 * speedMultiplier);
                return;
            }

            enteredFormation = true;
        }

        double enemyCenterX = enemy.getX() + GameAssets.SWARM_WIDTH / 2.0;
        double playerCenterX = player.getX() + GameAssets.PLAYER_WIDTH / 2.0;
        double tracking = (playerCenterX - enemyCenterX) * 1.05;
        double weave = Math.sin(moveTimer * 5.2 + anchorX * 0.02) * 38;
        double vx = clamp(tracking + weave, -180 * speedMultiplier, 180 * speedMultiplier);
        double vy = (95 + Math.sin(moveTimer * 3.0 + anchorX * 0.01) * 16) * speedMultiplier;

        enemy.setVelocity(vx, vy);
    }

    @Override
    public boolean canShoot() {
        return false;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
