package com.shoppoc.shared.event;

import java.time.Instant;

public interface DomainEvent {

    String eventId();

    Instant occurredAt();
}
