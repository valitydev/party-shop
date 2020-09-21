package com.rbkmoney.partyshop.filter;

import com.rbkmoney.damsel.payment_processing.PartyChange;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class CategoryChangeFilter implements Predicate<PartyChange> {

    @Override
    public boolean test(PartyChange partyChange) {
        return (partyChange.isSetClaimCreated() && partyChange.getClaimCreated().getStatus().isSetAccepted())
                || (partyChange.isSetClaimStatusChanged() && partyChange.getClaimStatusChanged().getStatus().isSetAccepted());
    }
}
