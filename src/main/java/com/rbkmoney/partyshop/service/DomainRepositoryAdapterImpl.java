package com.rbkmoney.partyshop.service;

import com.rbkmoney.damsel.domain.Category;
import com.rbkmoney.damsel.domain.CategoryRef;
import com.rbkmoney.damsel.domain_config.Head;
import com.rbkmoney.damsel.domain_config.Reference;
import com.rbkmoney.damsel.domain_config.RepositoryClientSrv;
import com.rbkmoney.damsel.domain_config.VersionedObject;
import com.rbkmoney.partyshop.exception.UnknownCategoryRevisionException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomainRepositoryAdapterImpl {

    private final RepositoryClientSrv.Iface repositoryClient;

    @SneakyThrows
    @Cacheable("categories")
    public Category getCategory(CategoryRef categoryRef) {
        VersionedObject versionedObject = repositoryClient.checkoutObject(Reference.head(new Head()), com.rbkmoney.damsel.domain.Reference.category(categoryRef));
        if (!versionedObject.isSetObject() || !versionedObject.getObject().isSetCategory() || !versionedObject.getObject().getCategory().isSetData()) {
            throw new UnknownCategoryRevisionException(String.format("Unknown category: %s", categoryRef.id));
        }
        return versionedObject.getObject().getCategory().getData();
    }

}
