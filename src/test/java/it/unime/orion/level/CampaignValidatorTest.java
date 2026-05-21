package it.unime.orion.level;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.entities.boss.BossTuning;
import it.unime.orion.errors.InvalidGameConfigurationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class CampaignValidatorTest {

    private final CampaignValidator validator = new DefaultCampaignValidator();

    @Test
    void testValidatorRejectsEmptyCampaign() {
        try {
            validator.validateCampaign(List.of());
            fail("CampaignValidator must reject empty campaigns");
        } catch (InvalidGameConfigurationException expected) {
            assertTrue(expected.getMessage().contains("at least one level"));
        }
    }

    @Test
    void testValidatorRejectsNonSequentialLevels() {
        try {
            validator.validateCampaign(List.of(createLevel(1, "boss_a"), createLevel(3, "boss_a_phase2")));
            fail("CampaignValidator must reject campaigns with level gaps");
        } catch (InvalidGameConfigurationException expected) {
            assertTrue(expected.getMessage().contains("sequential"));
        }
    }

    @Test
    void testValidatorRejectsBlankBossAssetKey() {
        try {
            validator.validateCampaign(List.of(createLevel(1, "   ")));
            fail("CampaignValidator must reject blank boss asset keys");
        } catch (InvalidGameConfigurationException expected) {
            assertTrue(expected.getMessage().contains("boss asset key"));
        }
    }

    @Test
    void testDefaultCampaignFactoryProducesValidCampaign() {
        List<LevelDefinition> levels = validator.validateCampaign(new DefaultCampaignFactory().createCampaign(900));

        assertEquals(4, levels.size());
        assertEquals(1, levels.get(0).getLevelNumber());
        assertEquals(4, levels.get(3).getLevelNumber());
    }

    private LevelDefinition createLevel(int levelNumber, String bossAssetKey) {
        return new LevelDefinition(
                levelNumber,
                List.of(new Wave(1, 0, 0)),
                new EnemyTuning(1.0, 1.0),
                new BossTuning(10, 0, 100, 30, 100, 1.0, 1.0, 0, 0, 500, GameAssets.getEnemyRedBulletAssetPath()),
                bossAssetKey,
                (world, worldWidth, waveIndex, wave, tuning) -> {
                }
        );
    }
}
