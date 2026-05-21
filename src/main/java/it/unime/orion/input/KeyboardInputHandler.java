package it.unime.orion.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.Objects;

public final class KeyboardInputHandler {

    private final InputState state;

    public KeyboardInputHandler(InputState state) {
        this.state = Objects.requireNonNull(state, "state");
    }

    public void attach(Scene scene) {
        Objects.requireNonNull(scene, "scene");

        scene.setOnKeyPressed(e -> setKey(e.getCode(), true));
        scene.setOnKeyReleased(e -> setKey(e.getCode(), false));
    }

    private void setKey(KeyCode code, boolean pressed) {
        InputAction action = mapKey(code);
        if (action == null) {
            return;
        }

        if (pressed) {
            state.press(action);
        } else {
            state.release(action);
        }
    }

    private InputAction mapKey(KeyCode code) {
        return switch (code) {
            case LEFT -> InputAction.MOVE_LEFT;
            case RIGHT -> InputAction.MOVE_RIGHT;
            case UP -> InputAction.MOVE_UP;
            case DOWN -> InputAction.MOVE_DOWN;
            case SPACE -> InputAction.FIRE;
            case ENTER -> InputAction.CONFIRM;
            case P, ESCAPE -> InputAction.PAUSE;
            case R -> InputAction.RESTART;
            case D -> InputAction.DEBUG_DAMAGE;
            default -> null;
        };
    }
}
