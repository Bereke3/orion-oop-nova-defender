package it.unime.orion.entities.boss;

import java.util.List;

final class BossPhaseResolver {

    private BossPhaseResolver() {
    }

    static BossPhaseDefinition resolve(List<BossPhaseDefinition> phases, int hp, int maxHp) {
        if (maxHp <= 0) {
            return phases.get(phases.size() - 1);
        }
        return resolve(phases, hp / (double) maxHp);
    }

    static BossPhaseDefinition resolve(List<BossPhaseDefinition> phases, double hpRatio) {
        BossPhaseDefinition resolved = phases.get(0);
        for (BossPhaseDefinition phase : phases) {
            if (hpRatio <= phase.getMaxHpRatio()) {
                resolved = phase;
            }
        }
        return resolved;
    }
}
