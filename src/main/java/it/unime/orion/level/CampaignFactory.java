package it.unime.orion.level;

import java.util.List;

public interface CampaignFactory {

    List<LevelDefinition> createCampaign(double worldWidth);
}
