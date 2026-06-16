package it.unime.orion.oop;

import it.unime.orion.combat.BasicWeapon;
import it.unime.orion.combat.DoubleShotWeapon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ClientProgramExamplesTest {

    @Test
    void testMultitypingUsesOneObjectWithoutDowncasting() {
        ClientProgramExamples.MultitypingDemo demo = ClientProgramExamples.demonstrateWeaponMultityping();

        assertEquals("DoubleShotWeapon", demo.concreteReferenceType());
        assertEquals("Weapon", demo.interfaceReferenceType());
        assertEquals("Object", demo.generalReferenceType());
        assertTrue(demo.sameObject());
        assertEquals(2, demo.producedProjectileCount());
    }

    @Test
    void testSubtypePolymorphismChangesBehaviorThroughSameInterface() {
        assertEquals(1, ClientProgramExamples.fireWithWeapon(new BasicWeapon()));
        assertEquals(2, ClientProgramExamples.fireWithWeapon(new DoubleShotWeapon()));
    }

    @Test
    void testExtensibilityAddsNewPolicyThroughExistingInterface() {
        ClientProgramExamples.ExtensibilityDemo demo =
                ClientProgramExamples.demonstrateRewardPolicyExtensibility();

        assertEquals("ScoreThresholdBossRewardPolicy", demo.concreteReferenceType());
        assertEquals("BossRewardPolicy", demo.interfaceReferenceType());
        assertTrue(demo.sameObject());
        assertEquals("BossRewardSystem", demo.consumerType());
        assertEquals(1, demo.droppedPowerUps());
        assertEquals(1, demo.droppedShieldRewards());
    }
}
