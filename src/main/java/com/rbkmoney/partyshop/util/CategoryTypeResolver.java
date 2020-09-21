package com.rbkmoney.partyshop.util;

import com.rbkmoney.damsel.domain.CategoryType;
import com.rbkmoney.damsel.party_shop.Environment;

public class CategoryTypeResolver {

    public static String resolve(Environment environment) {
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
