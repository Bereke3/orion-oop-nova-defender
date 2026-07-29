package it.unime.orion.combat;

import java.util.List;

public interface Weapon {
    List<Projectile> fire(double originX, double originY);
    double getCooldownSeconds();
}
