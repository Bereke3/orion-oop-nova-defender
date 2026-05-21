package it.unime.orion.events;

@FunctionalInterface
public interface EventSubscription extends AutoCloseable {

    void unsubscribe();

    @Override
    default void close() {
        unsubscribe();
    }
}
