package it.unime.orion.entities.boss;

public enum BossPhase {
    PHASE_ONE("Phase 1", 1.00, 0.95, 1.00, 1.45),
    PHASE_TWO("Phase 2", 0.75, 1.20, 1.18, 1.05),
    PHASE_THREE("Phase 3", 0.40, 1.48, 1.35, 0.82);

    private final String displayName;
    private final double maxHpRatio;
    private final double moveFrequency;
    private final double moveAmplitudeMultiplier;
    private final double cooldownSeconds;

    BossPhase(String displayName,
              double maxHpRatio,
              double moveFrequency,
              double moveAmplitudeMultiplier,
              double cooldownSeconds) {
        this.displayName = displayName;
        this.maxHpRatio = maxHpRatio;
        this.moveFrequency = moveFrequency;
        this.moveAmplitudeMultiplier = moveAmplitudeMultiplier;
        this.cooldownSeconds = cooldownSeconds;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMaxHpRatio() {
        return maxHpRatio;
    }

    public double getMoveFrequency() {
        return moveFrequency;
    }

    public double getMoveAmplitudeMultiplier() {
        return moveAmplitudeMultiplier;
    }

    public double getCooldownSeconds() {
        return cooldownSeconds;
    }

    public static BossPhase forHpRatio(double hpRatio) {
        if (hpRatio <= PHASE_THREE.getMaxHpRatio()) {
            return PHASE_THREE;
        }
        if (hpRatio <= PHASE_TWO.getMaxHpRatio()) {
            return PHASE_TWO;
        }
        return PHASE_ONE;
    }

    public static BossPhase forHp(int hp, int maxHp) {
        if (maxHp <= 0) {
            return PHASE_THREE;
        }
        return forHpRatio(hp / (double) maxHp);
    }
}
