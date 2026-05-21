package it.unime.orion.game;

import it.unime.orion.errors.InvalidGameConfigurationException;

public final class LifeCounter {

    private final int initialLives;
    private int currentLives;

    public LifeCounter(int initialLives) {
        if (initialLives <= 0) {
            throw new InvalidGameConfigurationException("initialLives must be > 0");
        }
        this.initialLives = initialLives;
        this.currentLives = initialLives;
    }

    public int getCurrentLives() {
        return currentLives;
    }

    public int getInitialLives() {
        return initialLives;
    }

    public boolean hasLivesRemaining() {
        return currentLives > 0;
    }

    public boolean loseLife() {
        if (!hasLivesRemaining()) {
            return false;
        }

        currentLives--;
        return hasLivesRemaining();
    }

    public boolean gainLife() {
        if (currentLives >= initialLives) {
            return false;
        }

        currentLives++;
        return true;
    }

    public void reset() {
        currentLives = initialLives;
    }
}
