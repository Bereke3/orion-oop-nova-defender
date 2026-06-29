package it.unime.orion.oop;

import it.unime.orion.combat.BasicWeapon;
import it.unime.orion.combat.DoubleShotWeapon;
import it.unime.orion.combat.Weapon;
import it.unime.orion.events.EnemyDestroyedEvent;
import it.unime.orion.events.EventBus;
import it.unime.orion.powerups.PowerUp;
import it.unime.orion.powerups.PowerUpFactory;
import it.unime.orion.powerups.PowerUpType;
import it.unime.orion.powerups.ShieldPowerUp;
import it.unime.orion.systems.BossRewardPolicy;
import it.unime.orion.systems.BossRewardSystem;
import it.unime.orion.systems.ScoreThresholdBossRewardPolicy;
import it.unime.orion.world.GameWorld;
import javafx.scene.shape.Rectangle;

public final class ClientProgramExamples {

    private ClientProgramExamples() {
    }

    public static void main(String[] args) {
        System.out.println("Multityping -> " + demonstrateWeaponMultityping());
        System.out.println("Subtype polymorphism -> basic bullets="
                + fireWithWeapon(new BasicWeapon())
                + ", double bullets="
                + fireWithWeapon(new DoubleShotWeapon()));
        System.out.println("Extensibility -> " + demonstrateRewardPolicyExtensibility());
    }

    public static MultitypingDemo demonstrateWeaponMultityping() {
        DoubleShotWeapon concreteReference = new DoubleShotWeapon();
        Weapon interfaceReference = concreteReference;
        Object generalReference = concreteReference;

        return new MultitypingDemo(
                "DoubleShotWeapon",
                "Weapon",
                "Object",
                concreteReference == interfaceReference && interfaceReference == generalReference,
                fireWithWeapon(interfaceReference)
        );
    }

    public static int fireWithWeapon(Weapon weapon) {
        return weapon.fire(150, 200).size();
    }

    public static ExtensibilityDemo demonstrateRewardPolicyExtensibility() {
        GameWorld world = new GameWorld();
        EventBus<EnemyDestroyedEvent> enemyDestroyedBus = new EventBus<>();
        ScoreThresholdBossRewardPolicy concreteReference =
                new ScoreThresholdBossRewardPolicy(2000, PowerUpType.SHIELD);
        BossRewardPolicy interfaceReference = concreteReference;
        PowerUpFactory factory = (type, x, y) -> new ShieldPowerUp(new Rectangle(24, 24), x, y);

        try (BossRewardSystem rewardSystem =
                     new BossRewardSystem(world, enemyDestroyedBus, factory, interfaceReference)) {
            enemyDestroyedBus.publish(new EnemyDestroyedEvent("boss-1", 100, 50, 2500, true));
            enemyDestroyedBus.publish(new EnemyDestroyedEvent("boss-2", 100, 50, 1500, true));
        }

        return new ExtensibilityDemo(
                "ScoreThresholdBossRewardPolicy",
                "BossRewardPolicy",
                concreteReference == interfaceReference,
                "BossRewardSystem",
                countPowerUps(world),
                countShieldRewards(world)
        );
    }

    private static int countPowerUps(GameWorld world) {
        int count = 0;

        for (var entity : world.getEntitiesView()) {
            if (entity instanceof PowerUp) {
                count++;
            }
        }

        return count;
    }

    private static int countShieldRewards(GameWorld world) {
        return (int) world.getEntitiesView().stream().filter(ShieldPowerUp.class::isInstance).count();
    }

    public record MultitypingDemo(String concreteReferenceType,
                                  String interfaceReferenceType,
                                  String generalReferenceType,
                                  boolean sameObject,
                                  int producedProjectileCount) {
    }

    public record ExtensibilityDemo(String concreteReferenceType,
                                    String interfaceReferenceType,
                                    boolean sameObject,
                                    String consumerType,
                                    int droppedPowerUps,
                                    int droppedShieldRewards) {
    }
}
