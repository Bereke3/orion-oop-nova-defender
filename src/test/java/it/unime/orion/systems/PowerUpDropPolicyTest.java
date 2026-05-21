package it.unime.orion.systems;

import it.unime.orion.errors.InvalidGameConfigurationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class PowerUpDropPolicyTest {

    @Test
    void testRegularEnemyDropDependsOnChanceAndCapacity() {
        PowerUpDropPolicy policy = new PowerUpDropPolicy(0.35, 4);

        assertTrue(policy.shouldDrop(0, false, 0.20));
        assertFalse(policy.shouldDrop(0, false, 0.60));
        assertFalse(policy.shouldDrop(4, false, 0.10));
    }

    @Test
    void testBossKillForcesDropWhenCapacityAllowsIt() {
        PowerUpDropPolicy policy = new PowerUpDropPolicy(0.10, 4);

        assertTrue(policy.shouldDrop(1, true, 0.99));
        assertFalse(policy.shouldDrop(4, true, 0.01));
    }

    @Test
    void testInvalidDropChanceIsRejected() {
        try {
            new PowerUpDropPolicy(1.2, 3);
            fail("Drop chance above 1 must be rejected");
        } catch (InvalidGameConfigurationException expected) {
            assertTrue(expected.getMessage().contains("dropChance"));
        }
    }
}
