package com.rbkmoney.partyshop.handler;

import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

public interface EventHandler {

    boolean filter(PartyChange partyChange);

    boolean handle(PartyChange partyChange, MachineEvent machineEvent, Integer changeId);

}
