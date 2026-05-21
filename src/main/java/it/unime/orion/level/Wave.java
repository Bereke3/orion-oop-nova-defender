package it.unime.orion.level;

import it.unime.orion.errors.InvalidGameConfigurationException;

public final class Wave {

    private final int swarmCount;
    private final int shooterCount;
    private final int heavyCount;

    public Wave(int swarmCount, int shooterCount, int heavyCount) {
        if (swarmCount < 0 || shooterCount < 0 || heavyCount < 0) {
            throw new InvalidGameConfigurationException("Wave enemy counts must be >= 0");
        }
        if (swarmCount + shooterCount + heavyCount == 0) {
            throw new InvalidGameConfigurationException("Wave must contain at least one enemy");
        }
        this.swarmCount = swarmCount;
        this.shooterCount = shooterCount;
        this.heavyCount = heavyCount;
    }

    public int getSwarmCount() {
        return swarmCount;
    }

    public int getShooterCount() {
        return shooterCount;
    }

    public int getHeavyCount() {
        return heavyCount;
    }
}
