package dev.vality.partyshop.resource.handler;

import com.rbkmoney.damsel.party_shop.PartyShopServiceSrv;
import com.rbkmoney.damsel.party_shop.PaymentInstitutionRealm;
import dev.vality.partyshop.entity.PartyShopReference;
import dev.vality.partyshop.repository.PartyShopReferenceRepository;
import dev.vality.partyshop.util.CategoryTypeResolver;
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
    public List<String> getShopsIds(String partyId, PaymentInstitutionRealm paymentInstitutionRealm) throws TException {
        log.debug("-> get shops ids by partyId: {} env: {}", partyId, paymentInstitutionRealm);
        List<PartyShopReference> references = partyShopReferenceRepository.findByPartyShopPKPartyIdAndCategoryType(
                partyId,
                CategoryTypeResolver.resolve(paymentInstitutionRealm)
        );
        log.debug("-> get shops ids by partyId: {} env: {} result: {}", partyId, paymentInstitutionRealm, references);
        if (!CollectionUtils.isEmpty(references)) {
            return references.stream()
                    .map(partyShopReference -> partyShopReference.getPartyShopPK().getShopId())
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
