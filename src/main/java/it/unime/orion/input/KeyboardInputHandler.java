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
        switch (code) {
            case LEFT -> state.left = pressed;
            case RIGHT -> state.right = pressed;
            case UP -> state.up = pressed;
            case DOWN -> state.down = pressed;
            case SPACE -> state.fire = pressed;
            case D -> state.debugDamage = pressed;
        }
    }
}