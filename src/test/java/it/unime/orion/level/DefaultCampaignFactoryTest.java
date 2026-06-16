package it.unime.orion.level;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.powerups.PowerUpType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public final class DefaultCampaignFactoryTest {

    @Test
    void testFactoryLoadsCampaignFromExternalResource() {
        List<LevelDefinition> levels = new DefaultCampaignFactory().createCampaign(900);

        assertEquals(4, levels.size());
        assertEquals("boss_a", levels.get(0).getBossAssetKey());
        assertInstanceOf(AlternatingFlankWaveSpawnStrategy.class, levels.get(2).getWaveSpawnStrategy());
        assertEquals(GameAssets.getEnemyPinkBulletAssetPath(), levels.get(3).getBossTuning().getBulletAssetPath());
        assertEquals(Optional.of(PowerUpType.SHIELD), levels.get(1).getRuntimeTuning().getBossRewardType());
        assertEquals(Optional.of(PowerUpType.EXTRA_LIFE), levels.get(2).getRuntimeTuning().getBossRewardType());
        assertEquals(Optional.of(PowerUpType.EXTRA_LIFE), levels.get(3).getRuntimeTuning().getBossRewardType());
        assertNotEquals(
                levels.get(0).getRuntimeTuning().getBossRewardType(),
                levels.get(1).getRuntimeTuning().getBossRewardType()
        );
    }
}
