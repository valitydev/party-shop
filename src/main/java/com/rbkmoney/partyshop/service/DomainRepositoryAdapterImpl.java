package com.rbkmoney.partyshop.service;

import com.rbkmoney.damsel.domain.Category;
import com.rbkmoney.damsel.domain.CategoryRef;
import com.rbkmoney.damsel.domain_config.Reference;
import com.rbkmoney.damsel.domain_config.RepositoryClientSrv;
import com.rbkmoney.damsel.domain_config.VersionedObject;
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
    public Category getCategory(CategoryRef categoryRef, long revision) {
        VersionedObject versionedObject = repositoryClient.checkoutObject(Reference.version(revision), com.rbkmoney.damsel.domain.Reference.category(categoryRef));
        return versionedObject.getObject().getCategory().getData();
    }

}