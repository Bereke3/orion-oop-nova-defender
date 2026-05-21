package it.unime.orion.game;

import it.unime.orion.input.InputAction;
import it.unime.orion.input.InputState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SessionInputInterpreterTest {

    @Test
    void testConfirmStartsRunOnlyOnKeyEdge() {
        InputState input = new InputState();
        SessionInputInterpreter interpreter = new SessionInputInterpreter(input);

        input.press(InputAction.CONFIRM);
        assertEquals(SessionCommand.START_OR_CONTINUE, interpreter.poll(GameState.START_SCREEN));
        assertEquals(SessionCommand.NONE, interpreter.poll(GameState.START_SCREEN));
    }

    @Test
    void testPauseTogglesOnlyOnKeyEdge() {
        InputState input = new InputState();
        SessionInputInterpreter interpreter = new SessionInputInterpreter(input);

        input.press(InputAction.PAUSE);
        assertEquals(SessionCommand.TOGGLE_PAUSE, interpreter.poll(GameState.RUNNING));
        assertEquals(SessionCommand.NONE, interpreter.poll(GameState.RUNNING));
    }

    @Test
    void testRestartWorksOnlyInTerminalState() {
        InputState input = new InputState();
        SessionInputInterpreter interpreter = new SessionInputInterpreter(input);

        input.press(InputAction.RESTART);
        assertEquals(SessionCommand.NONE, interpreter.poll(GameState.RUNNING));
        input.release(InputAction.RESTART);
        interpreter.poll(GameState.RUNNING);

        input.press(InputAction.RESTART);
        assertEquals(SessionCommand.RESTART_RUN, interpreter.poll(GameState.GAME_OVER));
    }
}
