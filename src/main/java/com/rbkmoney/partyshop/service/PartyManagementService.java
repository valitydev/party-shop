package com.rbkmoney.partyshop.service;

import com.rbkmoney.damsel.domain.Category;
import com.rbkmoney.damsel.domain.Shop;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.partyshop.domain.ClaimStatusWrapper;
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
    private final DomainRepositoryAdapterImpl domainRepositoryAdapter;

    @Transactional(propagation = Propagation.REQUIRED)
    public void handleEvents(List<MachineEvent> machineEvents) {
        for (MachineEvent machineEvent : machineEvents) {
            PartyEventData partyEventData = parser.parse(machineEvent);
            if (partyEventData.isSetChanges()) {
                partyEventData.getChanges().stream()
                        .filter(this::isShopCreatedOrCategoryChange)
                        .forEach(partyChange -> handlePartyChange(machineEvent, partyChange))
                ;
            }
        }
    }

    private void handlePartyChange(MachineEvent machineEvent, PartyChange partyChange) {
        ClaimStatusWrapper claimStatusWrapper = getClaimStatus(partyChange);
        claimStatusWrapper.getClaimStatus().getAccepted().getEffects()
                .stream()
                .filter(ClaimEffect::isSetShopEffect)
                .forEach(claimEffect -> checkAndSaveShopReferences(machineEvent, claimEffect, claimStatusWrapper.getRevision()));
    }

    private boolean isShopCreatedOrCategoryChange(PartyChange partyChange) {
        return (partyChange.isSetClaimCreated() && partyChange.getClaimCreated().getStatus().isSetAccepted())
                || (partyChange.isSetClaimStatusChanged() && partyChange.getClaimStatusChanged().getStatus().isSetAccepted());
    }

    private void checkAndSaveShopReferences(MachineEvent machineEvent, ClaimEffect claimEffect, long revision) {
        PartyShopReference partyShopReference;
        ShopEffect shopEffect = claimEffect.getShopEffect().getEffect();
        if (shopEffect.isSetCreated()) {
            Shop created = shopEffect.getCreated();
            Category category = domainRepositoryAdapter.getCategory(created.getCategory(), revision);
            partyShopReference = partyShopReferenceRepository.save(PartyShopReference.builder()
                    .shopId(claimEffect.getShopEffect().getShopId())
                    .partyId(machineEvent.getSourceId())
                    .categoryType(category.getType().name())
                    .build());
            log.debug("save created partyShopReference: {}", partyShopReference);
        } else if (shopEffect.isSetCategoryChanged()) {
            partyShopReference = partyShopReferenceRepository.getOne(claimEffect.getShopEffect().getShopId());
            Category category = domainRepositoryAdapter.getCategory(shopEffect.getCategoryChanged(), revision);
            partyShopReference.setCategoryType(category.getType().name());
            partyShopReferenceRepository.save(partyShopReference);
            log.debug("save created partyShopReference: {}", partyShopReference);
        }
    }

    protected ClaimStatusWrapper getClaimStatus(PartyChange change) {
        ClaimStatusWrapper claimStatusWrapper = new ClaimStatusWrapper();
        if (change.isSetClaimCreated()) {
            Claim claimCreated = change.getClaimCreated();
            claimStatusWrapper.setClaimStatus(claimCreated.getStatus());
            claimStatusWrapper.setRevision(claimCreated.getRevision());
        } else if (change.isSetClaimStatusChanged()) {
            ClaimStatusChanged claimStatusChanged = change.getClaimStatusChanged();
            claimStatusWrapper.setClaimStatus(claimStatusChanged.getStatus());
            claimStatusWrapper.setRevision(claimStatusChanged.getRevision());
        }
        return claimStatusWrapper;
    }
}
