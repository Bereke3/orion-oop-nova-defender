package it.unime.orion.events;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class EventBusTest {

    @Test
    void testClosedSubscriptionStopsReceivingEvents() {
        EventBus<DamageEvent> bus = new EventBus<>();
        AtomicInteger receivedCount = new AtomicInteger();

        EventSubscription subscription = bus.subscribe(event -> receivedCount.incrementAndGet());
        bus.publish(new DamageEvent("player-1", 5));
        subscription.unsubscribe();
        bus.publish(new DamageEvent("player-1", 5));

        assertEquals(1, receivedCount.get());
    }

    @Test
    void testHandlerCanUnsubscribeDuringPublish() {
        EventBus<DamageEvent> bus = new EventBus<>();
        AtomicInteger receivedCount = new AtomicInteger();
        AtomicReference<EventSubscription> subscriptionRef = new AtomicReference<>();

        subscriptionRef.set(bus.subscribe(event -> {
            receivedCount.incrementAndGet();
            subscriptionRef.get().unsubscribe();
        }));

        bus.publish(new DamageEvent("player-1", 5));
        bus.publish(new DamageEvent("player-1", 5));

        assertEquals(1, receivedCount.get());
    }
}
