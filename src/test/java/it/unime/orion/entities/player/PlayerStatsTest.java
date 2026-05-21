package it.unime.orion.entities.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class PlayerStatsTest {

    @Test
    void testInvulnerabilityPreventsDamageUntilTimerExpires() {
        PlayerStats stats = new PlayerStats(100);

        stats.activateInvulnerability(1.5);
        stats.damage(25);
        assertEquals(100, stats.getHp());
        assertTrue(stats.isInvulnerable());

        stats.update(1.5);
        assertFalse(stats.isInvulnerable());

        stats.damage(25);
        assertEquals(75, stats.getHp());
    }

    @Test
    void testRestoreFullHealthRefillsHp() {
        PlayerStats stats = new PlayerStats(100);

        stats.damage(80);
        assertEquals(20, stats.getHp());

        stats.restoreFullHealth();
        assertEquals(100, stats.getHp());
    }
}
