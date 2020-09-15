package com.rbkmoney.partyshop;

import com.rbkmoney.damsel.domain.PartyContactInfo;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyCreated;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.partyshop.service.PartyManagementService;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PartyShopApplication.class)
public class PartyShopApplicationTest extends AbstractKafkaIntegrationTest {

    @Value("${kafka.topics.party-shop.id}")
    public String topic;

    @MockBean
    PartyManagementService partyManagementService;

    @Test
    public void contextLoads() throws ExecutionException, InterruptedException {
        PartyChange partyChange = new PartyChange();
        partyChange.setPartyCreated(new PartyCreated()
                .setId("123")
                .setCreatedAt(Instant.now().toString())
                .setContactInfo(new PartyContactInfo()
                        .setEmail("tetst")));
        MachineEvent message = createMachineEvent(partyChange, "12", 1L);
        SinkEvent sinkEvent = createSinkEvent(message);
        Producer<String, SinkEvent> producer = createProducer();
        ProducerRecord<String, SinkEvent> producerRecord = new ProducerRecord<>(topic, message.getSourceId(), sinkEvent);
        producer.send(producerRecord).get();

        Thread.sleep(5000L);

        Mockito.verify(partyManagementService, Mockito.times(1)).handleEvents(any());
    }

}
