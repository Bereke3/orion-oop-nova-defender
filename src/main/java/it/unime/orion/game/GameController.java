package it.unime.orion.game;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.combat.BasicWeapon;
import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.entities.player.PlayerStats;
import it.unime.orion.input.InputState;
import it.unime.orion.level.DefaultCampaignFactory;
import it.unime.orion.level.LevelDefinition;
import it.unime.orion.ui.GameUiPresenter;
import it.unime.orion.ui.GameOverlayView;
import it.unime.orion.ui.HudView;
import it.unime.orion.world.GameWorld;

import java.util.List;
import java.util.Objects;
import java.util.function.DoubleFunction;

public final class GameController implements AutoCloseable {

    @FunctionalInterface
    public interface PlayerCreator {

        PlayerShip create(PlayerMovement movement, double worldWidth, double worldHeight);
    }
    private final GameSession session = new GameSession();
    private final SessionInputInterpreter sessionInputInterpreter;
    private final PlayerInputController playerInputController;
    private final GameRunLifecycle runLifecycle;

    public GameController(double worldWidth,
                          double worldHeight,
                          GameWorld world,
                          HudView hud,
                          GameOverlayView overlay,
                          InputState input) {
        this(worldWidth, worldHeight, world, hud, overlay, input, new DefaultCampaignFactory()::createCampaign);
    }

    public GameController(double worldWidth,
                          double worldHeight,
                          GameWorld world,
                          HudView hud,
                          GameOverlayView overlay,
                          InputState input,
                          DoubleFunction<List<LevelDefinition>> campaignLoader) {
        this(worldWidth, worldHeight, world, hud, overlay, input, campaignLoader, GameController::createDefaultPlayer);
    }

    public GameController(double worldWidth,
                          double worldHeight,
                          GameWorld world,
                          HudView hud,
                          GameOverlayView overlay,
                          InputState input,
                          DoubleFunction<List<LevelDefinition>> campaignLoader,
                          PlayerCreator playerCreator) {
        Objects.requireNonNull(input, "input");

        PlayerMovement playerMovement = createPlayerMovement(worldWidth, worldHeight);
        this.sessionInputInterpreter = new SessionInputInterpreter(input);
        this.playerInputController = new PlayerInputController(input, playerMovement);

        List<LevelDefinition> campaignLevels = DefaultCampaignFactory.validateCampaign(
                Objects.requireNonNull(campaignLoader, "campaignLoader").apply(worldWidth)
        );
        this.runLifecycle = new GameRunLifecycle(
                worldWidth,
                worldHeight,
                Objects.requireNonNull(world, "world"),
                session,
                playerMovement,
                Objects.requireNonNull(playerCreator, "playerCreator"),
                new CampaignProgression(world, worldWidth, campaignLevels),
                new GameUiPresenter(
                        Objects.requireNonNull(hud, "hud"),
                        Objects.requireNonNull(overlay, "overlay")
                )
        );
        runLifecycle.initializeRun();
    }

    public void preUpdate(double dt) {
        handleSessionCommand(sessionInputInterpreter.poll(session.getState()));

        if (!session.isRunning()) {
            playerInputController.stop(runLifecycle.getPlayer());
            return;
        }

        playerInputController.update(runLifecycle.getRuntime());
        runLifecycle.updateSystems(dt);
    }

    public boolean shouldAdvanceWorld() {
        return session.isRunning();
    }

    public void postUpdate(double dt) {
        runLifecycle.postUpdate();
    }

    private static PlayerMovement createPlayerMovement(double worldWidth, double worldHeight) {
        double shipWidth = GameAssets.PLAYER_WIDTH;
        double shipHeight = GameAssets.PLAYER_HEIGHT;
        double movementMargin = 5;

        double minX = -movementMargin;
        double maxX = (worldWidth - shipWidth) + movementMargin;
        double minY = (worldHeight * 0.70) - movementMargin;
        double maxY = (worldHeight - shipHeight) + movementMargin;

        return new PlayerMovement(250, minX, maxX, minY, maxY);
    }

    private static PlayerShip createDefaultPlayer(PlayerMovement movement, double worldWidth, double worldHeight) {
        Objects.requireNonNull(movement, "movement");

        double spawnX = (worldWidth - GameAssets.PLAYER_WIDTH) / 2.0;
        double spawnY = worldHeight - GameAssets.PLAYER_HEIGHT - 14;

        return new PlayerShip(
                GameAssets.createPlayerView(),
                spawnX,
                spawnY,
                new PlayerStats(100),
                movement,
                new BasicWeapon()
        );
    }

    private void handleSessionCommand(SessionCommand command) {
        switch (Objects.requireNonNull(command, "command")) {
            case NONE -> {
            }
            case START_OR_CONTINUE -> {
                if (session.getState() == GameState.START_SCREEN) {
                    runLifecycle.startSession();
                } else if (session.getState().isTerminal()) {
                    playerInputController.reset();
                    runLifecycle.restartSession();
                }
            }
            case TOGGLE_PAUSE -> session.togglePause();
            case RESTART_RUN -> {
                playerInputController.reset();
                runLifecycle.restartSession();
            }
        }
    }

    @Override
    public void close() {
        runLifecycle.close();
    }
}
