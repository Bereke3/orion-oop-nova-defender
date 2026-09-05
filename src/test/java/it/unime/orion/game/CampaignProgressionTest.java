package it.unime.orion.game;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.combat.BasicWeapon;
import it.unime.orion.entities.boss.BossA;
import it.unime.orion.entities.boss.BossTuning;
import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.entities.player.PlayerStats;
import it.unime.orion.level.EnemyTuning;
import it.unime.orion.level.LevelDefinition;
import it.unime.orion.level.Wave;
import it.unime.orion.level.WaveSpawnStrategy;
import it.unime.orion.world.GameWorld;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class CampaignProgressionTest {

    @Test
    void testProgressionSpawnsBossAfterClearedWaves() {
        GameWorld world = new GameWorld();
        GameSession session = new GameSession();
        session.startGame();
        PlayerShip player = createPlayer();
        world.addEntity(player);

        CampaignProgression progression = new CampaignProgression(
                world,
                900,
                List.of(createLevel(1))
        );

        progression.startFirstWave();
        progression.update(session, player);

        assertTrue(session.hasBossSpawned());
        assertNotNull(progression.getBoss());
        assertTrue(world.getEntitiesView().contains(progression.getBoss()));
    }

    @Test
    void testProgressionMarksVictoryAfterLastBossFalls() {
        GameWorld world = new GameWorld();
        GameSession session = new GameSession();
        session.startGame();
        PlayerShip player = createPlayer();
        world.addEntity(player);

        CampaignProgression progression = new CampaignProgression(
                world,
                900,
                List.of(createLevel(1))
        );

        progression.startFirstWave();
        progression.update(session, player);
        BossA boss = progression.getBoss();
        boss.takeDamage(boss.getBossMaxHp());

        progression.update(session, player);

        assertEquals(GameState.VICTORY, session.getState());
        assertNull(progression.getBoss());
    }

    @Test
    void testProgressionAdvancesToNextLevelBeforeVictory() {
        GameWorld world = new GameWorld();
        GameSession session = new GameSession();
        session.startGame();
        PlayerShip player = createPlayer();
        world.addEntity(player);

        CampaignProgression progression = new CampaignProgression(
                world,
                900,
                List.of(createLevel(1), createLevel(2))
        );

        progression.startFirstWave();
        progression.update(session, player);
        BossA firstBoss = progression.getBoss();
        firstBoss.takeDamage(firstBoss.getBossMaxHp());

        progression.update(session, player);

        assertEquals(GameState.RUNNING, session.getState());
        assertFalse(session.hasBossSpawned());

        progression.update(session, player);
        assertNotNull(progression.getBoss());
        assertNotSame(firstBoss, progression.getBoss());
    }

    @Test
    void testProgressionRecoversIfBossLeavesWorldUnexpectedly() {
        GameWorld world = new GameWorld();
        GameSession session = new GameSession();
        session.startGame();
        PlayerShip player = createPlayer();
        world.addEntity(player);

        CampaignProgression progression = new CampaignProgression(
                world,
                900,
                List.of(createLevel(1))
        );

        progression.startFirstWave();
        progression.update(session, player);
        BossA boss = progression.getBoss();
        world.removeEntity(boss);

        progression.update(session, player);

        assertEquals(GameState.VICTORY, session.getState());
        assertNull(progression.getBoss());
    }

    private LevelDefinition createLevel(int levelNumber) {
        return new LevelDefinition(
                levelNumber,
                List.of(new Wave(1, 0, 0)),
                new EnemyTuning(1.0, 1.0),
                new BossTuning(4, 0, 100, 30, 100, 1.0, 1.0, 0, 0, 500, GameAssets.getEnemyRedBulletAssetPath()),
                "boss_a",
                ignoringSpawnStrategy()
        );
    }

    private WaveSpawnStrategy ignoringSpawnStrategy() {
        return (world, worldWidth, waveIndex, wave, enemyTuning) -> {
        };
    }

    private PlayerShip createPlayer() {
        return new PlayerShip(
                new Rectangle(60, 40),
                420,
                520,
                new PlayerStats(100),
                new PlayerMovement(250, 0, 900, 0, 600),
                new BasicWeapon()
        );
    }
}
