package dev.vality.partyshop;

import dev.vality.damsel.domain.Category;
import dev.vality.damsel.domain.CategoryType;
import com.rbkmoney.damsel.party_shop.PaymentInstitutionRealm;
import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.machinegun.eventsink.MachineEvent;
import dev.vality.machinegun.eventsink.SinkEvent;
import dev.vality.partyshop.repository.PartyShopReferenceRepository;
import dev.vality.partyshop.resource.handler.PartyShopHandler;
import dev.vality.partyshop.service.DomainRepositoryAdapterImpl;
import dev.vality.partyshop.utils.BeanUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.concurrent.ExecutionException;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = PartyShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        ProducerRecord<String, SinkEvent> producerRecord =
                new ProducerRecord<>(topic, message.getSourceId(), createSinkEvent(message));
        producer.send(producerRecord).get();
        producer.send(producerRecord).get();
        producer.send(producerRecord).get();

        Thread.sleep(2000L);

        List<String> shopsIds = partyShopHandler.getShopsIds(SOURCE_ID, PaymentInstitutionRealm.live);

        assertEquals(1, shopsIds.size());
        assertEquals(BeanUtils.SHOP_ID, shopsIds.get(0));
    }

}
