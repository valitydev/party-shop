package com.rbkmoney.partyshop;

import com.rbkmoney.damsel.domain.Category;
import com.rbkmoney.damsel.domain.CategoryType;
import com.rbkmoney.partyshop.entity.PartyShopReference;
import com.rbkmoney.partyshop.repository.PartyShopReferenceRepository;
import com.rbkmoney.partyshop.service.DomainRepositoryAdapterImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;

//Test for real big data set
//Need get truststore pcsng-kafka by devops
@Slf4j
@Disabled
@SpringBootTest(classes = PartyShopApplication.class,
        properties = {"kafka.bootstrap-servers=" + "dev-kafka-mirror.bst1.rbkmoney.net:9092",
                "kafka.ssl.trustStoreLocation=" + "src/test/resources/broker/pcsng-kafka.p12",
                "kafka.ssl.trustStorePassword=" + "xxx",
                "kafka.ssl.keyStoreLocation=" + "src/test/resources/broker/strug.p12",
                "kafka.ssl.keyStorePassword=" + "xxx",
                "kafka.ssl.keyPassword=" + "xxx",
                "kafka.ssl.enabled=" + "true",
                "kafka.ssl.keyStoreType=" + "PKCS12",
                "kafka.ssl.trustStoreType=" + "PKCS12",
                "kafka.consumer.group-id=" + "fraud-connector"
        })
public class IntegrationTest extends PostgresAbstractTest {

    @Autowired
    PartyShopReferenceRepository partyShopReferenceRepository;

    @MockBean
    DomainRepositoryAdapterImpl domainRepositoryAdapter;

    @Test
    public void test() throws InterruptedException {
        Mockito.when(domainRepositoryAdapter.getCategory(any())).thenReturn(new Category()
                .setName("test")
                .setType(CategoryType.live)
        );

        Thread.sleep(10000L);

        List<PartyShopReference> all = partyShopReferenceRepository.findAll();

        log.info("all: {}", all);
    }

}
