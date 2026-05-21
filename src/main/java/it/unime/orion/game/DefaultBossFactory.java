package it.unime.orion.game;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.entities.boss.BossA;
import it.unime.orion.entities.boss.BossPhase;
import it.unime.orion.level.LevelDefinition;

import java.util.Objects;

public final class DefaultBossFactory implements BossFactory {

    @Override
    public BossA create(LevelDefinition level, double worldWidth) {
        Objects.requireNonNull(level, "level");

        double spawnX = (worldWidth - GameAssets.BOSS_WIDTH) / 2.0;
        double spawnY = -GameAssets.BOSS_HEIGHT;

        return new BossA(
                GameAssets.createBossView(level.getBossAssetKey(), BossPhase.PHASE_ONE),
                spawnX,
                spawnY,
                level.getBossTuning()
        );
    }
}
