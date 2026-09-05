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
    private final EnemyCollisionHandler enemyCollisionHandler;
    private final ProjectileCollisionHandler projectileCollisionHandler;
    private final PowerUpCollisionHandler powerUpCollisionHandler;

    public CollisionSystem(GameWorld world,
                           PlayerShip player,
                           EventBus<DamageEvent> damageBus,
                           EventBus<EnemyDestroyedEvent> enemyDestroyedBus,
                           EventBus<PowerUpCollectedEvent> powerUpCollectedBus,
                           PowerUpContext powerUpContext) {
        this.world = Objects.requireNonNull(world, "world");
        PlayerShip requiredPlayer = Objects.requireNonNull(player, "player");
        EventBus<DamageEvent> requiredDamageBus = Objects.requireNonNull(damageBus, "damageBus");
        EventBus<EnemyDestroyedEvent> requiredEnemyDestroyedBus = Objects.requireNonNull(enemyDestroyedBus, "enemyDestroyedBus");
        EventBus<PowerUpCollectedEvent> requiredPowerUpCollectedBus = Objects.requireNonNull(powerUpCollectedBus, "powerUpCollectedBus");
        PowerUpContext requiredPowerUpContext = Objects.requireNonNull(powerUpContext, "powerUpContext");

        this.enemyCollisionHandler = new EnemyCollisionHandler(requiredPlayer, requiredDamageBus);
        this.projectileCollisionHandler = new ProjectileCollisionHandler(
                requiredPlayer,
                requiredDamageBus,
                requiredEnemyDestroyedBus
        );
        this.powerUpCollisionHandler = new PowerUpCollisionHandler(
                requiredPlayer,
                requiredPowerUpCollectedBus,
                requiredPowerUpContext
        );
    }

    public CollisionResult handleCollisions(double worldHeight) {
        CollisionSweep sweep = new CollisionSweep(world, worldHeight);

        for (GameEntity entity : sweep.snapshot()) {
            if (entity instanceof Enemy enemy) {
                enemyCollisionHandler.handle(enemy, sweep);
                continue;
            }
            if (entity instanceof Projectile projectile) {
                projectileCollisionHandler.handle(projectile, sweep);
                continue;
            }
            if (entity instanceof PowerUp powerUp) {
                powerUpCollisionHandler.handle(powerUp, sweep);
            }
        }

        return sweep.finish();
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

    private static final class CollisionSweep {

        private final GameWorld world;
        private final double worldHeight;
        private final List<GameEntity> snapshot;
        private final Set<GameEntity> toRemove = new LinkedHashSet<>();

        private int collisionsCount;
        private int powerUpsCollected;

        private CollisionSweep(GameWorld world, double worldHeight) {
            this.world = world;
            this.worldHeight = worldHeight;
            this.snapshot = new ArrayList<>(world.getEntitiesView());
        }

        private List<GameEntity> snapshot() {
            return snapshot;
        }

        private double worldHeight() {
            return worldHeight;
        }

        private void markForRemoval(GameEntity entity) {
            toRemove.add(entity);
        }

        private void countCollision() {
            collisionsCount++;
        }

        private void countPowerUpCollected() {
            powerUpsCollected++;
        }

        private CollisionResult finish() {
            for (GameEntity entity : toRemove) {
                world.removeEntity(entity);
            }
            return new CollisionResult(collisionsCount, powerUpsCollected);
        }
    }

    private static final class EnemyCollisionHandler {

        private final PlayerShip player;
        private final EventBus<DamageEvent> damageBus;

        private EnemyCollisionHandler(PlayerShip player, EventBus<DamageEvent> damageBus) {
            this.player = player;
            this.damageBus = damageBus;
        }

        private void handle(Enemy enemy, CollisionSweep sweep) {
            if (!enemy.isAlive()) {
                sweep.markForRemoval(enemy);
                return;
            }

            if (enemy.getY() > sweep.worldHeight() + 40) {
                sweep.markForRemoval(enemy);
                return;
            }

            if (player.getCollisionBounds().intersects(enemy.getCollisionBounds())) {
                damageBus.publish(new DamageEvent(player.getId(), enemy.getContactDamage()));
                sweep.markForRemoval(enemy);
                sweep.countCollision();
            }
        }
    }

    private static final class ProjectileCollisionHandler {

        private final PlayerShip player;
        private final EventBus<DamageEvent> damageBus;
        private final EventBus<EnemyDestroyedEvent> enemyDestroyedBus;

        private ProjectileCollisionHandler(PlayerShip player,
                                           EventBus<DamageEvent> damageBus,
                                           EventBus<EnemyDestroyedEvent> enemyDestroyedBus) {
            this.player = player;
            this.damageBus = damageBus;
            this.enemyDestroyedBus = enemyDestroyedBus;
        }

        private void handle(Projectile projectile, CollisionSweep sweep) {
            if (projectile.isExpired()
                    || projectile.getY() < -30
                    || projectile.getY() > sweep.worldHeight() + 30) {
                sweep.markForRemoval(projectile);
                return;
            }

            if (projectile instanceof EnemyBullet) {
                handleEnemyBullet(projectile, sweep);
                return;
            }

            handlePlayerProjectile(projectile, sweep);
        }

        private void handleEnemyBullet(Projectile projectile, CollisionSweep sweep) {
            if (projectile.getCollisionBounds().intersects(player.getCollisionBounds())) {
                damageBus.publish(new DamageEvent(player.getId(), projectile.getDamage()));
                sweep.markForRemoval(projectile);
                sweep.countCollision();
            }
        }

        private void handlePlayerProjectile(Projectile projectile, CollisionSweep sweep) {
            for (GameEntity entity : sweep.snapshot()) {
                if (!(entity instanceof Enemy enemy) || !enemy.isAlive()) {
                    continue;
                }

                if (projectile.getCollisionBounds().intersects(enemy.getCollisionBounds())) {
                    enemy.takeDamage(projectile.getDamage());
                    sweep.markForRemoval(projectile);

                    if (!enemy.isAlive()) {
                        sweep.markForRemoval(enemy);
                        enemyDestroyedBus.publish(new EnemyDestroyedEvent(
                                enemy.getId(),
                                enemy.getX(),
                                enemy.getY(),
                                enemy.getScoreValue(),
                                enemy.isBoss()
                        ));
                    }

                    sweep.countCollision();
                    return;
                }
            }
        }
    }

    private static final class PowerUpCollisionHandler {

        private final PlayerShip player;
        private final EventBus<PowerUpCollectedEvent> powerUpCollectedBus;
        private final PowerUpContext powerUpContext;

        private PowerUpCollisionHandler(PlayerShip player,
                                       EventBus<PowerUpCollectedEvent> powerUpCollectedBus,
                                       PowerUpContext powerUpContext) {
            this.player = player;
            this.powerUpCollectedBus = powerUpCollectedBus;
            this.powerUpContext = powerUpContext;
        }

        private void handle(PowerUp powerUp, CollisionSweep sweep) {
            if (!player.getCollisionBounds().intersects(powerUp.getCollisionBounds())) {
                return;
            }

            powerUp.applyTo(powerUpContext);
            powerUpCollectedBus.publish(new PowerUpCollectedEvent(
                    powerUp.getId(),
                    powerUp.getPowerUpType(),
                    powerUp.getSpawnX(),
                    powerUp.getSpawnY()
            ));
            sweep.markForRemoval(powerUp);
            sweep.countPowerUpCollected();
        }
    }
}
