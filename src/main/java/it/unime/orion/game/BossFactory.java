package it.unime.orion.game;

import it.unime.orion.entities.boss.BossA;
import it.unime.orion.level.LevelDefinition;

public interface BossFactory {

    BossA create(LevelDefinition level, double worldWidth);
}
