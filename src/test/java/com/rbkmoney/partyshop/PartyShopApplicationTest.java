package com.rbkmoney.partyshop;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.partyshop.entity.PartyShopReference;
import com.rbkmoney.partyshop.repository.PartyShopReferenceRepository;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PartyShopApplication.class)
public class PartyShopApplicationTest extends AbstractKafkaIntegrationTest {

    public static final String SHOP_ID = "shop_id";
    @Value("${kafka.topics.party-shop.id}")
    public String topic;

    @Autowired
    PartyShopReferenceRepository partyShopReferenceRepository;

    @Test
    public void contextLoads() throws ExecutionException, InterruptedException {
        PartyChange partyChange = PartyChange.claim_created(new Claim()
                .setCreatedAt(Instant.now().toString())
                .setChangeset(List.of(new PartyModification()))
                .setStatus(ClaimStatus.accepted(new ClaimAccepted().setEffects(
                        List.of(ClaimEffect.shop_effect(new ShopEffectUnit()
                                .setShopId("123")
                                .setEffect(ShopEffect.created(
                                        new Shop()
                                                .setBlocking(Blocking.unblocked(new Unblocked()
                                                        .setReason("123")
                                                        .setSince("123"))
                                                )
                                                .setSuspension(Suspension.active(new Active().setSince("1")))
                                                .setDetails(new ShopDetails()
                                                        .setName("name"))
                                                .setLocation(new ShopLocation())
                                                .setCategory(new CategoryRef().setId(1))
                                                .setContractId("123")
                                                .setCreatedAt(Instant.now().toString())
                                                .setId(SHOP_ID))))))
                        )
                )
        );
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

        PartyShopReference one = partyShopReferenceRepository.getOne(SHOP_ID);
    }

}
