package it.unime.orion.entities.enemy;

import it.unime.orion.entities.enemy.behavior.SwarmBehavior;
import it.unime.orion.level.EnemyTuning;
import javafx.scene.Node;

public final class EnemyType1Swarm extends Enemy {

    public EnemyType1Swarm(Node view, double x, double y, double entryTargetY, EnemyTuning tuning) {
        super(view, x, y, 1, 10, 100, new SwarmBehavior(x, entryTargetY, tuning.getSpeedMultiplier()));
    }
}
