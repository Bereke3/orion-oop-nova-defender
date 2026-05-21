package it.unime.orion.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Minimal in-process event bus used by the gameplay runtime.
 * <p>
 * This implementation is intentionally not thread-safe and is expected to be used
 * only from the JavaFX application thread together with the rest of the game loop.
 */
public final class EventBus<T extends GameEvent> {

    // Not thread-safe: intended to be used only from the JavaFX application thread.
    private final List<Consumer<T>> subscribers = new ArrayList<>();

    public EventSubscription subscribe(Consumer<T> handler) {
        Consumer<T> safeHandler = Objects.requireNonNull(handler, "handler");
        subscribers.add(safeHandler);
        return new EventSubscription() {
            private boolean active = true;

            @Override
            public void unsubscribe() {
                if (!active) {
                    return;
                }
                subscribers.remove(safeHandler);
                active = false;
            }
        };
    }

    public void publish(T event) {
        Objects.requireNonNull(event, "event");
        for (Consumer<T> s : List.copyOf(subscribers)) {
            s.accept(event);
        }
    }
}
