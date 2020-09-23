package com.rbkmoney.partyshop.service;

import com.rbkmoney.damsel.payment_processing.PartyEventData;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.partyshop.filter.CategoryChangeFilter;
import com.rbkmoney.partyshop.handler.PartyChangeHandler;
import com.rbkmoney.sink.common.parser.impl.MachineEventParser;
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
