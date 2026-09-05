package it.unime.orion.level;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unime.orion.assets.GameAssets;
import it.unime.orion.entities.boss.BossPhase;
import it.unime.orion.entities.boss.BossPhaseDefinition;
import it.unime.orion.entities.boss.BossTuning;
import it.unime.orion.errors.InvalidGameConfigurationException;
import it.unime.orion.powerups.PowerUpType;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public final class DefaultCampaignFactory {

    private static final String RESOURCE_PATH = "/campaign/default-campaign.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(DefaultCampaignFactory.class.getName());

    public List<LevelDefinition> createCampaign(double worldWidth) {
        CampaignDocument document = loadDocument();
        double bossMinX = document.bossArenaPaddingX();
        double bossMaxX = worldWidth - GameAssets.BOSS_WIDTH - document.bossArenaPaddingX();

        return document.levels().stream()
                .map(level -> toLevelDefinition(level, bossMinX, bossMaxX))
                .toList();
    }

    public static List<LevelDefinition> validateCampaign(List<LevelDefinition> levels) {
        Objects.requireNonNull(levels, "levels");

        List<LevelDefinition> snapshot = List.copyOf(levels);
        if (snapshot.isEmpty()) {
            throw validationFailure("Campaign must contain at least one level");
        }

        for (int index = 0; index < snapshot.size(); index++) {
            LevelDefinition level = snapshot.get(index);
            validateLevel(level, index + 1);
        }

        return snapshot;
    }

    private CampaignDocument loadDocument() {
        try (InputStream stream = DefaultCampaignFactory.class.getResourceAsStream(RESOURCE_PATH)) {
            if (stream == null) {
                throw new InvalidGameConfigurationException("Missing campaign resource: " + RESOURCE_PATH);
            }
            CampaignDocument document = OBJECT_MAPPER.readValue(stream, CampaignDocument.class);
            if (document.levels() == null || document.levels().isEmpty()) {
                throw new InvalidGameConfigurationException("Campaign resource must contain at least one level");
            }
            return document;
        } catch (IOException exception) {
            throw new InvalidGameConfigurationException("Failed to load campaign resource: " + RESOURCE_PATH, exception);
        }
    }

    private LevelDefinition toLevelDefinition(LevelPayload payload, double bossMinX, double bossMaxX) {
        Objects.requireNonNull(payload, "payload");
        return new LevelDefinition(
                payload.levelNumber(),
                payload.waves().stream()
                        .map(wave -> new Wave(wave.swarmCount(), wave.shooterCount(), wave.heavyCount()))
                        .toList(),
                new EnemyTuning(payload.enemyTuning().speedMultiplier(), payload.enemyTuning().fireRateMultiplier()),
                toBossTuning(payload.bossTuning(), bossMinX, bossMaxX),
                payload.bossAssetKey(),
                resolveWaveSpawnStrategy(payload.waveSpawnStrategy()),
                new LevelRuntimeTuning(
                        payload.runtimeTuning().ambientPowerUpSpawnIntervalSeconds(),
                        payload.runtimeTuning().maxAmbientPowerUps(),
                        resolveBossRewardType(payload.runtimeTuning().bossRewardType())
                )
        );
    }

    private BossTuning toBossTuning(BossTuningPayload payload, double bossMinX, double bossMaxX) {
        Objects.requireNonNull(payload, "payload");
        return new BossTuning(
                payload.maxHp(),
                payload.contactDamage(),
                payload.scoreValue(),
                payload.arenaY(),
                payload.entrySpeed(),
                payload.moveAmplitudeMultiplier(),
                payload.cooldownMultiplier(),
                payload.extraCannons(),
                bossMinX,
                bossMaxX,
                resolveBulletAssetPath(payload.bulletAsset()),
                payload.phases().stream().map(this::toBossPhaseDefinition).toList()
        );
    }

    private BossPhaseDefinition toBossPhaseDefinition(BossPhasePayload payload) {
        Objects.requireNonNull(payload, "payload");
        return new BossPhaseDefinition(
                resolveBossPhase(payload.phase()),
                payload.displayName(),
                payload.maxHpRatio(),
                payload.moveFrequency(),
                payload.moveAmplitudeMultiplier(),
                payload.cooldownSeconds(),
                payload.assaultDurationSeconds(),
                payload.chargeDurationSeconds(),
                payload.rainDurationSeconds(),
                payload.rainBulletCount(),
                payload.rainVerticalSpeed()
        );
    }

    private WaveSpawnStrategy resolveWaveSpawnStrategy(String strategyId) {
        return switch (normalizeId(strategyId)) {
            case "tiered" -> new TieredWaveSpawnStrategy();
            case "alternating_flank" -> new AlternatingFlankWaveSpawnStrategy();
            default -> throw new InvalidGameConfigurationException("Unsupported waveSpawnStrategy: " + strategyId);
        };
    }

    private Optional<PowerUpType> resolveBossRewardType(String rewardType) {
        if (rewardType == null || rewardType.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(PowerUpType.valueOf(normalizeEnum(rewardType)));
        } catch (IllegalArgumentException exception) {
            throw new InvalidGameConfigurationException("Unsupported boss reward type: " + rewardType, exception);
        }
    }

    private String resolveBulletAssetPath(String bulletAsset) {
        return switch (normalizeId(bulletAsset)) {
            case "enemy_red" -> GameAssets.getEnemyRedBulletAssetPath();
            case "enemy_crimson" -> GameAssets.getEnemyCrimsonBulletAssetPath();
            case "enemy_pink" -> GameAssets.getEnemyPinkBulletAssetPath();
            default -> throw new InvalidGameConfigurationException("Unsupported boss bullet asset: " + bulletAsset);
        };
    }

    private BossPhase resolveBossPhase(String phaseId) {
        try {
            return BossPhase.valueOf(normalizeEnum(phaseId));
        } catch (IllegalArgumentException exception) {
            throw new InvalidGameConfigurationException("Unsupported boss phase id: " + phaseId, exception);
        }
    }

    private String normalizeId(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidGameConfigurationException("Configuration id must not be blank");
        }
        return id.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeEnum(String value) {
        return normalizeId(value).replace('-', '_').toUpperCase(Locale.ROOT);
    }

    private static void validateLevel(LevelDefinition level, int expectedLevelNumber) {
        if (level == null) {
            throw validationFailure("Campaign must not contain null level definitions");
        }
        if (level.getLevelNumber() != expectedLevelNumber) {
            throw validationFailure(
                    "Campaign levels must be sequential starting from 1; expected level "
                            + expectedLevelNumber
                            + " but found "
                            + level.getLevelNumber()
            );
        }
        if (level.getBossAssetKey().isBlank()) {
            throw validationFailure("Level " + expectedLevelNumber + " must define a non-blank boss asset key");
        }
        if (level.getBossTuning().getScoreValue() <= 0) {
            throw validationFailure("Level " + expectedLevelNumber + " boss must award a positive score value");
        }
    }

    private static InvalidGameConfigurationException validationFailure(String message) {
        LOGGER.severe(message);
        return new InvalidGameConfigurationException(message);
    }

    private record CampaignDocument(double bossArenaPaddingX, List<LevelPayload> levels) {
    }

    private record LevelPayload(int levelNumber,
                                String bossAssetKey,
                                String waveSpawnStrategy,
                                List<WavePayload> waves,
                                EnemyTuningPayload enemyTuning,
                                BossTuningPayload bossTuning,
                                RuntimeTuningPayload runtimeTuning) {
    }

    private record WavePayload(int swarmCount, int shooterCount, int heavyCount) {
    }

    private record EnemyTuningPayload(double speedMultiplier, double fireRateMultiplier) {
    }

    private record BossTuningPayload(int maxHp,
                                     int contactDamage,
                                     int scoreValue,
                                     double arenaY,
                                     double entrySpeed,
                                     double moveAmplitudeMultiplier,
                                     double cooldownMultiplier,
                                     int extraCannons,
                                     String bulletAsset,
                                     List<BossPhasePayload> phases) {
    }

    private record BossPhasePayload(String phase,
                                    String displayName,
                                    double maxHpRatio,
                                    double moveFrequency,
                                    double moveAmplitudeMultiplier,
                                    double cooldownSeconds,
                                    double assaultDurationSeconds,
                                    double chargeDurationSeconds,
                                    double rainDurationSeconds,
                                    int rainBulletCount,
                                    double rainVerticalSpeed) {
    }

    private record RuntimeTuningPayload(double ambientPowerUpSpawnIntervalSeconds,
                                        int maxAmbientPowerUps,
                                        String bossRewardType) {
    }
}
