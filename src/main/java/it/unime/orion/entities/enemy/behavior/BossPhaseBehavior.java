package it.unime.orion.entities.enemy.behavior;

import it.unime.orion.assets.GameAssets;
import it.unime.orion.combat.EnemyBullet;
import it.unime.orion.entities.boss.BossPhase;
import it.unime.orion.entities.boss.BossPhaseDefinition;
import it.unime.orion.entities.boss.BossTuning;
import it.unime.orion.entities.enemy.Enemy;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

public final class BossPhaseBehavior implements EnemyBehavior {

    private static final double BULLET_WIDTH = 13;
    private static final double BULLET_HEIGHT = 36;

    private enum AttackMode {
        ASSAULT,
        CHARGE,
        RAIN
    }

    private final BossTuning tuning;

    private BossPhaseDefinition currentPhase;
    private AttackMode attackMode = AttackMode.ASSAULT;

    private double moveTimer;
    private double modeTimer;
    private double shootCooldownLeft;
    private boolean enteredArena;
    private boolean rainSweepLeftToRight = true;
    private boolean rainVolleyFired;

    public BossPhaseBehavior(BossTuning tuning) {
        this.tuning = tuning;
        this.currentPhase = tuning.getPhaseDefinition(BossPhase.PHASE_ONE);
        this.shootCooldownLeft = adjustCooldown(currentPhase);
    }

    @Override
    public void update(Enemy enemy, PlayerShip player, GameWorld world, double deltaSeconds) {
        moveTimer += deltaSeconds;
        BossPhaseDefinition resolvedPhase = determinePhase(enemy);
        if (resolvedPhase != currentPhase) {
            currentPhase = resolvedPhase;
            shootCooldownLeft = Math.min(shootCooldownLeft, adjustCooldown(currentPhase));
        }

        if (!enteredArena) {
            updateEntry(enemy);
            return;
        }

        switch (attackMode) {
            case ASSAULT -> updateAssault(enemy, world, deltaSeconds);
            case CHARGE -> updateCharge(enemy, deltaSeconds);
            case RAIN -> updateRain(enemy, world, deltaSeconds);
        }
    }

    @Override
    public boolean canShoot() {
        return true;
    }

    private void updateEntry(Enemy enemy) {
        if (enemy.getY() < tuning.getArenaY() - 4) {
            enemy.setVelocity(0, tuning.getEntrySpeed());
            return;
        }

        enteredArena = true;
        attackMode = AttackMode.ASSAULT;
        modeTimer = 0;
        shootCooldownLeft = adjustCooldown(currentPhase);
        moveTowards(enemy, getArenaCenterX(), tuning.getArenaY(), 160, 100);
    }

    private void updateAssault(Enemy enemy, GameWorld world, double deltaSeconds) {
        modeTimer += deltaSeconds;
        shootCooldownLeft -= deltaSeconds;

        moveBossThroughArena(enemy);

        if (enemy.isAlive() && shootCooldownLeft <= 0) {
            fireAssaultVolley(enemy, world);
            shootCooldownLeft = adjustCooldown(currentPhase);
        }

        if (modeTimer >= currentPhase.getAssaultDurationSeconds()) {
            attackMode = AttackMode.CHARGE;
            modeTimer = 0;
        }
    }

    private void updateCharge(Enemy enemy, double deltaSeconds) {
        modeTimer += deltaSeconds;

        double desiredX = getArenaCenterX();
        double desiredY = tuning.getArenaY() - 10;
        moveTowards(enemy, desiredX, desiredY, 150, 90);

        if (modeTimer >= currentPhase.getChargeDurationSeconds()) {
            attackMode = AttackMode.RAIN;
            modeTimer = 0;
            rainSweepLeftToRight = !rainSweepLeftToRight;
            rainVolleyFired = false;
        }
    }

    private void updateRain(Enemy enemy, GameWorld world, double deltaSeconds) {
        modeTimer += deltaSeconds;

        double desiredX = rainSweepLeftToRight ? tuning.getArenaMinX() + 20 : tuning.getArenaMaxX() - 20;
        double desiredY = tuning.getArenaY() - 2;
        moveTowards(enemy, desiredX, desiredY, 120, 55);

        if (enemy.isAlive() && !rainVolleyFired) {
            fireRainWave(world, enemy);
            rainVolleyFired = true;
        }

        if (modeTimer >= currentPhase.getRainDurationSeconds()) {
            attackMode = AttackMode.ASSAULT;
            modeTimer = 0;
            shootCooldownLeft = adjustCooldown(currentPhase) * 0.85;
        }
    }

    private void moveBossThroughArena(Enemy enemy) {
        double arenaCenterX = getArenaCenterX();
        double horizontalRange = (tuning.getArenaMaxX() - tuning.getArenaMinX()) / 2.0;
        int phaseIndex = currentPhase.getPhase().ordinal();
        double wideWave = Math.sin(moveTimer * currentPhase.getMoveFrequency()) * horizontalRange * 0.92;
        double shortWave = Math.sin(moveTimer * 2.1 + phaseIndex) * horizontalRange * 0.18;
        double desiredX = clamp(arenaCenterX + (wideWave + shortWave) * currentPhase.getMoveAmplitudeMultiplier(),
                tuning.getArenaMinX(),
                tuning.getArenaMaxX());
        double desiredY = tuning.getArenaY()
                + Math.sin(moveTimer * 1.25 + phaseIndex) * (10 + phaseIndex * 6)
                + Math.cos(moveTimer * 0.65) * 8;

        moveTowards(enemy, desiredX, desiredY, 240 * tuning.getMoveAmplitudeMultiplier(), 110);
    }

    private void fireAssaultVolley(Enemy enemy, GameWorld world) {
        double[] baseOffsets = switch (currentPhase.getPhase()) {
            case PHASE_ONE -> new double[]{47, 135, 224};
            case PHASE_TWO -> new double[]{30, 98, 166, 234};
            case PHASE_THREE -> new double[]{18, 72, 126, 180, 234};
        };

        for (double xOffset : expandPattern(baseOffsets)) {
            spawnBullet(world, enemy, xOffset, 150, 0, 230 + currentPhase.getPhase().ordinal() * 20);
        }
    }

    private void fireRainWave(GameWorld world, Enemy enemy) {
        double worldWidth = tuning.getArenaMinX() + GameAssets.BOSS_WIDTH + tuning.getArenaMaxX();
        double startX = 18;
        double endX = worldWidth - BULLET_WIDTH - 18;
        double step = currentPhase.getRainBulletCount() == 1 ? 0 : (endX - startX) / (currentPhase.getRainBulletCount() - 1);
        double spawnY = enemy.getY() + 150;
        double verticalSpeed = currentPhase.getRainVerticalSpeed();

        for (int i = 0; i < currentPhase.getRainBulletCount(); i++) {
            double x = startX + step * i;
            EnemyBullet bullet = new EnemyBullet(
                    GameAssets.createSpriteView(tuning.getBulletAssetPath(), BULLET_WIDTH, BULLET_HEIGHT),
                    x,
                    spawnY
            );
            bullet.setVelocity(0, verticalSpeed);
            world.addEntity(bullet);
        }
    }

    private void spawnBullet(GameWorld world,
                             Enemy enemy,
                             double xOffset,
                             double yOffset,
                             double vx,
                             double vy) {
        EnemyBullet bullet = new EnemyBullet(
                GameAssets.createSpriteView(tuning.getBulletAssetPath(), BULLET_WIDTH, BULLET_HEIGHT),
                enemy.getX() + xOffset,
                enemy.getY() + yOffset
        );
        bullet.setVelocity(vx, vy);
        world.addEntity(bullet);
    }

    private double[] expandPattern(double[] baseOffsets) {
        List<Double> offsets = new ArrayList<>();
        for (double baseOffset : baseOffsets) {
            offsets.add(baseOffset);
        }

        if (tuning.getExtraCannons() >= 1) {
            offsets.add(10.0);
            offsets.add(GameAssets.BOSS_WIDTH - BULLET_WIDTH - 10.0);
        }
        if (tuning.getExtraCannons() >= 2) {
            offsets.add(58.0);
            offsets.add(GameAssets.BOSS_WIDTH - BULLET_WIDTH - 58.0);
        }

        double[] expanded = new double[offsets.size()];
        for (int i = 0; i < offsets.size(); i++) {
            expanded[i] = offsets.get(i);
        }
        return expanded;
    }

    private void moveTowards(Enemy enemy, double desiredX, double desiredY, double maxSpeedX, double maxSpeedY) {
        double vx = clamp((desiredX - enemy.getX()) * 3.1, -maxSpeedX, maxSpeedX);
        double vy = clamp((desiredY - enemy.getY()) * 3.0, -maxSpeedY, maxSpeedY);
        enemy.setVelocity(vx, vy);
    }

    private BossPhaseDefinition determinePhase(Enemy enemy) {
        return tuning.resolvePhase(enemy.getHp(), enemy.getMaxHp());
    }

    private double getArenaCenterX() {
        return (tuning.getArenaMinX() + tuning.getArenaMaxX()) / 2.0;
    }

    private double adjustCooldown(BossPhaseDefinition phase) {
        return phase.getCooldownSeconds() / tuning.getCooldownMultiplier();
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
