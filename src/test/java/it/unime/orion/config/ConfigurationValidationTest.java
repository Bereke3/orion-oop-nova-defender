package it.unime.orion.config;

import it.unime.orion.entities.boss.BossTuning;
import it.unime.orion.entities.boss.BossPhase;
import it.unime.orion.entities.boss.BossPhaseDefinition;
import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerStats;
import it.unime.orion.errors.InvalidGameConfigurationException;
import it.unime.orion.level.EnemyTuning;
import it.unime.orion.level.LevelDefinition;
import it.unime.orion.level.LevelRuntimeTuning;
import it.unime.orion.level.Wave;
import it.unime.orion.powerups.PowerUpType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public final class ConfigurationValidationTest {

    @Test
    void testPlayerStatsRejectsInvalidMaxHp() {
        try {
            new PlayerStats(0);
            fail("PlayerStats must reject non-positive maxHp");
        } catch (InvalidGameConfigurationException expected) {
            assertTrue(expected.getMessage().contains("maxHp"));
        }
    }

    @Test
    void testPlayerMovementRejectsInvalidBounds() {
        try {
            new PlayerMovement(250, 100, 50, 420, 560);
            fail("PlayerMovement must reject inverted X bounds");
        } catch (InvalidGameConfigurationException expected) {
            assertTrue(expected.getMessage().contains("minX"));
        }
    }

    @Test
    void testWaveRejectsEmptyConfiguration() {
        try {
            new Wave(0, 0, 0);
            fail("Wave must reject empty enemy configurations");
        } catch (InvalidGameConfigurationException expected) {
            assertTrue(expected.getMessage().contains("at least one enemy"));
        }
    }

    @Test
    void testDamageRejectsNegativeValues() {
        PlayerStats stats = new PlayerStats(100);

        try {
            stats.damage(-1);
            fail("Negative damage values must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("damage"));
        }
    }

    @Test
    void testEnemyTuningRejectsInvalidSpeedMultiplier() {
        try {
            new EnemyTuning(0, 1.0);
            fail("EnemyTuning must reject non-positive speed multipliers");
        } catch (InvalidGameConfigurationException expected) {
            assertTrue(expected.getMessage().contains("speedMultiplier"));
        }
    }

    @Test
    void testBossTuningRejectsInvalidExtraCannons() {
        try {
            new BossTuning(20, 10, 1000, 30, 120, 1.0, 1.0, -1, 50, 400, "/images/test.png");
            fail("BossTuning must reject negative extra cannons");
        } catch (InvalidGameConfigurationException expected) {
            assertTrue(expected.getMessage().contains("extraCannons"));
        }
    }

    @Test
    void testLevelDefinitionRejectsEmptyWaveList() {
        try {
            new LevelDefinition(
                    1,
                    List.of(),
                    new EnemyTuning(1.0, 1.0),
                    new BossTuning(20, 10, 1000, 30, 120, 1.0, 1.0, 0, 50, 400, "/images/test.png"),
                    "boss_a"
            );
            fail("LevelDefinition must reject empty wave lists");
        } catch (InvalidGameConfigurationException expected) {
            assertTrue(expected.getMessage().contains("at least one wave"));
        }
    }

    @Test
    void testBossTuningRejectsIncompletePhaseConfiguration() {
        try {
            new BossTuning(
                    20,
                    10,
                    1000,
                    30,
                    120,
                    1.0,
                    1.0,
                    0,
                    50,
                    400,
                    "/images/test.png",
                    List.of(
                            new BossPhaseDefinition(BossPhase.PHASE_ONE, "Alpha", 1.0, 1.0, 1.0, 1.2, 4.0, 1.5, 0.6, 7, 240),
                            new BossPhaseDefinition(BossPhase.PHASE_TWO, "Beta", 0.6, 1.2, 1.2, 0.9, 3.8, 1.5, 0.6, 8, 260)
                    )
            );
            fail("BossTuning must reject incomplete phase configuration");
        } catch (InvalidGameConfigurationException expected) {
            assertTrue(expected.getMessage().contains("exactly"));
        }
    }

    @Test
    void testLevelRuntimeTuningRejectsNegativePowerUpLimit() {
        try {
            new LevelRuntimeTuning(8.0, -1, Optional.of(PowerUpType.EXTRA_LIFE));
            fail("LevelRuntimeTuning must reject negative power-up limits");
        } catch (InvalidGameConfigurationException expected) {
            assertTrue(expected.getMessage().contains("maxAmbientPowerUps"));
        }
    }
}
