package it.unime.orion.events;

public final class EnemyDestroyedEvent implements GameEvent {

    private final String enemyId;
    private final double x;
    private final double y;
    private final int scoreValue;
    private final boolean bossKill;

    public EnemyDestroyedEvent(String enemyId,
                               double x,
                               double y,
                               int scoreValue,
                               boolean bossKill) {
        this.enemyId = enemyId;
        this.x = x;
        this.y = y;
        this.scoreValue = scoreValue;
        this.bossKill = bossKill;
    }

    public String getEnemyId() {
        return enemyId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public boolean isBossKill() {
        return bossKill;
    }
}
