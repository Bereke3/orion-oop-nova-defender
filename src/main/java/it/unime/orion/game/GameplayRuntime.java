package it.unime.orion.game;

import it.unime.orion.combat.Projectile;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.events.DamageEvent;
import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.events.EventBus;
import it.unime.orion.events.EventSubscription;
import it.unime.orion.events.PowerUpCollectedEvent;
import it.unime.orion.level.LevelRuntimeTuning;
import it.unime.orion.systems.FixedBossRewardPolicy;
import it.unime.orion.systems.BossRewardSystem;
import it.unime.orion.systems.CollisionSystem;
import it.unime.orion.systems.EnemyAttackSystem;
import it.unime.orion.systems.PowerUpSpawnSystem;
import it.unime.orion.systems.VisualEffectSystem;
import it.unime.orion.world.GameWorld;

import java.util.List;
import java.util.Objects;

public final class GameplayRuntime implements AutoCloseable {

    private final GameWorld world;
    private final GameSession session;
    private final PlayerShip player;
    private final EventBus<DamageEvent> damageBus = new EventBus<>();
    private final EventBus<EnemyDestroyedEvent> enemyDestroyedBus = new EventBus<>();
    private final EventBus<PowerUpCollectedEvent> powerUpCollectedBus = new EventBus<>();
    private final CollisionSystem collisionSystem;
    private final EnemyAttackSystem enemyAttackSystem;
    private final PowerUpSpawnSystem powerUpSpawnSystem;
    private final VisualEffectSystem visualEffectSystem;
    private final BossRewardSystem bossRewardSystem;
    private final EventSubscription damageSubscription;
    private final EventSubscription enemyDestroyedSubscription;

    public GameplayRuntime(GameWorld world, GameSession session, PlayerShip player, double worldWidth) {
        this.world = Objects.requireNonNull(world, "world");
        this.session = Objects.requireNonNull(session, "session");
        this.player = Objects.requireNonNull(player, "player");

        this.collisionSystem = new CollisionSystem(
                world,
                player,
                damageBus,
                enemyDestroyedBus,
                powerUpCollectedBus,
                new GamePowerUpContext(player, session)
        );
        this.enemyAttackSystem = new EnemyAttackSystem(world, player);
        this.powerUpSpawnSystem = new PowerUpSpawnSystem(world, worldWidth);
        this.visualEffectSystem = new VisualEffectSystem(world, enemyDestroyedBus, powerUpCollectedBus);
        this.bossRewardSystem = new BossRewardSystem(world, enemyDestroyedBus);
        this.damageSubscription = damageBus.subscribe(this::handleDamageEvent);
        this.enemyDestroyedSubscription = enemyDestroyedBus.subscribe(this::handleEnemyDestroyed);

        world.addEntity(player);
    }

    public PlayerShip getPlayer() {
        return player;
    }

    public void updateSystems(double dt) {
        enemyAttackSystem.update(dt);
        powerUpSpawnSystem.update(dt);
    }

    public void resolveCollisions(double worldHeight) {
        collisionSystem.handleCollisions(worldHeight);
    }

    public void updateEffects() {
        visualEffectSystem.update();
    }

    public void applyDebugDamageToPlayer(int amount) {
        damageBus.publish(new DamageEvent(player.getId(), amount));
    }

    public void addProjectiles(List<Projectile> projectiles) {
        for (Projectile projectile : projectiles) {
            world.addEntity(projectile);
        }
        // Keep the player sprite readable when new bullets are spawned in the same area.
        player.bringToFront();
    }

    public void applyLevelRuntimeTuning(LevelRuntimeTuning runtimeTuning) {
        Objects.requireNonNull(runtimeTuning, "runtimeTuning");
        // Keep runtime systems configurable from the active level definition.
        powerUpSpawnSystem.applySettings(
                runtimeTuning.getAmbientPowerUpSpawnIntervalSeconds(),
                runtimeTuning.getMaxAmbientPowerUps()
        );
        bossRewardSystem.setBossRewardPolicy(new FixedBossRewardPolicy(runtimeTuning.getBossRewardType()));
    }

    private void handleDamageEvent(DamageEvent event) {
        if (event.getTargetId().equals(player.getId())) {
            player.takeDamage(event.getAmount());
        }
    }

    private void handleEnemyDestroyed(EnemyDestroyedEvent event) {
        session.addScore(event.getScoreValue());
    }

    @Override
    public void close() {
        damageSubscription.unsubscribe();
        enemyDestroyedSubscription.unsubscribe();
        closeQuietly(visualEffectSystem);
        closeQuietly(bossRewardSystem);
    }

    private void closeQuietly(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to close gameplay runtime resource", exception);
        }
    }
}
