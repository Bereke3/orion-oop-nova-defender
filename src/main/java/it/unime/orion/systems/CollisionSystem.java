package it.unime.orion.systems;

import it.unime.orion.combat.EnemyBullet;
import it.unime.orion.combat.Projectile;
import it.unime.orion.entities.GameEntity;
import it.unime.orion.entities.enemy.Enemy;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.events.DamageEvent;
import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.events.EventBus;
import it.unime.orion.events.PowerUpCollectedEvent;
import it.unime.orion.powerups.PowerUp;
import it.unime.orion.powerups.PowerUpContext;
import it.unime.orion.world.GameWorld;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class CollisionSystem {

    private final GameWorld world;
    private final PlayerShip player;
    private final EventBus<DamageEvent> damageBus;
    private final EventBus<EnemyDestroyedEvent> enemyDestroyedBus;
    private final EventBus<PowerUpCollectedEvent> powerUpCollectedBus;
    private final PowerUpContext powerUpContext;

    public CollisionSystem(GameWorld world,
                           PlayerShip player,
                           EventBus<DamageEvent> damageBus,
                           EventBus<EnemyDestroyedEvent> enemyDestroyedBus,
                           EventBus<PowerUpCollectedEvent> powerUpCollectedBus,
                           PowerUpContext powerUpContext) {
        this.world = Objects.requireNonNull(world, "world");
        this.player = Objects.requireNonNull(player, "player");
        this.damageBus = Objects.requireNonNull(damageBus, "damageBus");
        this.enemyDestroyedBus = Objects.requireNonNull(enemyDestroyedBus, "enemyDestroyedBus");
        this.powerUpCollectedBus = Objects.requireNonNull(powerUpCollectedBus, "powerUpCollectedBus");
        this.powerUpContext = Objects.requireNonNull(powerUpContext, "powerUpContext");
    }

    public CollisionResult handleCollisions(double worldHeight) {
        Set<GameEntity> toRemove = new LinkedHashSet<>();
        int collisionsCount = 0;
        int powerUpsCollected = 0;

        // Iterate over a snapshot so collisions can safely schedule removals.
        List<GameEntity> snapshot = new ArrayList<>(world.getEntitiesView());

        for (GameEntity entity : snapshot) {

            if (entity instanceof Enemy enemy) {
                if (!enemy.isAlive()) {
                    toRemove.add(enemy);
                    continue;
                }

                if (enemy.getY() > worldHeight + 40) {
                    toRemove.add(enemy);
                    continue;
                }

                if (player.getCollisionBounds().intersects(enemy.getCollisionBounds())) {
                    damageBus.publish(new DamageEvent(player.getId(), enemy.getContactDamage()));
                    toRemove.add(enemy);
                    collisionsCount++;
                }
            }

            if (entity instanceof Projectile projectile) {
                if (projectile.isExpired()
                        || projectile.getY() < -30
                        || projectile.getY() > worldHeight + 30) {
                    toRemove.add(projectile);
                    continue;
                }

                if (projectile instanceof EnemyBullet) {
                    if (projectile.getCollisionBounds().intersects(player.getCollisionBounds())) {
                        damageBus.publish(new DamageEvent(player.getId(), projectile.getDamage()));
                        toRemove.add(projectile);
                        collisionsCount++;
                        continue;
                    }
                }

                if (!(projectile instanceof EnemyBullet)) {
                    for (GameEntity other : snapshot) {
                        if (other instanceof Enemy enemy) {
                            if (!enemy.isAlive()) {
                                continue;
                            }

                            if (projectile.getCollisionBounds().intersects(enemy.getCollisionBounds())) {
                                enemy.takeDamage(projectile.getDamage());
                                toRemove.add(projectile);

                                if (!enemy.isAlive()) {
                                    toRemove.add(enemy);
                                    enemyDestroyedBus.publish(new EnemyDestroyedEvent(
                                            enemy.getId(),
                                            enemy.getX(),
                                            enemy.getY(),
                                            enemy.getScoreValue(),
                                            enemy.isBoss()
                                    ));
                                }

                                collisionsCount++;
                                break;
                            }
                        }
                    }
                }
            }

            if (entity instanceof PowerUp powerUp) {
                if (player.getCollisionBounds().intersects(powerUp.getCollisionBounds())) {
                    powerUp.applyTo(powerUpContext);
                    powerUpCollectedBus.publish(new PowerUpCollectedEvent(
                            powerUp.getId(),
                            powerUp.getPowerUpType(),
                            powerUp.getSpawnX(),
                            powerUp.getSpawnY()
                    ));
                    toRemove.add(powerUp);
                    powerUpsCollected++;
                }
            }
        }

        for (GameEntity entity : toRemove) {
            world.removeEntity(entity);
        }

        return new CollisionResult(collisionsCount, powerUpsCollected);
    }

    public static final class CollisionResult {
        private final int collisionsCount;
        private final int powerUpsCollected;

        public CollisionResult(int collisionsCount, int powerUpsCollected) {
            this.collisionsCount = collisionsCount;
            this.powerUpsCollected = powerUpsCollected;
        }

        public static CollisionResult empty() {
            return new CollisionResult(0, 0);
        }

        public int getCollisionsCount() {
            return collisionsCount;
        }

        public int getPowerUpsCollected() {
            return powerUpsCollected;
        }
    }
}
