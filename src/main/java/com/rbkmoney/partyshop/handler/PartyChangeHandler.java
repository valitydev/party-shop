package com.rbkmoney.partyshop.handler;

import com.rbkmoney.damsel.domain.Category;
import com.rbkmoney.damsel.domain.Shop;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.partyshop.domain.ClaimStatusWrapper;
import com.rbkmoney.partyshop.entity.PartyShopPK;
import com.rbkmoney.partyshop.entity.PartyShopReference;
import com.rbkmoney.partyshop.exception.UnknownClaimStatusException;
import com.rbkmoney.partyshop.exception.UnknownReferenceException;
import com.rbkmoney.partyshop.repository.PartyShopReferenceRepository;
import com.rbkmoney.partyshop.service.DomainRepositoryAdapterImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class PartyChangeHandler {

    private final PartyShopReferenceRepository partyShopReferenceRepository;
    private final DomainRepositoryAdapterImpl domainRepositoryAdapter;

    public void handle(MachineEvent machineEvent, PartyChange partyChange) {
        log.info("handle machineEvent: {} partyChange: {}", machineEvent, partyChange);
        ClaimStatusWrapper claimStatusWrapper = getClaimStatus(partyChange);
        claimStatusWrapper.getClaimStatus().getAccepted().getEffects()
                .stream()
                .filter(ClaimEffect::isSetShopEffect)
                .forEach(claimEffect -> checkAndSaveShopReferences(machineEvent, claimEffect, claimStatusWrapper.getRevision()));
    }

    private void checkAndSaveShopReferences(MachineEvent machineEvent, ClaimEffect claimEffect, long revision) {
        PartyShopReference partyShopReference;
        ShopEffect shopEffect = claimEffect.getShopEffect().getEffect();
        if (shopEffect.isSetCreated()) {
            Shop created = shopEffect.getCreated();
            Category category = domainRepositoryAdapter.getCategory(created.getCategory(), revision);
            partyShopReference = partyShopReferenceRepository.save(PartyShopReference.builder()
                    .partyShopPK(PartyShopPK.builder()
                            .shopId(claimEffect.getShopEffect().getShopId())
                            .partyId(machineEvent.getSourceId())
                            .build())
                    .categoryType(category.getType().name())
                    .build());
            log.info("save created partyShopReference: {}", partyShopReference);
        } else if (shopEffect.isSetCategoryChanged()) {
            Optional<PartyShopReference> shopReference = partyShopReferenceRepository
                    .findByPartyShopPK(PartyShopPK.builder()
                            .shopId(claimEffect.getShopEffect().getShopId())
                            .partyId(machineEvent.getSourceId())
                            .build());
            if (shopReference.isEmpty()) {
                log.warn("can't find reference with shopId: {}", claimEffect.getShopEffect().getShopId());
                throw new UnknownReferenceException(String.format("Can't find reference for shopId: %s!",
                        claimEffect.getShopEffect().getShopId()));
            }
            Category category = domainRepositoryAdapter.getCategory(shopEffect.getCategoryChanged(), revision);
            partyShopReference = shopReference.get();
            partyShopReference.setCategoryType(category.getType().name());
            partyShopReferenceRepository.save(partyShopReference);
            log.info("save created partyShopReference: {}", partyShopReference);
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
        } else {
            throw new UnknownClaimStatusException(String.format("Unknown claim status for %s!", change.toString()));
        }
        return claimStatusWrapper;
    }
}
