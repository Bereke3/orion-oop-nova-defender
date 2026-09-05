package it.unime.orion.game;

import it.unime.orion.errors.InvalidGameConfigurationException;

public final class GameSession {

    private final int initialLives;
    private int score;
    private int currentLives;
    private GameState state = GameState.START_SCREEN;
    private boolean bossSpawned;

    public GameSession() {
        this(3);
    }

    public GameSession(int initialLives) {
        if (initialLives <= 0) {
            throw new InvalidGameConfigurationException("initialLives must be > 0");
        }
        this.initialLives = initialLives;
        this.currentLives = initialLives;
    }

    public GameState getState() {
        return state;
    }

    public int getScore() {
        return score;
    }

    public int getLivesLeft() {
        return currentLives;
    }

    public int getInitialLives() {
        return initialLives;
    }

    public boolean isRunning() {
        return state.allowsSimulation();
    }

    public void startGame() {
        if (state != GameState.START_SCREEN) {
            return;
        }
        beginNewRun();
    }

    public void restartGame() {
        if (!state.isTerminal()) {
            return;
        }
        beginNewRun();
    }

    private void beginNewRun() {
        score = 0;
        currentLives = initialLives;
        bossSpawned = false;
        state = GameState.RUNNING;
    }

    public void togglePause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
        }
    }

    public void addScore(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("points must be >= 0");
        }
        score += points;
    }

    public boolean gainLife() {
        if (currentLives >= initialLives) {
            return false;
        }
        currentLives++;
        return true;
    }

    public boolean handlePlayerDestroyed() {
        if (!state.allowsSimulation()) {
            return false;
        }

        if (currentLives > 0) {
            currentLives--;
        }

        if (currentLives > 0) {
            return true;
        }

        state = GameState.GAME_OVER;
        return false;
    }

    public boolean hasBossSpawned() {
        return bossSpawned;
    }

    public void markBossSpawned() {
        bossSpawned = true;
    }

    public void prepareNextLevel() {
        bossSpawned = false;
    }

    public void markGameOver() {
        state = GameState.GAME_OVER;
    }

    public void markVictory() {
        state = GameState.VICTORY;
    }
}
