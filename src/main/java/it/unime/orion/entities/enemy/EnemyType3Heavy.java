package it.unime.orion.entities.enemy;

import it.unime.orion.entities.enemy.behavior.HeavyBehavior;
import it.unime.orion.level.EnemyTuning;
import javafx.scene.Node;

public final class EnemyType3Heavy extends Enemy {

    public EnemyType3Heavy(Node view, double x, double y, double entryTargetY, EnemyTuning tuning) {
        super(view, x, y, 3, 15, 300, new HeavyBehavior(x, entryTargetY, tuning));
    }
}
