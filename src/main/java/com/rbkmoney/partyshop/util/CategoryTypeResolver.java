package com.rbkmoney.partyshop.util;

import com.rbkmoney.damsel.domain.CategoryType;
import com.rbkmoney.damsel.party_shop.Environment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryTypeResolver {

    public static String resolve(Environment environment) {
        switch (environment) {
            case prod:
                return CategoryType.live.name();
            case test:
                return CategoryType.test.name();
            default:
                throw new IllegalArgumentException(
                        String.format("resolveCategoryType environment: %s is unknown!", environment));
        }
    }

}
