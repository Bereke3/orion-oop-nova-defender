package it.unime.orion.input;

import java.util.EnumSet;

public final class InputState {

    private final EnumSet<InputAction> pressedActions = EnumSet.noneOf(InputAction.class);

    public void press(InputAction action) {
        pressedActions.add(action);
    }

    public void release(InputAction action) {
        pressedActions.remove(action);
    }

    public boolean isPressed(InputAction action) {
        return pressedActions.contains(action);
    }

    public boolean isMovingLeft() {
        return isPressed(InputAction.MOVE_LEFT);
    }

    public boolean isMovingRight() {
        return isPressed(InputAction.MOVE_RIGHT);
    }

    public boolean isMovingUp() {
        return isPressed(InputAction.MOVE_UP);
    }

    public boolean isMovingDown() {
        return isPressed(InputAction.MOVE_DOWN);
    }

    public boolean isFiring() {
        return isPressed(InputAction.FIRE);
    }

    public boolean isDebugDamagePressed() {
        return isPressed(InputAction.DEBUG_DAMAGE);
    }

    public boolean isConfirmPressed() {
        return isPressed(InputAction.CONFIRM);
    }

    public boolean isPausePressed() {
        return isPressed(InputAction.PAUSE);
    }

    public boolean isRestartPressed() {
        return isPressed(InputAction.RESTART);
    }
}
