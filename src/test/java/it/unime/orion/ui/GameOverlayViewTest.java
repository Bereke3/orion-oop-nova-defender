package it.unime.orion.ui;

import it.unime.orion.game.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class GameOverlayViewTest {

    @Test
    void testStartScreenOverlayShowsExpectedTexts() {
        GameOverlayView overlay = new GameOverlayView(900, 600);

        overlay.update(GameState.START_SCREEN, 0);

        assertTrue(overlay.getRoot().isVisible());
        assertEquals("Nova Defender", overlay.getDisplayedTitle());
        assertEquals("Press ENTER to begin the full campaign.", overlay.getDisplayedDetail());
        assertTrue(overlay.getDisplayedHint().contains("SPACE fire"));
    }

    @Test
    void testRunningStateHidesOverlay() {
        GameOverlayView overlay = new GameOverlayView(900, 600);

        overlay.update(GameState.RUNNING, 42);

        assertFalse(overlay.getRoot().isVisible());
    }

    @Test
    void testGameOverOverlayDisplaysFinalScore() {
        GameOverlayView overlay = new GameOverlayView(900, 600);

        overlay.update(GameState.GAME_OVER, 1250);

        assertEquals("Game Over", overlay.getDisplayedTitle());
        assertEquals("Final score: 1250", overlay.getDisplayedDetail());
    }
}
