package dev.vality.partyshop.service;

import dev.vality.damsel.domain.Category;
import dev.vality.damsel.domain.CategoryRef;
import dev.vality.damsel.domain_config.Head;
import dev.vality.damsel.domain_config.Reference;
import dev.vality.damsel.domain_config.RepositoryClientSrv;
import dev.vality.damsel.domain_config.VersionedObject;
import dev.vality.partyshop.exception.UnknownCategoryRevisionException;
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
        VersionedObject versionedObject = repositoryClient.checkoutObject(
                Reference.head(new Head()),
                dev.vality.damsel.domain.Reference.category(categoryRef)
        );
        if (!versionedObject.isSetObject()
                || !versionedObject.getObject().isSetCategory()
                || !versionedObject.getObject().getCategory().isSetData()) {
            throw new UnknownCategoryRevisionException(String.format("Unknown category: %s", categoryRef.id));
        }
        return versionedObject.getObject().getCategory().getData();
    }

}
