package it.unime.orion.oop;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.entities.boss.BossTuning;
import it.unime.orion.errors.InvalidGameConfigurationException;
import it.unime.orion.events.DamageEvent;
import it.unime.orion.events.EventBus;
import it.unime.orion.events.PowerUpCollectedEvent;
import it.unime.orion.level.AlternatingFlankWaveSpawnStrategy;
import it.unime.orion.level.DefaultCampaignFactory;
import it.unime.orion.level.EnemyTuning;
import it.unime.orion.level.LevelDefinition;
import it.unime.orion.level.LevelRuntimeTuning;
import it.unime.orion.level.Wave;
import it.unime.orion.powerups.PowerUpType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ProjectOopEvidenceTest {

    @Test
    void testParametricPolymorphismUsesDifferentGenericEventTypes() {
        EventBus<DamageEvent> damageBus = new EventBus<>();
        EventBus<PowerUpCollectedEvent> powerUpBus = new EventBus<>();
        AtomicInteger damageEventsHandled = new AtomicInteger();
        AtomicReference<PowerUpType> lastCollectedPowerUp = new AtomicReference<>();

        damageBus.subscribe(event -> damageEventsHandled.incrementAndGet());
        powerUpBus.subscribe(event -> lastCollectedPowerUp.set(event.getPowerUpType()));

        damageBus.publish(new DamageEvent("player-1", 5));
        powerUpBus.publish(new PowerUpCollectedEvent("power-up-1", PowerUpType.SHIELD, 200, 100));

        assertEquals(1, damageEventsHandled.get());
        assertEquals(PowerUpType.SHIELD, lastCollectedPowerUp.get());
    }

    @Test
    void testOverloadedLevelDefinitionConstructorsKeepDefaultAndCustomizedConfigurationsSeparate() {
        LevelDefinition defaultLevel = new LevelDefinition(
                1,
                List.of(new Wave(1, 0, 0)),
                new EnemyTuning(1.0, 1.0),
                createDemoBossTuning(),
                "boss_a"
        );
        LevelDefinition customizedLevel = new LevelDefinition(
                2,
                List.of(new Wave(1, 1, 0)),
                new EnemyTuning(1.15, 1.1),
                createDemoBossTuning(),
                "boss_a_phase2",
                new AlternatingFlankWaveSpawnStrategy(),
                new LevelRuntimeTuning(5.5, 3, Optional.of(PowerUpType.SHIELD))
        );

        assertEquals("TieredWaveSpawnStrategy", defaultLevel.getWaveSpawnStrategy().getClass().getSimpleName());
        assertEquals("AlternatingFlankWaveSpawnStrategy", customizedLevel.getWaveSpawnStrategy().getClass().getSimpleName());
        assertEquals(Optional.of(PowerUpType.EXTRA_LIFE), defaultLevel.getRuntimeTuning().getBossRewardType());
        assertEquals(Optional.of(PowerUpType.SHIELD), customizedLevel.getRuntimeTuning().getBossRewardType());
    }

    @Test
    void testExceptionHandlingRejectsInvalidRuntimeTuning() {
        InvalidGameConfigurationException exception = assertThrows(
                InvalidGameConfigurationException.class,
                () -> new LevelRuntimeTuning(0, 2, Optional.of(PowerUpType.HEAL))
        );

        assertEquals("ambientPowerUpSpawnIntervalSeconds must be > 0", exception.getMessage());
    }

    @Test
    void testThirdLevelCustomizationIsLoadedFromCampaignConfiguration() {
        LevelDefinition level = new DefaultCampaignFactory().createCampaign(900).get(2);

        assertEquals(3, level.getLevelNumber());
        assertEquals("AlternatingFlankWaveSpawnStrategy", level.getWaveSpawnStrategy().getClass().getSimpleName());
        assertEquals(1.22, level.getEnemyTuning().getSpeedMultiplier());
        assertEquals(1.25, level.getEnemyTuning().getFireRateMultiplier());
        assertEquals(6.4, level.getRuntimeTuning().getAmbientPowerUpSpawnIntervalSeconds());
        assertEquals(3, level.getRuntimeTuning().getMaxAmbientPowerUps());
        assertEquals(Optional.of(PowerUpType.EXTRA_LIFE), level.getRuntimeTuning().getBossRewardType());
    }

    private BossTuning createDemoBossTuning() {
        return new BossTuning(
                10,
                5,
                250,
                30,
                100,
                1.0,
                1.0,
                0,
                0,
                500,
                GameAssets.getEnemyRedBulletAssetPath()
        );
    }
}
