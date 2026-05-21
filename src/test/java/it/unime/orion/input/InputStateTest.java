package it.unime.orion.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class InputStateTest {

    @Test
    void testInputStateTracksPressedActionsThroughQueries() {
        InputState inputState = new InputState();

        inputState.press(InputAction.MOVE_LEFT);
        inputState.press(InputAction.FIRE);

        assertTrue(inputState.isMovingLeft());
        assertTrue(inputState.isFiring());
        assertFalse(inputState.isPausePressed());

        inputState.release(InputAction.MOVE_LEFT);
        assertFalse(inputState.isMovingLeft());
        assertTrue(inputState.isFiring());
    }
}
