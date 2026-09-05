package it.unime.orion.systems;

import it.unime.orion.combat.BasicWeapon;
import it.unime.orion.combat.Weapon;
import it.unime.orion.combat.PlayerBullet;
import it.unime.orion.entities.enemy.Enemy;
import it.unime.orion.game.GameSession;
import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.entities.player.PlayerStats;
import it.unime.orion.events.DamageEvent;
import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.events.EventBus;
import it.unime.orion.events.PowerUpCollectedEvent;
import it.unime.orion.level.EnemyTuning;
import it.unime.orion.powerups.ExtraLifePowerUp;
import it.unime.orion.powerups.HealPowerUp;
import it.unime.orion.powerups.PowerUpContext;
import it.unime.orion.powerups.PowerUpType;
import it.unime.orion.world.GameWorld;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public final class CollisionSystemTest {

    @Test
    void testPlayerBulletDestroyingEnemyRemovesEntitiesAndPublishesEvent() {
        GameWorld world = new GameWorld();
        PlayerShip player = createPlayer(280, 520);
        EventBus<DamageEvent> damageBus = new EventBus<>();
        EventBus<EnemyDestroyedEvent> enemyDestroyedBus = new EventBus<>();
        EventBus<PowerUpCollectedEvent> powerUpCollectedBus = new EventBus<>();
        GameSession session = new GameSession();
        CollisionSystem collisionSystem = new CollisionSystem(
                world,
                player,
                damageBus,
                enemyDestroyedBus,
                powerUpCollectedBus,
                createPowerUpContext(player, session)
        );

        Enemy enemy = Enemy.createSwarm(new Rectangle(34, 28), 100, 100, 100, new EnemyTuning(1.0, 1.0));
        PlayerBullet bullet = new PlayerBullet(new Rectangle(8, 18), 100, 100);
        AtomicReference<EnemyDestroyedEvent> publishedEvent = new AtomicReference<>();

        enemyDestroyedBus.subscribe(publishedEvent::set);
        world.addEntity(player);
        world.addEntity(enemy);
        world.addEntity(bullet);

        CollisionSystem.CollisionResult result = collisionSystem.handleCollisions(800);

        assertEquals(1, result.getCollisionsCount());
        assertEquals(0, result.getPowerUpsCollected());
        assertFalse(world.getEntitiesView().contains(enemy));
        assertFalse(world.getEntitiesView().contains(bullet));
        assertNotNull(publishedEvent.get());
        assertEquals(enemy.getId(), publishedEvent.get().getEnemyId());
        assertEquals(enemy.getScoreValue(), publishedEvent.get().getScoreValue());
        assertFalse(publishedEvent.get().isBossKill());
    }

    @Test
    void testCollectingHealPowerUpPublishesEventAndRestoresHp() {
        GameWorld world = new GameWorld();
        PlayerShip player = createPlayer(220, 500);
        EventBus<DamageEvent> damageBus = new EventBus<>();
        EventBus<EnemyDestroyedEvent> enemyDestroyedBus = new EventBus<>();
        EventBus<PowerUpCollectedEvent> powerUpCollectedBus = new EventBus<>();
        GameSession session = new GameSession();
        CollisionSystem collisionSystem = new CollisionSystem(
                world,
                player,
                damageBus,
                enemyDestroyedBus,
                powerUpCollectedBus,
                createPowerUpContext(player, session)
        );

        HealPowerUp healPowerUp = new HealPowerUp(new Rectangle(24, 24), player.getX(), player.getY(), 25);
        AtomicReference<PowerUpCollectedEvent> publishedEvent = new AtomicReference<>();

        player.takeDamage(40);
        powerUpCollectedBus.subscribe(publishedEvent::set);
        world.addEntity(player);
        world.addEntity(healPowerUp);

        CollisionSystem.CollisionResult result = collisionSystem.handleCollisions(800);

        assertEquals(0, result.getCollisionsCount());
        assertEquals(1, result.getPowerUpsCollected());
        assertEquals(85, player.getHp());
        assertFalse(world.getEntitiesView().contains(healPowerUp));
        assertNotNull(publishedEvent.get());
        assertEquals(healPowerUp.getId(), publishedEvent.get().getPowerUpId());
        assertEquals(PowerUpType.HEAL, publishedEvent.get().getPowerUpType());
    }

    @Test
    void testCollectingExtraLifePowerUpRestoresOneLifeOnlyUpToMaximum() {
        GameWorld world = new GameWorld();
        PlayerShip player = createPlayer(220, 500);
        EventBus<DamageEvent> damageBus = new EventBus<>();
        EventBus<EnemyDestroyedEvent> enemyDestroyedBus = new EventBus<>();
        EventBus<PowerUpCollectedEvent> powerUpCollectedBus = new EventBus<>();
        GameSession session = new GameSession(3);
        session.startGame();
        assertTrue(session.handlePlayerDestroyed());
        assertEquals(2, session.getLivesLeft());

        CollisionSystem collisionSystem = new CollisionSystem(
                world,
                player,
                damageBus,
                enemyDestroyedBus,
                powerUpCollectedBus,
                createPowerUpContext(player, session)
        );

        ExtraLifePowerUp extraLifePowerUp = new ExtraLifePowerUp(new Rectangle(24, 24), player.getX(), player.getY());
        world.addEntity(player);
        world.addEntity(extraLifePowerUp);

        collisionSystem.handleCollisions(800);
        assertEquals(3, session.getLivesLeft());

        ExtraLifePowerUp redundantLife = new ExtraLifePowerUp(new Rectangle(24, 24), player.getX(), player.getY());
        world.addEntity(redundantLife);
        collisionSystem.handleCollisions(800);
        assertEquals(3, session.getLivesLeft());
    }

    private PlayerShip createPlayer(double x, double y) {
        return new PlayerShip(
                new Rectangle(60, 40),
                x,
                y,
                new PlayerStats(100),
                new PlayerMovement(250, 0, 800, 0, 600),
                new BasicWeapon()
        );
    }

    private PowerUpContext createPowerUpContext(PlayerShip player, GameSession session) {
        return new PowerUpContext() {
            @Override
            public void healPlayer(int amount) {
                player.heal(amount);
            }

            @Override
            public void activateShield() {
                player.activateShield();
            }

            @Override
            public void activateTemporaryWeapon(Weapon weapon, double durationSeconds) {
                player.activateTemporaryWeapon(weapon, durationSeconds);
            }

            @Override
            public boolean gainLife() {
                return session.gainLife();
            }
        };
    }
}
