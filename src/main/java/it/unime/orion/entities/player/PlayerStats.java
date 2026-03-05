package it.unime.orion.entities.player;

public final class PlayerStats {

    private int hp;
    private final int maxHp;

    private boolean shield;

    public PlayerStats(int maxHp) {
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public boolean hasShield() {
        return shield;
    }

    public void activateShield() {
        shield = true;
    }

    public void deactivateShield() {
        shield = false;
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public void damage(int amount) {
        if (shield) {
            return;
        }

        hp = Math.max(0, hp - amount);
        if (hp == 0) return;
    }

    public boolean isAlive() {
        return hp > 0;
    }
}