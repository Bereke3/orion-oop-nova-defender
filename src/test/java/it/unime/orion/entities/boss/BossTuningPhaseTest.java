package it.unime.orion.entities.boss;

import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class BossTuningPhaseTest {

    @Test
    void testBossUsesCustomPhaseDefinitionsForDisplayAndThresholds() {
        BossTuning tuning = new BossTuning(
                100,
                10,
                1000,
                30,
                120,
                1.0,
                1.0,
                0,
                50,
                400,
                "/images/test.png",
                List.of(
                        new BossPhaseDefinition(BossPhase.PHASE_ONE, "Shielded", 1.0, 1.0, 1.0, 1.2, 4.0, 1.5, 0.6, 7, 240),
                        new BossPhaseDefinition(BossPhase.PHASE_TWO, "Exposed", 0.65, 1.2, 1.2, 0.9, 3.8, 1.5, 0.6, 8, 260),
                        new BossPhaseDefinition(BossPhase.PHASE_THREE, "Overload", 0.30, 1.4, 1.4, 0.7, 3.4, 1.5, 0.6, 9, 280)
                )
        );

        BossA boss = new BossA(new Rectangle(100, 60), 100, 0, tuning);

        assertEquals("Shielded", boss.getCurrentPhaseName());

        boss.takeDamage(40);
        assertEquals(BossPhase.PHASE_TWO, boss.getCurrentPhase());
        assertEquals("Exposed", boss.getCurrentPhaseName());

        boss.takeDamage(35);
        assertEquals(BossPhase.PHASE_THREE, boss.getCurrentPhase());
        assertEquals("Overload", boss.getCurrentPhaseName());
    }
}
