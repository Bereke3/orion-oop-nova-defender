package it.unime.orion.entities.ship;

import it.unime.orion.entities.Damageable;
import it.unime.orion.entities.EntityType;
import it.unime.orion.entities.GameEntity;
import javafx.scene.Node;

public abstract class Ship extends GameEntity implements Damageable {

    private final int contactDamage;

    protected Ship(EntityType type, Node view, double x, double y, int contactDamage) {
        super(type, view, x, y);
        if (contactDamage < 0) {
            throw new IllegalArgumentException("contactDamage must be >= 0");
        }
        this.contactDamage = contactDamage;
    }

    public final int getContactDamage() {
        return contactDamage;
    }

    public abstract int getHp();

    public abstract int getMaxHp();

    protected abstract void applyDamage(int amount);

    @Override
    public final void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("damage amount must be >= 0");
        }
        if (!isAlive()) {
            return;
        }
        applyDamage(amount);
    }

    @Override
    public final boolean isAlive() {
        return getHp() > 0;
    }
}
