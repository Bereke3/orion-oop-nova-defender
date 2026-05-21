package it.unime.orion.game;

public enum GameState {
    START_SCREEN("Start screen"),
    RUNNING("Running"),
    PAUSED("Paused"),
    GAME_OVER("Game over"),
    VICTORY("Victory");

    private final String displayName;

    GameState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean allowsSimulation() {
        return this == RUNNING;
    }

    public boolean showsOverlay() {
        return this != RUNNING;
    }

    public boolean isTerminal() {
        return this == GAME_OVER || this == VICTORY;
    }
}
