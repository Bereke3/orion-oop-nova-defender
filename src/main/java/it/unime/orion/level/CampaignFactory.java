package it.unime.orion.level;

import java.util.List;

/**
 * Builds the ordered campaign definition used by the main game flow.
 * Implementations may load levels from code, resources, or external configuration,
 * but must return a fully materialized list ready for validation.
 */
public interface CampaignFactory {

    List<LevelDefinition> createCampaign(double worldWidth);
}
