package it.unime.orion.events;

public final class DamageEvent {

    private final String targetId;
    private final int amount;

    public DamageEvent(String targetId, int amount) {
        this.targetId = targetId;
        this.amount = amount;
    }

    public String getTargetId() {
        return targetId;
    }

    public int getAmount() {
        return amount;
    }
}
