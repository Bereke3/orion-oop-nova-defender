package it.unime.orion.entities.boss;

import it.unime.orion.errors.InvalidGameConfigurationException;

import java.util.List;
import java.util.Objects;

public final class BossPhaseDefinition {

    private final BossPhase phase;
    private final String displayName;
    private final double maxHpRatio;
    private final double moveFrequency;
    private final double moveAmplitudeMultiplier;
    private final double cooldownSeconds;
    private final double assaultDurationSeconds;
    private final double chargeDurationSeconds;
    private final double rainDurationSeconds;
    private final int rainBulletCount;
    private final double rainVerticalSpeed;

    public BossPhaseDefinition(BossPhase phase,
                               String displayName,
                               double maxHpRatio,
                               double moveFrequency,
                               double moveAmplitudeMultiplier,
                               double cooldownSeconds,
                               double assaultDurationSeconds,
                               double chargeDurationSeconds,
                               double rainDurationSeconds,
                               int rainBulletCount,
                               double rainVerticalSpeed) {
        this.phase = Objects.requireNonNull(phase, "phase");
        if (displayName == null || displayName.isBlank()) {
            throw new InvalidGameConfigurationException("Boss phase displayName must not be blank");
        }
        if (maxHpRatio <= 0 || maxHpRatio > 1.0) {
            throw new InvalidGameConfigurationException("Boss phase maxHpRatio must be within (0, 1]");
        }
        if (moveFrequency <= 0) {
            throw new InvalidGameConfigurationException("Boss phase moveFrequency must be > 0");
        }
        if (moveAmplitudeMultiplier <= 0) {
            throw new InvalidGameConfigurationException("Boss phase moveAmplitudeMultiplier must be > 0");
        }
        if (cooldownSeconds <= 0) {
            throw new InvalidGameConfigurationException("Boss phase cooldownSeconds must be > 0");
        }
        if (assaultDurationSeconds <= 0) {
            throw new InvalidGameConfigurationException("Boss phase assaultDurationSeconds must be > 0");
        }
        if (chargeDurationSeconds <= 0) {
            throw new InvalidGameConfigurationException("Boss phase chargeDurationSeconds must be > 0");
        }
        if (rainDurationSeconds <= 0) {
            throw new InvalidGameConfigurationException("Boss phase rainDurationSeconds must be > 0");
        }
        if (rainBulletCount <= 0) {
            throw new InvalidGameConfigurationException("Boss phase rainBulletCount must be > 0");
        }
        if (rainVerticalSpeed <= 0) {
            throw new InvalidGameConfigurationException("Boss phase rainVerticalSpeed must be > 0");
        }

        this.displayName = displayName;
        this.maxHpRatio = maxHpRatio;
        this.moveFrequency = moveFrequency;
        this.moveAmplitudeMultiplier = moveAmplitudeMultiplier;
        this.cooldownSeconds = cooldownSeconds;
        this.assaultDurationSeconds = assaultDurationSeconds;
        this.chargeDurationSeconds = chargeDurationSeconds;
        this.rainDurationSeconds = rainDurationSeconds;
        this.rainBulletCount = rainBulletCount;
        this.rainVerticalSpeed = rainVerticalSpeed;
    }

    public BossPhase getPhase() {
        return phase;
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

    public double getAssaultDurationSeconds() {
        return assaultDurationSeconds;
    }

    public double getChargeDurationSeconds() {
        return chargeDurationSeconds;
    }

    public double getRainDurationSeconds() {
        return rainDurationSeconds;
    }

    public int getRainBulletCount() {
        return rainBulletCount;
    }

    public double getRainVerticalSpeed() {
        return rainVerticalSpeed;
    }

    public static List<BossPhaseDefinition> defaultDefinitions() {
        return List.of(
                new BossPhaseDefinition(BossPhase.PHASE_ONE, "Phase 1", 1.00, 0.95, 1.00, 1.45, 5.00, 2.00, 0.70, 8, 250),
                new BossPhaseDefinition(BossPhase.PHASE_TWO, "Phase 2", 0.75, 1.20, 1.18, 1.05, 4.45, 2.00, 0.70, 9, 266),
                new BossPhaseDefinition(BossPhase.PHASE_THREE, "Phase 3", 0.40, 1.48, 1.35, 0.82, 3.90, 2.00, 0.70, 10, 282)
        );
    }
}
