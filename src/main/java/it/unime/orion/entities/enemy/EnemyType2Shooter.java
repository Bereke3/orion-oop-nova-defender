package it.unime.orion.entities.enemy;

import it.unime.orion.entities.enemy.behavior.ShooterBehavior;
import it.unime.orion.level.EnemyTuning;
import javafx.scene.Node;

public final class EnemyType2Shooter extends Enemy {

    public EnemyType2Shooter(Node view, double x, double y, double entryTargetY, EnemyTuning tuning) {
        super(view, x, y, 2, 10, 175, new ShooterBehavior(x, entryTargetY, tuning));
    }
}
