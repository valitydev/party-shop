package dev.vality.partyshop.service;

import dev.vality.damsel.payment_processing.PartyEventData;
import dev.vality.machinegun.eventsink.MachineEvent;
import dev.vality.partyshop.filter.CategoryChangeFilter;
import dev.vality.partyshop.handler.PartyChangeHandler;
import dev.vality.sink.common.parser.impl.MachineEventParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyManagementService {

    private final MachineEventParser<PartyEventData> parser;
    private final PartyChangeHandler handlePartyChange;
    private final CategoryChangeFilter categoryChangeFilter;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        for (MachineEvent machineEvent : machineEvents) {
            PartyEventData partyEventData = parser.parse(machineEvent);
            if (partyEventData.isSetChanges()) {
                partyEventData.getChanges().stream()
                        .filter(categoryChangeFilter)
                        .forEach(partyChange -> handlePartyChange.handle(machineEvent, partyChange));
            }
        }
    }

}
