package com.rbkmoney.partyshop;

import com.rbkmoney.damsel.domain.Category;
import com.rbkmoney.damsel.domain.CategoryType;
import com.rbkmoney.damsel.party_shop.PaymentInstitutionRealm;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.partyshop.repository.PartyShopReferenceRepository;
import com.rbkmoney.partyshop.resource.handler.PartyShopHandler;
import com.rbkmoney.partyshop.service.DomainRepositoryAdapterImpl;
import com.rbkmoney.partyshop.utils.BeanUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PartyShopApplication.class)
public class PartyShopApplicationTest extends AbstractKafkaIntegrationTest {

    public static final String SOURCE_ID = "12";

    @Value("${kafka.topics.party-shop.id}")
    public String topic;

    @Autowired
    PartyShopReferenceRepository partyShopReferenceRepository;

    @Autowired
    PartyShopHandler partyShopHandler;

    @MockBean
    DomainRepositoryAdapterImpl domainRepositoryAdapter;

    @Test
    public void contextLoads() throws ExecutionException, InterruptedException, TException {
        Mockito.when(domainRepositoryAdapter.getCategory(any())).thenReturn(new Category()
                .setName("test")
                .setType(CategoryType.live)
        );

        PartyChange partyChange = BeanUtils.createPartyChange();
        MachineEvent message = createMachineEvent(partyChange, SOURCE_ID, 1L);
        Producer<String, SinkEvent> producer = createProducer();
        ProducerRecord<String, SinkEvent> producerRecord = new ProducerRecord<>(topic, message.getSourceId(), createSinkEvent(message));
        producer.send(producerRecord).get();
        producer.send(producerRecord).get();
        producer.send(producerRecord).get();

        Thread.sleep(2000L);

        List<String> shopsIds = partyShopHandler.getShopsIds(SOURCE_ID, PaymentInstitutionRealm.live);

        assertEquals(1, shopsIds.size());
        assertEquals(BeanUtils.SHOP_ID, shopsIds.get(0));
    }

}
