package it.unime.orion.entities.boss;

public enum BossPhase {
    PHASE_ONE,
    PHASE_TWO,
    PHASE_THREE;

    public static BossPhase forHpRatio(double hpRatio) {
        return BossPhaseResolver.resolve(BossPhaseDefinition.defaultDefinitions(), hpRatio).getPhase();
    }

    public static BossPhase forHp(int hp, int maxHp) {
        return BossPhaseResolver.resolve(BossPhaseDefinition.defaultDefinitions(), hp, maxHp).getPhase();
    }
}
