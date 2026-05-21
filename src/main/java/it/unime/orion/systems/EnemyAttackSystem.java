package it.unime.orion.systems;

import it.unime.orion.entities.GameEntity;
import it.unime.orion.entities.enemy.Enemy;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EnemyAttackSystem {

    private static final Logger LOGGER = Logger.getLogger(EnemyAttackSystem.class.getName());

    private final GameWorld world;
    private final PlayerShip player;

    public EnemyAttackSystem(GameWorld world, PlayerShip player) {
        this.world = Objects.requireNonNull(world, "world");
        this.player = Objects.requireNonNull(player, "player");
    }

    public void update(double deltaSeconds) {
        List<GameEntity> behaviorSnapshot = new ArrayList<>(world.getEntitiesView());

        for (GameEntity entity : behaviorSnapshot) {
            if (entity instanceof Enemy enemy) {
                try {
                    enemy.updateBehavior(player, world, deltaSeconds);
                } catch (RuntimeException exception) {
                    LOGGER.log(Level.WARNING,
                            "Enemy behavior update failed for entity " + enemy.getId() + "; skipping this frame.",
                            exception);
                }
            }
        }
    }
}
