package it.unime.orion.game;

public final class GameSession {

    private final ScoreCounter scoreCounter = new ScoreCounter();
    private final LifeCounter lifeCounter;
    private GameState state = GameState.START_SCREEN;
    private boolean bossSpawned;

    public GameSession() {
        this(3);
    }

    public GameSession(int initialLives) {
        this.lifeCounter = new LifeCounter(initialLives);
    }

    public GameState getState() {
        return state;
    }

    public int getScore() {
        return scoreCounter.getPoints();
    }

    public int getLivesLeft() {
        return lifeCounter.getCurrentLives();
    }

    public int getInitialLives() {
        return lifeCounter.getInitialLives();
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
        scoreCounter.reset();
        lifeCounter.reset();
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
        scoreCounter.addPoints(points);
    }

    public boolean gainLife() {
        return lifeCounter.gainLife();
    }

    public boolean handlePlayerDestroyed() {
        if (!state.allowsSimulation()) {
            return false;
        }

        if (lifeCounter.loseLife()) {
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
