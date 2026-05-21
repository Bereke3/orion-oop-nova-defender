package it.unime.orion.entities.boss;

import it.unime.orion.entities.enemy.Enemy;
import it.unime.orion.entities.enemy.behavior.BossPhaseBehavior;
import javafx.scene.Node;

public final class BossA extends Enemy {

    private final BossTuning tuning;

    public BossA(Node view, double x, double y, BossTuning tuning) {
        super(view, x, y, tuning.getMaxHp(), tuning.getContactDamage(), tuning.getScoreValue(), new BossPhaseBehavior(tuning));
        this.tuning = tuning;
    }

    public BossPhase getCurrentPhase() {
        return tuning.resolvePhase(getHp(), getMaxHp()).getPhase();
    }

    public BossPhaseDefinition getCurrentPhaseDefinition() {
        return tuning.resolvePhase(getHp(), getMaxHp());
    }

    public String getCurrentPhaseName() {
        return getCurrentPhaseDefinition().getDisplayName();
    }

    public int getBossHp() {
        return getHp();
    }

    public int getBossMaxHp() {
        return getMaxHp();
    }

    @Override
    public boolean isBoss() {
        return true;
    }
}
