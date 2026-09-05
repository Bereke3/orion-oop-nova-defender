package it.unime.orion.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class GameSessionTest {

    @Test
    void testSessionStartsOnStartScreen() {
        GameSession session = new GameSession();

        assertEquals(GameState.START_SCREEN, session.getState());
        assertEquals(0, session.getScore());
        assertFalse(session.isRunning());
    }

    @Test
    void testStartAndPauseTransitions() {
        GameSession session = new GameSession();

        session.startGame();
        assertEquals(GameState.RUNNING, session.getState());
        assertTrue(session.isRunning());

        session.togglePause();
        assertEquals(GameState.PAUSED, session.getState());

        session.togglePause();
        assertEquals(GameState.RUNNING, session.getState());
    }

    @Test
    void testScoreCounterRejectsNegativeValues() {
        GameSession session = new GameSession();

        try {
            session.addScore(-10);
            fail("Negative score updates must be rejected");
        } catch (IllegalArgumentException expected) {
            assertEquals(0, session.getScore());
        }
    }

    @Test
    void testRestartGameResetsScoreAndBossStateAfterGameOver() {
        GameSession session = new GameSession();

        session.startGame();
        session.addScore(450);
        session.markBossSpawned();
        session.markGameOver();

        session.restartGame();

        assertEquals(GameState.RUNNING, session.getState());
        assertEquals(0, session.getScore());
        assertFalse(session.hasBossSpawned());
    }

    @Test
    void testRestartGameResetsScoreAndBossStateAfterVictory() {
        GameSession session = new GameSession();

        session.startGame();
        session.addScore(900);
        session.markBossSpawned();
        session.markVictory();

        session.restartGame();

        assertEquals(GameState.RUNNING, session.getState());
        assertEquals(0, session.getScore());
        assertFalse(session.hasBossSpawned());
    }

    @Test
    void testPlayerLivesAreConsumedBeforeGameOver() {
        GameSession session = new GameSession(2);

        session.startGame();
        assertEquals(2, session.getLivesLeft());

        assertTrue(session.handlePlayerDestroyed());
        assertEquals(1, session.getLivesLeft());
        assertEquals(GameState.RUNNING, session.getState());

        assertFalse(session.handlePlayerDestroyed());
        assertEquals(0, session.getLivesLeft());
        assertEquals(GameState.GAME_OVER, session.getState());
    }

    @Test
    void testGainLifeRestoresOneLifeUpToMaximum() {
        GameSession session = new GameSession(3);

        session.startGame();
        assertTrue(session.handlePlayerDestroyed());
        assertEquals(2, session.getLivesLeft());

        assertTrue(session.gainLife());
        assertEquals(3, session.getLivesLeft());
        assertFalse(session.gainLife());
        assertEquals(3, session.getLivesLeft());
    }
}
