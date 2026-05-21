package it.unime.orion.ui;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.combat.BasicWeapon;
import it.unime.orion.entities.player.PlayerMovement;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.entities.player.PlayerStats;
import it.unime.orion.game.GameSession;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public final class GameUiPresenterTest {

    @Test
    void testPresenterSynchronizesHudAndOverlay() {
        HudView hud = new HudView();
        GameOverlayView overlay = new GameOverlayView(900, 600);
        GameUiPresenter presenter = new GameUiPresenter(hud, overlay);
        GameSession session = new GameSession();
        PlayerShip player = new PlayerShip(
                new Rectangle(GameAssets.PLAYER_WIDTH, GameAssets.PLAYER_HEIGHT),
                100,
                200,
                new PlayerStats(10),
                new PlayerMovement(250, 0, 500, 0, 400),
                new BasicWeapon(),
                0
        );

        session.startGame();
        session.addScore(700);
        player.takeDamage(3);

        presenter.present(session, player, null);

        assertEquals("HP: 7 / 10", hud.getDisplayedHp());
        assertEquals("Score: 700", hud.getDisplayedScore());
        assertFalse(overlay.getRoot().isVisible());
    }
}
