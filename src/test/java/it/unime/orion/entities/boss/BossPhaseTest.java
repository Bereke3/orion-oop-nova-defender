package it.unime.orion.entities.boss;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class BossPhaseTest {

    @Test
    void testBossPhaseThresholds() {
        assertEquals(BossPhase.PHASE_ONE, BossPhase.forHpRatio(1.00));
        assertEquals(BossPhase.PHASE_ONE, BossPhase.forHpRatio(0.80));
        assertEquals(BossPhase.PHASE_TWO, BossPhase.forHpRatio(0.75));
        assertEquals(BossPhase.PHASE_TWO, BossPhase.forHpRatio(0.50));
        assertEquals(BossPhase.PHASE_THREE, BossPhase.forHpRatio(0.40));
        assertEquals(BossPhase.PHASE_THREE, BossPhase.forHpRatio(0.10));

        assertEquals(BossPhase.PHASE_ONE, BossPhase.forHp(18, 22));
        assertEquals(BossPhase.PHASE_TWO, BossPhase.forHp(16, 22));
        assertEquals(BossPhase.PHASE_THREE, BossPhase.forHp(8, 22));
    }
}
