package com.banking.account.cmd.infrastructure;

import com.banking.account.cmd.domain.AccountAggregate;
import com.banking.cqrs.core.domain.AggregateRoot;
import com.banking.cqrs.core.events.BaseEvent;
import com.banking.cqrs.core.handlers.EventSourcingHandler;
import com.banking.cqrs.core.infrastructure.EventStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@AllArgsConstructor
@Service
public class AccountEventSourcingHandler implements EventSourcingHandler<AccountAggregate> {

    private final EventStore eventStore;

    @Override
    public void save(AggregateRoot aggregate) {
        eventStore.saveEvent(aggregate.getId(), aggregate.getUncommitedChanges(), aggregate.getVersion());
        aggregate.markChangesAsCommited();
    }

    @Override
    public AccountAggregate getById(String aggregateId) {
        var aggregate = new AccountAggregate();
        var events = eventStore.getEvents(aggregateId);
        if (events != null && !events.isEmpty()) {
            aggregate.replayEvents(events);
            var latestVersion = events.stream()
                    .map(BaseEvent::getVersion)
                    .max(Comparator.naturalOrder());
            aggregate.setVersion(latestVersion.get());
        }
        return aggregate;
    }
}
