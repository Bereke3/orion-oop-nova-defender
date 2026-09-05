package it.unime.orion.game;

import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.input.InputState;

import java.util.Objects;

public final class PlayerInputController {

    private static final int DEBUG_DAMAGE_AMOUNT = 10;

    private final InputState input;
    private final PlayerMovement movement;

    private boolean wasDamagePressed;
    private boolean wasFirePressed;

    public PlayerInputController(InputState input, PlayerMovement movement) {
        this.input = Objects.requireNonNull(input, "input");
        this.movement = Objects.requireNonNull(movement, "movement");
    }

    public void stop(PlayerShip player) {
        Objects.requireNonNull(player, "player").setVelocity(0, 0);
    }

    public void update(GameplayRuntime runtime) {
        Objects.requireNonNull(runtime, "runtime");
        PlayerShip player = runtime.getPlayer();

        double speed = movement.getSpeed();
        double vx = 0;
        double vy = 0;

        if (input.isMovingLeft()) {
            vx -= speed;
        }
        if (input.isMovingRight()) {
            vx += speed;
        }
        if (input.isMovingUp()) {
            vy -= speed * 0.4;
        }
        if (input.isMovingDown()) {
            vy += speed * 0.4;
        }

        player.setVelocity(vx, vy);

        boolean pressedDamage = input.isDebugDamagePressed();
        if (pressedDamage && !wasDamagePressed) {
            runtime.applyDebugDamageToPlayer(DEBUG_DAMAGE_AMOUNT);
        }
        wasDamagePressed = pressedDamage;

        boolean pressedFire = input.isFiring();
        if (pressedFire && !wasFirePressed) {
            runtime.addProjectiles(player.fire());
        }
        wasFirePressed = pressedFire;
    }

    public void reset() {
        wasDamagePressed = false;
        wasFirePressed = false;
    }
}
