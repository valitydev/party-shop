package com.rbkmoney.partyshop.handler;

import lombok.RequiredArgsConstructor;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;

@RequiredArgsConstructor
public class ShopCreatedHandler {

    private final Filter filter = new PathConditionFilter(new PathConditionRule(
            "party_created",
            new IsNullCondition().not()));

}
