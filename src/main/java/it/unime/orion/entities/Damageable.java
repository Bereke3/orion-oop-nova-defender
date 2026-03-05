package it.unime.orion.entities;

public interface Damageable {
    void takeDamage(int amount);
    boolean isAlive();
}