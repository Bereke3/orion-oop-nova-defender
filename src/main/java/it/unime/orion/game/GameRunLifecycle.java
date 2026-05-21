package it.unime.orion.game;

import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.level.LevelDefinition;
import it.unime.orion.ui.GameUiPresenter;
import it.unime.orion.world.GameWorld;

import java.util.Objects;

/**
 * Owns the lifecycle of one active run: player creation, runtime creation,
 * level-specific runtime tuning, restart flow, and UI refresh after gameplay updates.
 */
final class GameRunLifecycle implements AutoCloseable {

    private final double worldWidth;
    private final double worldHeight;
    private final GameWorld world;
    private final GameSession session;
    private final PlayerMovement playerMovement;
    private final PlayerFactory playerFactory;
    private final GameplayRuntimeFactory runtimeFactory;
    private final CampaignProgression progression;
    private final GameUiPresenter uiPresenter;

    private int appliedLevelNumber = -1;
    private GameplayRuntime runtime;

    GameRunLifecycle(double worldWidth,
                     double worldHeight,
                     GameWorld world,
                     GameSession session,
                     PlayerMovement playerMovement,
                     PlayerFactory playerFactory,
                     GameplayRuntimeFactory runtimeFactory,
                     CampaignProgression progression,
                     GameUiPresenter uiPresenter) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.world = Objects.requireNonNull(world, "world");
        this.session = Objects.requireNonNull(session, "session");
        this.playerMovement = Objects.requireNonNull(playerMovement, "playerMovement");
        this.playerFactory = Objects.requireNonNull(playerFactory, "playerFactory");
        this.runtimeFactory = Objects.requireNonNull(runtimeFactory, "runtimeFactory");
        this.progression = Objects.requireNonNull(progression, "progression");
        this.uiPresenter = Objects.requireNonNull(uiPresenter, "uiPresenter");
    }

    void initializeRun() {
        closeRunResources();
        progression.reset();
        PlayerShip player = playerFactory.create(playerMovement, worldWidth, worldHeight);
        runtime = runtimeFactory.create(world, session, player, worldWidth);
        appliedLevelNumber = -1;
        synchronizeLevelRuntimeTuning();
        refreshUi();
    }

    void startSession() {
        session.startGame();
        progression.startFirstWave();
        refreshUi();
    }

    void restartSession() {
        world.clearEntities();
        initializeRun();
        session.restartGame();
        progression.startFirstWave();
        refreshUi();
    }

    GameplayRuntime getRuntime() {
        return runtime;
    }

    PlayerShip getPlayer() {
        return runtime.getPlayer();
    }

    void updateSystems(double dt) {
        runtime.updateSystems(dt);
    }

    void postUpdate() {
        if (session.isRunning()) {
            runtime.resolveCollisions(worldHeight);

            if (!getPlayer().isAlive()) {
                if (session.handlePlayerDestroyed()) {
                    getPlayer().respawn();
                }
            } else {
                progression.update(session, getPlayer());
                synchronizeLevelRuntimeTuning();
            }
        }

        runtime.updateEffects();
        refreshUi();
    }

    @Override
    public void close() {
        closeRunResources();
    }

    private void synchronizeLevelRuntimeTuning() {
        LevelDefinition currentLevel = progression.getCurrentLevelDefinition();
        if (currentLevel.getLevelNumber() == appliedLevelNumber) {
            return;
        }
        runtime.applyLevelRuntimeTuning(currentLevel.getRuntimeTuning());
        appliedLevelNumber = currentLevel.getLevelNumber();
    }

    private void refreshUi() {
        uiPresenter.present(session, getPlayer(), progression.getBoss());
    }

    private void closeRunResources() {
        closeQuietly(runtime);
        runtime = null;
    }

    private void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to close game resource", exception);
        }
    }
}
