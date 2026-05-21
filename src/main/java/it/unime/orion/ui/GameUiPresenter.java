package it.unime.orion.ui;

import it.unime.orion.entities.boss.BossA;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.game.GameSession;

import java.util.Objects;

public final class GameUiPresenter {

    private final HudView hud;
    private final GameOverlayView overlay;

    public GameUiPresenter(HudView hud, GameOverlayView overlay) {
        this.hud = Objects.requireNonNull(hud, "hud");
        this.overlay = Objects.requireNonNull(overlay, "overlay");
    }

    public void present(GameSession session, PlayerShip player, BossA boss) {
        Objects.requireNonNull(session, "session");
        Objects.requireNonNull(player, "player");

        hud.updateHp(player.getHp(), player.getMaxHp());
        hud.setLives(session.getLivesLeft(), session.getInitialLives());
        hud.setScore(session.getScore());

        if (session.hasBossSpawned() && boss != null && boss.isAlive()) {
            hud.showBossHp(boss.getBossHp(), boss.getBossMaxHp(), boss.getCurrentPhaseName());
        } else {
            hud.hideBossHp();
        }

        overlay.update(session.getState(), session.getScore());
    }
}
