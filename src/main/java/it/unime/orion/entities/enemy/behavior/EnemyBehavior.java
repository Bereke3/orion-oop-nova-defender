package it.unime.orion.entities.enemy.behavior;

import it.unime.orion.entities.enemy.Enemy;
import it.unime.orion.entities.player.PlayerShip;
import it.unime.orion.world.GameWorld;

public interface EnemyBehavior {
    void update(Enemy enemy, PlayerShip player, GameWorld world, double deltaSeconds);
    boolean canShoot();
}
