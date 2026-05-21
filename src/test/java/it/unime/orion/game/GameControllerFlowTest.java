package it.unime.orion.game;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.combat.BasicWeapon;
import it.unime.orion.entities.boss.BossA;
import it.unime.orion.entities.boss.BossTuning;
import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.entities.player.PlayerStats;
import it.unime.orion.input.InputAction;
import it.unime.orion.input.InputState;
import it.unime.orion.level.CampaignFactory;
import it.unime.orion.level.CampaignValidator;
import it.unime.orion.level.DefaultCampaignValidator;
import it.unime.orion.level.EnemyTuning;
import it.unime.orion.level.LevelDefinition;
import it.unime.orion.level.Wave;
import it.unime.orion.ui.GameOverlayView;
import it.unime.orion.ui.HudView;
import it.unime.orion.world.GameWorld;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class GameControllerFlowTest {

    private static final double WORLD_WIDTH = 900;
    private static final double WORLD_HEIGHT = 600;
    private static final double DT = 0.016;

    @Test
    void testControllerStartsPausesAndResumesRun() {
        GameWorld world = new GameWorld();
        HudView hud = new HudView();
        GameOverlayView overlay = new GameOverlayView(WORLD_WIDTH, WORLD_HEIGHT);
        InputState input = new InputState();
        GameController controller = createController(world, hud, overlay, input);

        try {
            assertFalse(controller.shouldAdvanceWorld());
            assertTrue(overlay.getRoot().isVisible());

            tap(controller, input, InputAction.CONFIRM);
            assertTrue(controller.shouldAdvanceWorld());
            assertFalse(overlay.getRoot().isVisible());

            tap(controller, input, InputAction.PAUSE);
            assertFalse(controller.shouldAdvanceWorld());
            assertTrue(overlay.getRoot().isVisible());

            tap(controller, input, InputAction.PAUSE);
            assertTrue(controller.shouldAdvanceWorld());
            assertFalse(overlay.getRoot().isVisible());
        } finally {
            controller.close();
        }
    }

    @Test
    void testControllerReachesGameOverAndRestartsRun() {
        GameWorld world = new GameWorld();
        HudView hud = new HudView();
        GameOverlayView overlay = new GameOverlayView(WORLD_WIDTH, WORLD_HEIGHT);
        InputState input = new InputState();
        GameController controller = createController(world, hud, overlay, input);

        try {
            tap(controller, input, InputAction.CONFIRM);

            tap(controller, input, InputAction.DEBUG_DAMAGE);
            tap(controller, input, InputAction.DEBUG_DAMAGE);
            tap(controller, input, InputAction.DEBUG_DAMAGE);

            assertFalse(controller.shouldAdvanceWorld());
            assertTrue(overlay.getRoot().isVisible());

            tap(controller, input, InputAction.CONFIRM);

            assertTrue(controller.shouldAdvanceWorld());
            assertFalse(overlay.getRoot().isVisible());
        } finally {
            controller.close();
        }
    }

    private GameController createController(GameWorld world, HudView hud, GameOverlayView overlay, InputState input) {
        CampaignFactory campaignFactory = width -> List.of(
                new LevelDefinition(
                        1,
                        List.of(new Wave(1, 0, 0)),
                        new EnemyTuning(1.0, 1.0),
                        new BossTuning(8, 0, 100, 30, 100, 1.0, 1.0, 0, 0, 500, GameAssets.getEnemyRedBulletAssetPath()),
                        "boss_a",
                        (ignoredWorld, ignoredWidth, waveIndex, wave, tuning) -> {
                        }
                )
        );
        PlayerFactory playerFactory = (movement, worldWidth, worldHeight) -> new PlayerShip(
                new Rectangle(60, 40),
                (worldWidth - GameAssets.PLAYER_WIDTH) / 2.0,
                worldHeight - GameAssets.PLAYER_HEIGHT - 14,
                new PlayerStats(10),
                movement,
                new BasicWeapon(),
                0
        );
        BossFactory bossFactory = this::createBoss;
        CampaignValidator validator = new DefaultCampaignValidator();

        return new GameController(
                WORLD_WIDTH,
                WORLD_HEIGHT,
                world,
                hud,
                overlay,
                input,
                campaignFactory,
                playerFactory,
                bossFactory,
                new DefaultGameplayRuntimeFactory(),
                validator
        );
    }

    private BossA createBoss(LevelDefinition level, double worldWidth) {
        return new BossA(new Rectangle(100, 60), 100, 0, level.getBossTuning());
    }

    private void tap(GameController controller, InputState input, InputAction action) {
        input.press(action);
        controller.preUpdate(DT);
        controller.postUpdate(DT);

        input.release(action);
        controller.preUpdate(DT);
        controller.postUpdate(DT);
    }
}
