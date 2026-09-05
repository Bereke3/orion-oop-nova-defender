package it.unime.orion.entities.boss;

import it.unime.orion.errors.InvalidGameConfigurationException;

import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

public final class BossTuning {

    private final int maxHp;
    private final int contactDamage;
    private final int scoreValue;
    private final double arenaY;
    private final double entrySpeed;
    private final double moveAmplitudeMultiplier;
    private final double cooldownMultiplier;
    private final int extraCannons;
    private final double arenaMinX;
    private final double arenaMaxX;
    private final String bulletAssetPath;
    private final List<BossPhaseDefinition> phases;
    private final EnumMap<BossPhase, BossPhaseDefinition> phaseById;

    public BossTuning(int maxHp,
                      int contactDamage,
                      int scoreValue,
                      double arenaY,
                      double entrySpeed,
                      double moveAmplitudeMultiplier,
                      double cooldownMultiplier,
                      int extraCannons,
                      double arenaMinX,
                      double arenaMaxX,
                      String bulletAssetPath) {
        this(maxHp, contactDamage, scoreValue, arenaY, entrySpeed, moveAmplitudeMultiplier,
                cooldownMultiplier, extraCannons, arenaMinX, arenaMaxX, bulletAssetPath,
                BossPhaseDefinition.defaultDefinitions());
    }

    public BossTuning(int maxHp,
                      int contactDamage,
                      int scoreValue,
                      double arenaY,
                      double entrySpeed,
                      double moveAmplitudeMultiplier,
                      double cooldownMultiplier,
                      int extraCannons,
                      double arenaMinX,
                      double arenaMaxX,
                      String bulletAssetPath,
                      List<BossPhaseDefinition> phases) {
        if (maxHp <= 0) {
            throw new InvalidGameConfigurationException("boss maxHp must be > 0");
        }
        if (contactDamage < 0) {
            throw new InvalidGameConfigurationException("boss contactDamage must be >= 0");
        }
        if (scoreValue < 0) {
            throw new InvalidGameConfigurationException("boss scoreValue must be >= 0");
        }
        if (arenaY < 0) {
            throw new InvalidGameConfigurationException("boss arenaY must be >= 0");
        }
        if (entrySpeed <= 0) {
            throw new InvalidGameConfigurationException("boss entrySpeed must be > 0");
        }
        if (moveAmplitudeMultiplier <= 0) {
            throw new InvalidGameConfigurationException("boss moveAmplitudeMultiplier must be > 0");
        }
        if (cooldownMultiplier <= 0) {
            throw new InvalidGameConfigurationException("boss cooldownMultiplier must be > 0");
        }
        if (extraCannons < 0) {
            throw new InvalidGameConfigurationException("boss extraCannons must be >= 0");
        }
        if (arenaMinX < 0) {
            throw new InvalidGameConfigurationException("boss arenaMinX must be >= 0");
        }
        if (arenaMaxX <= arenaMinX) {
            throw new InvalidGameConfigurationException("boss arenaMaxX must be > arenaMinX");
        }

        this.maxHp = maxHp;
        this.contactDamage = contactDamage;
        this.scoreValue = scoreValue;
        this.arenaY = arenaY;
        this.entrySpeed = entrySpeed;
        this.moveAmplitudeMultiplier = moveAmplitudeMultiplier;
        this.cooldownMultiplier = cooldownMultiplier;
        this.extraCannons = extraCannons;
        this.arenaMinX = arenaMinX;
        this.arenaMaxX = arenaMaxX;
        this.bulletAssetPath = Objects.requireNonNull(bulletAssetPath, "bulletAssetPath");
        this.phases = validatePhases(Objects.requireNonNull(phases, "phases"));
        this.phaseById = indexPhases(this.phases);
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getContactDamage() {
        return contactDamage;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public double getArenaY() {
        return arenaY;
    }

    public double getEntrySpeed() {
        return entrySpeed;
    }

    public double getMoveAmplitudeMultiplier() {
        return moveAmplitudeMultiplier;
    }

    public double getCooldownMultiplier() {
        return cooldownMultiplier;
    }

    public int getExtraCannons() {
        return extraCannons;
    }

    public double getArenaMinX() {
        return arenaMinX;
    }

    public double getArenaMaxX() {
        return arenaMaxX;
    }

    public String getBulletAssetPath() {
        return bulletAssetPath;
    }

    public List<BossPhaseDefinition> getPhases() {
        return phases;
    }

    public BossPhaseDefinition getPhaseDefinition(BossPhase phase) {
        return phaseById.get(Objects.requireNonNull(phase, "phase"));
    }

    public BossPhaseDefinition resolvePhase(int hp, int maxHp) {
        return BossPhaseResolver.resolve(phases, hp, maxHp);
    }

    public BossPhaseDefinition resolvePhaseRatio(double hpRatio) {
        return BossPhaseResolver.resolve(phases, hpRatio);
    }

    private List<BossPhaseDefinition> validatePhases(List<BossPhaseDefinition> phases) {
        List<BossPhaseDefinition> snapshot = List.copyOf(phases);
        if (snapshot.size() != BossPhase.values().length) {
            throw new InvalidGameConfigurationException("boss must define exactly " + BossPhase.values().length + " phases");
        }

        EnumMap<BossPhase, BossPhaseDefinition> definitions = new EnumMap<>(BossPhase.class);
        for (BossPhaseDefinition definition : snapshot) {
            BossPhaseDefinition previous = definitions.put(definition.getPhase(), definition);
            if (previous != null) {
                throw new InvalidGameConfigurationException("Duplicate configuration for " + definition.getPhase().name());
            }
        }

        List<BossPhaseDefinition> orderedDefinitions = new java.util.ArrayList<>();
        double previousThreshold = Double.POSITIVE_INFINITY;
        for (BossPhase expectedPhase : BossPhase.values()) {
            BossPhaseDefinition definition = definitions.get(expectedPhase);
            if (definition == null) {
                throw new InvalidGameConfigurationException("Missing configuration for " + expectedPhase.name());
            }

            if (definition.getMaxHpRatio() >= previousThreshold) {
                throw new InvalidGameConfigurationException("Boss phase maxHpRatio values must decrease from phase 1 to phase 3");
            }
            previousThreshold = definition.getMaxHpRatio();
            orderedDefinitions.add(definition);
        }

        if (Math.abs(orderedDefinitions.get(0).getMaxHpRatio() - 1.0) > 0.0001) {
            throw new InvalidGameConfigurationException("Boss phase 1 must start at 100% HP");
        }

        return List.copyOf(orderedDefinitions);
    }

    private EnumMap<BossPhase, BossPhaseDefinition> indexPhases(List<BossPhaseDefinition> phases) {
        EnumMap<BossPhase, BossPhaseDefinition> definitions = new EnumMap<>(BossPhase.class);
        for (BossPhaseDefinition phase : phases) {
            definitions.put(phase.getPhase(), phase);
        }
        return definitions;
    }
}
