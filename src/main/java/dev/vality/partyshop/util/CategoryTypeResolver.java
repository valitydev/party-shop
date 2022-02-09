package dev.vality.partyshop.util;

import dev.vality.damsel.domain.CategoryType;
import com.rbkmoney.damsel.party_shop.PaymentInstitutionRealm;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryTypeResolver {

    public static String resolve(PaymentInstitutionRealm paymentInstitutionRealm) {
        switch (paymentInstitutionRealm) {
            case live:
                return CategoryType.live.name();
            case test:
                return CategoryType.test.name();
            default:
                throw new IllegalArgumentException(
                        String.format("resolveCategoryType environment: %s is unknown!", paymentInstitutionRealm));
        }
    }

}
