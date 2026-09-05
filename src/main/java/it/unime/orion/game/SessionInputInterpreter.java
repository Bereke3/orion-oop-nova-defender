package it.unime.orion.game;

import it.unime.orion.input.InputState;

import java.util.Objects;

public final class SessionInputInterpreter {

    private final InputState input;

    private boolean wasStartPressed;
    private boolean wasPausePressed;
    private boolean wasRestartPressed;

    public SessionInputInterpreter(InputState input) {
        this.input = Objects.requireNonNull(input, "input");
    }

    public SessionCommand poll(GameState state) {
        Objects.requireNonNull(state, "state");

        SessionCommand command = SessionCommand.NONE;

        boolean startPressed = input.isConfirmPressed();
        if (startPressed && !wasStartPressed
                && (state == GameState.START_SCREEN || state.isTerminal())) {
            command = SessionCommand.START_OR_CONTINUE;
        }
        wasStartPressed = startPressed;

        boolean pausePressed = input.isPausePressed();
        if (command == SessionCommand.NONE && pausePressed && !wasPausePressed) {
            command = SessionCommand.TOGGLE_PAUSE;
        }
        wasPausePressed = pausePressed;

        boolean restartPressed = input.isRestartPressed();
        if (command == SessionCommand.NONE && restartPressed && !wasRestartPressed && state.isTerminal()) {
            command = SessionCommand.RESTART_RUN;
        }
        wasRestartPressed = restartPressed;

        return command;
    }
}
