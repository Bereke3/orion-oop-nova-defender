package it.unime.orion.level;

import java.util.List;

public interface CampaignValidator {

    List<LevelDefinition> validateCampaign(List<LevelDefinition> levels);
}
