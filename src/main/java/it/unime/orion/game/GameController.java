package it.unime.orion.game;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.input.InputState;
import it.unime.orion.level.CampaignFactory;
import it.unime.orion.level.CampaignValidator;
import it.unime.orion.level.DefaultCampaignFactory;
import it.unime.orion.level.DefaultCampaignValidator;
import it.unime.orion.level.LevelDefinition;
import it.unime.orion.ui.GameUiPresenter;
import it.unime.orion.ui.GameOverlayView;
import it.unime.orion.ui.HudView;
import it.unime.orion.world.GameWorld;

import java.util.List;
import java.util.Objects;

public final class GameController implements AutoCloseable {

    private final InputState input;
    private final GameSession session = new GameSession();
    private final PlayerMovement playerMovement;
    private final SessionInputInterpreter sessionInputInterpreter;
    private final PlayerInputController playerInputController;
    private final GameRunLifecycle runLifecycle;

    public GameController(double worldWidth,
                          double worldHeight,
                          GameWorld world,
                          HudView hud,
                          GameOverlayView overlay,
                          InputState input) {
        this(worldWidth, worldHeight, world, hud, overlay, input,
                new DefaultCampaignFactory(), new DefaultPlayerFactory(), new DefaultBossFactory(), new DefaultGameplayRuntimeFactory());
    }

    public GameController(double worldWidth,
                          double worldHeight,
                          GameWorld world,
                          HudView hud,
                          GameOverlayView overlay,
                          InputState input,
                          CampaignFactory campaignFactory) {
        this(worldWidth, worldHeight, world, hud, overlay, input,
                campaignFactory, new DefaultPlayerFactory(), new DefaultBossFactory(), new DefaultGameplayRuntimeFactory());
    }

    public GameController(double worldWidth,
                          double worldHeight,
                          GameWorld world,
                          HudView hud,
                          GameOverlayView overlay,
                          InputState input,
                          CampaignFactory campaignFactory,
                          PlayerFactory playerFactory,
                          BossFactory bossFactory,
                          GameplayRuntimeFactory runtimeFactory) {
        this(worldWidth, worldHeight, world, hud, overlay, input,
                campaignFactory, playerFactory, bossFactory, runtimeFactory, new DefaultCampaignValidator());
    }

    public GameController(double worldWidth,
                          double worldHeight,
                          GameWorld world,
                          HudView hud,
                          GameOverlayView overlay,
                          InputState input,
                          CampaignFactory campaignFactory,
                          PlayerFactory playerFactory,
                          BossFactory bossFactory,
                          GameplayRuntimeFactory runtimeFactory,
                          CampaignValidator campaignValidator) {
        this.input = Objects.requireNonNull(input, "input");

        this.playerMovement = createPlayerMovement(worldWidth, worldHeight);
        this.sessionInputInterpreter = new SessionInputInterpreter(input);
        this.playerInputController = new PlayerInputController(input, playerMovement);
        List<LevelDefinition> campaignLevels = Objects.requireNonNull(campaignValidator, "campaignValidator")
                .validateCampaign(Objects.requireNonNull(campaignFactory, "campaignFactory").createCampaign(worldWidth));
        CampaignProgression progression = new CampaignProgression(
                Objects.requireNonNull(world, "world"),
                worldWidth,
                campaignLevels,
                Objects.requireNonNull(bossFactory, "bossFactory")
        );
        GameUiPresenter uiPresenter = new GameUiPresenter(
                Objects.requireNonNull(hud, "hud"),
                Objects.requireNonNull(overlay, "overlay")
        );
        this.runLifecycle = new GameRunLifecycle(
                worldWidth,
                worldHeight,
                world,
                session,
                playerMovement,
                Objects.requireNonNull(playerFactory, "playerFactory"),
                Objects.requireNonNull(runtimeFactory, "runtimeFactory"),
                progression,
                uiPresenter
        );
        runLifecycle.initializeRun();
    }

    public void preUpdate(double dt) {
        // Process high-level session commands before player and world updates.
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

    private PlayerMovement createPlayerMovement(double worldWidth, double worldHeight) {
        double shipWidth = GameAssets.PLAYER_WIDTH;
        double shipHeight = GameAssets.PLAYER_HEIGHT;
        double movementMargin = 5;

        double minX = -movementMargin;
        double maxX = (worldWidth - shipWidth) + movementMargin;
        double minY = (worldHeight * 0.70) - movementMargin;
        double maxY = (worldHeight - shipHeight) + movementMargin;

        return new PlayerMovement(250, minX, maxX, minY, maxY);
    }

    private void handleSessionCommand(SessionCommand command) {
        switch (Objects.requireNonNull(command, "command")) {
            case NONE -> {
            }
            case START_OR_CONTINUE -> {
                if (session.getState() == GameState.START_SCREEN) {
                    runLifecycle.startSession();
                } else if (session.getState().isTerminal()) {
                    runLifecycle.restartSession();
                }
            }
            case TOGGLE_PAUSE -> session.togglePause();
            case RESTART_RUN -> runLifecycle.restartSession();
        }
    }

    @Override
    public void close() {
        runLifecycle.close();
    }
}
