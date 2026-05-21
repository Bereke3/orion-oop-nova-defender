package it.unime.orion.level;

import it.unime.orion.errors.InvalidGameConfigurationException;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public final class DefaultCampaignValidator implements CampaignValidator {

    private static final Logger LOGGER = Logger.getLogger(DefaultCampaignValidator.class.getName());

    @Override
    public List<LevelDefinition> validateCampaign(List<LevelDefinition> levels) {
        Objects.requireNonNull(levels, "levels");

        List<LevelDefinition> snapshot = List.copyOf(levels);
        if (snapshot.isEmpty()) {
            throw validationFailure("Campaign must contain at least one level");
        }

        for (int index = 0; index < snapshot.size(); index++) {
            LevelDefinition level = snapshot.get(index);
            validateLevel(level, index + 1);
        }

        return snapshot;
    }

    private void validateLevel(LevelDefinition level, int expectedLevelNumber) {
        if (level == null) {
            throw validationFailure("Campaign must not contain null level definitions");
        }
        if (level.getLevelNumber() != expectedLevelNumber) {
            throw validationFailure(
                    "Campaign levels must be sequential starting from 1; expected level "
                            + expectedLevelNumber
                            + " but found "
                            + level.getLevelNumber()
            );
        }
        if (level.getBossAssetKey().isBlank()) {
            throw validationFailure("Level " + expectedLevelNumber + " must define a non-blank boss asset key");
        }
        if (level.getBossTuning().getScoreValue() <= 0) {
            throw validationFailure("Level " + expectedLevelNumber + " boss must award a positive score value");
        }
    }

    private InvalidGameConfigurationException validationFailure(String message) {
        LOGGER.severe(message);
        return new InvalidGameConfigurationException(message);
    }
}
