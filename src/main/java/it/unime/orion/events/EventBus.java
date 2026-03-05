package it.unime.orion.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class EventBus<T extends GameEvent> {

    private final List<Consumer<T>> subscribers = new ArrayList<>();

    public void subscribe(Consumer<T> handler) {
        subscribers.add(Objects.requireNonNull(handler, "handler"));
    }

    public void publish(T event) {
        Objects.requireNonNull(event, "event");
        for (Consumer<T> s : subscribers) {
            s.accept(event);
        }
    }
}