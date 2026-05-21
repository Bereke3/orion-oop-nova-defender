package it.unime.orion.level;

import it.unime.orion.assets.GameAssets;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public final class DefaultCampaignFactoryTest {

    @Test
    void testFactoryLoadsCampaignFromExternalResource() {
        List<LevelDefinition> levels = new DefaultCampaignFactory().createCampaign(900);

        assertEquals(4, levels.size());
        assertEquals("boss_a", levels.get(0).getBossAssetKey());
        assertInstanceOf(AlternatingFlankWaveSpawnStrategy.class, levels.get(2).getWaveSpawnStrategy());
        assertEquals(GameAssets.getEnemyPinkBulletAssetPath(), levels.get(3).getBossTuning().getBulletAssetPath());
    }
}
