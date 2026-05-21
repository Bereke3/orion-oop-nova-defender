package it.unime.orion.powerups;

import java.util.Random;

public enum PowerUpType {
    HEAL("Heal"),
    SHIELD("Shield"),
    WEAPON_UPGRADE("Weapon upgrade"),
    EXTRA_LIFE("Extra life");

    private final String displayName;

    PowerUpType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PowerUpType randomType(Random random) {
        PowerUpType[] values = {HEAL, SHIELD, WEAPON_UPGRADE};
        return values[random.nextInt(values.length)];
    }
}
