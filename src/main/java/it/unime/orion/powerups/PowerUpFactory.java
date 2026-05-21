package it.unime.orion.powerups;

public interface PowerUpFactory {
    PowerUp create(PowerUpType type, double x, double y);
}
