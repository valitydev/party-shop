package dev.vality.partyshop;

import dev.vality.damsel.payment_processing.PartyChange;
import dev.vality.damsel.payment_processing.PartyEventData;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.kafka.common.serialization.ThriftSerializer;
import dev.vality.machinegun.eventsink.MachineEvent;
import dev.vality.machinegun.eventsink.SinkEvent;
import dev.vality.machinegun.msgpack.Value;
import dev.vality.partyshop.extension.KafkaContainerExtension;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.thrift.TException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Properties;

@Slf4j
@ExtendWith(KafkaContainerExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractKafkaIntegrationTest extends PostgresAbstractTest {

    @DynamicPropertySource
    static void kafkaProps(DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap-servers", KafkaContainerExtension.KAFKA::getBootstrapServers);
    }

    public static <T> Producer<String, T> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaContainerExtension.KAFKA.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "CLIENT");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class);
        return new KafkaProducer<>(props);
    }

    protected static MachineEvent createMachineEvent(PartyChange partyChange, String sourceId, Long sequenceId)
            throws TException {
        MachineEvent message = new MachineEvent();
        PartyEventData payload = new PartyEventData();
        ArrayList<PartyChange> partyChanges = new ArrayList<>();
        partyChanges.add(partyChange);
        payload.setChanges(partyChanges);
        message.setCreatedAt(TypeUtil.temporalToString(Instant.now()));
        message.setEventId(sequenceId);
        message.setSourceNs("sda");
        message.setSourceId(sourceId);

        ThriftSerializer<PartyEventData> eventPayloadThriftSerializer = new ThriftSerializer<>();
        Value data = new Value();
        payload.validate();
        data.setBin(eventPayloadThriftSerializer.serialize("", payload));
        message.setData(data);
        return message;
    }

    protected static SinkEvent createSinkEvent(MachineEvent machineEvent) {
        SinkEvent sinkEvent = new SinkEvent();
        sinkEvent.setEvent(machineEvent);
        return sinkEvent;
    }
}
