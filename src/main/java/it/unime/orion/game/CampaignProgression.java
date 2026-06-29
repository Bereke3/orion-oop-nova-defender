package it.unime.orion.game;

import it.unime.orion.entities.GameEntity;
import it.unime.orion.entities.boss.BossA;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.level.LevelDefinition;
import it.unime.orion.level.WaveManager;
import it.unime.orion.powerups.PowerUp;
import it.unime.orion.world.GameWorld;

import java.util.List;
import java.util.Objects;

public final class CampaignProgression {

    private final GameWorld world;
    private final double worldWidth;
    private final List<LevelDefinition> campaignLevels;
    private final BossFactory bossFactory;

    private int currentLevelIndex;
    private WaveManager waveManager;
    private BossA boss;
    private boolean firstWaveStarted;

    public CampaignProgression(GameWorld world,
                               double worldWidth,
                               List<LevelDefinition> campaignLevels,
                               BossFactory bossFactory) {
        this.world = Objects.requireNonNull(world, "world");
        this.worldWidth = worldWidth;
        this.campaignLevels = List.copyOf(Objects.requireNonNull(campaignLevels, "campaignLevels"));
        this.bossFactory = Objects.requireNonNull(bossFactory, "bossFactory");
        reset();
    }

    public void reset() {
        currentLevelIndex = 0;
        boss = null;
        firstWaveStarted = false;
        waveManager = createWaveManagerForCurrentLevel();
    }

    public BossA getBoss() {
        return boss;
    }

    public LevelDefinition getCurrentLevelDefinition() {
        return getCurrentLevel();
    }

    public void startFirstWave() {
        if (firstWaveStarted) {
            return;
        }
        waveManager.startNextWave();
        firstWaveStarted = true;
    }

    public void update(GameSession session, PlayerShip player) {
        Objects.requireNonNull(session, "session");
        Objects.requireNonNull(player, "player");

        if (session.hasBossSpawned()) {
            if (isBossEncounterResolved()) {
                handleBossDefeated(session, player);
            }
            return;
        }

        if (!firstWaveStarted || !waveManager.isCurrentWaveCleared()) {
            return;
        }

        if (waveManager.hasMoreWaves()) {
            waveManager.startNextWave();
            return;
        }

        spawnBoss(session);
    }

    private void spawnBoss(GameSession session) {
        boss = bossFactory.create(getCurrentLevel(), worldWidth);
        world.addEntity(boss);
        session.markBossSpawned();
    }

    private WaveManager createWaveManagerForCurrentLevel() {
        LevelDefinition level = getCurrentLevel();
        return new WaveManager(world, worldWidth, level.getWaves(), level.getEnemyTuning(), level.getWaveSpawnStrategy());
    }

    private LevelDefinition getCurrentLevel() {
        return campaignLevels.get(currentLevelIndex);
    }

    private boolean isBossEncounterResolved() {
        return boss != null && (!boss.isAlive() || !world.getEntitiesView().contains(boss));
    }

    private void handleBossDefeated(GameSession session, PlayerShip player) {
        if (currentLevelIndex + 1 >= campaignLevels.size()) {
            boss = null;
            session.markVictory();
            return;
        }

        clearNonPlayerEntities(player);
        currentLevelIndex++;
        boss = null;
        firstWaveStarted = false;
        waveManager = createWaveManagerForCurrentLevel();
        session.prepareNextLevel();
        startFirstWave();
    }

    private void clearNonPlayerEntities(PlayerShip player) {
        for (GameEntity entity : List.copyOf(world.getEntitiesView())) {
            if (entity == player || entity instanceof PowerUp) {
                continue;
            }
            world.removeEntity(entity);
        }
    }
}
