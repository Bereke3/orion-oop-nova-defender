package it.unime.orion.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class HudViewTest {

    @Test
    void testHpScoreAndLivesAreRendered() {
        HudView hud = new HudView();

        hud.updateHp(5, 10);
        hud.setScore(1200);
        hud.setLives(2, 3);

        assertEquals("HP: 5 / 10", hud.getDisplayedHp());
        assertEquals(120.0, hud.getDisplayedHpBarWidth());
        assertEquals("Score: 1200", hud.getDisplayedScore());
        assertEquals(3, hud.getRenderedHeartCount());
        assertEquals(2, hud.getVisibleHeartCount());
    }

    @Test
    void testBossHpCanBeShownAndHidden() {
        HudView hud = new HudView();

        hud.showBossHp(9, 18, "Phase 2");

        assertEquals("Boss HP: 9 / 18 | Phase 2", hud.getDisplayedBossText());
        assertEquals(130.0, hud.getDisplayedBossBarWidth());
        assertTrue(hud.isBossHpVisible());

        hud.hideBossHp();
        assertFalse(hud.isBossHpVisible());
    }
}
