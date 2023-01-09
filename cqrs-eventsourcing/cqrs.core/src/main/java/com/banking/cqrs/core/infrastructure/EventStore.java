package com.banking.cqrs.core.infrastructure;

import com.banking.cqrs.core.events.BaseEvent;

import java.util.List;

public interface EventStore {

    void saveEvent(String aggregateId, Iterable<BaseEvent> events, int expectedVersion);

    List<BaseEvent> getEvents(String aggregateId);
}
