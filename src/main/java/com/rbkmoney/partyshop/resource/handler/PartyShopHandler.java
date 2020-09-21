package com.rbkmoney.partyshop.resource.handler;

import com.rbkmoney.damsel.domain.CategoryType;
import com.rbkmoney.damsel.party_shop.Environment;
import com.rbkmoney.damsel.party_shop.PartyShopServiceSrv;
import com.rbkmoney.partyshop.entity.PartyShopReference;
import com.rbkmoney.partyshop.repository.PartyShopReferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartyShopHandler implements PartyShopServiceSrv.Iface {

    private final PartyShopReferenceRepository partyShopReferenceRepository;

    @Override
    public List<String> getShopsIds(String partyId, Environment environment) throws TException {
        log.debug("-> get shops ids by partyId: {} env: {}", partyId, environment);
        List<PartyShopReference> references = partyShopReferenceRepository.findByPartyIdAndCategoryType(partyId,
                resolveCategoryType(environment));
        log.debug("-> get shops ids by partyId: {} env: {} result: {}", partyId, environment, references);
        if (!CollectionUtils.isEmpty(references)) {
            return references.stream()
                    .map(PartyShopReference::getShopId)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private String resolveCategoryType(Environment environment) {
        switch (environment) {
            case prod:
                return CategoryType.live.name();
            case test:
                return CategoryType.test.name();
            default:
                throw new RuntimeException("resolveCategoryType environment is unknown!");
        }
    }

}
