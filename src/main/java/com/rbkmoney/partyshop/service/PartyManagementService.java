package com.rbkmoney.partyshop.service;

import com.rbkmoney.damsel.domain.Shop;
import com.rbkmoney.damsel.payment_processing.ClaimEffect;
import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyEventData;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.partyshop.entity.PartyShopReference;
import com.rbkmoney.partyshop.repository.PartyShopReferenceRepository;
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
    private final PartyShopReferenceRepository partyShopReferenceRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        for (MachineEvent machineEvent : machineEvents) {
            PartyEventData partyEventData = parser.parse(machineEvent);
            if (partyEventData.isSetChanges()) {
                partyEventData.getChanges().stream()
                        .filter(this::isShopCreatedOrCategoryChange)
                        .flatMap(partyChange -> getClaimStatus(partyChange).getAccepted().getEffects().stream())
                        .filter(ClaimEffect::isSetShopEffect)
                        .forEach(claimEffect -> checkAndSaveShopReferencies(machineEvent, claimEffect));
            }
        }
    }

    private boolean isShopCreatedOrCategoryChange(PartyChange partyChange) {
        return (partyChange.isSetClaimCreated() && partyChange.getClaimCreated().getStatus().isSetAccepted())
                || (partyChange.isSetClaimStatusChanged() && partyChange.getClaimStatusChanged().getStatus().isSetAccepted());
    }

    private void checkAndSaveShopReferencies(MachineEvent machineEvent, ClaimEffect claimEffect) {
        if (claimEffect.getShopEffect().getEffect().isSetCreated()) {
            Shop created = claimEffect.getShopEffect().getEffect().getCreated();
            partyShopReferenceRepository.save(PartyShopReference.builder()
                    .shopId(claimEffect.getShopEffect().getShopId())
                    .partyId(machineEvent.getSourceId())
                    .categoryId(created.getCategory().getId())
                    .build());
        } else if (claimEffect.getShopEffect().getEffect().isSetCategoryChanged()) {
            PartyShopReference one = partyShopReferenceRepository.getOne(claimEffect.getShopEffect().getShopId());
            one.setCategoryId(claimEffect.getShopEffect().getEffect().getCategoryChanged().getId());
            partyShopReferenceRepository.save(one);
        }
    }

    protected ClaimStatus getClaimStatus(PartyChange change) {
        ClaimStatus claimStatus = null;
        if (change.isSetClaimCreated()) {
            claimStatus = change.getClaimCreated().getStatus();
        } else if (change.isSetClaimStatusChanged()) {
            claimStatus = change.getClaimStatusChanged().getStatus();
        }
        return claimStatus;
    }
}
