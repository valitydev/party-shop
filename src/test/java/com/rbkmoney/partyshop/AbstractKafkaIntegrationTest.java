package com.rbkmoney.partyshop;

import com.rbkmoney.damsel.payment_processing.EventPayload;
import com.rbkmoney.damsel.payment_processing.PartyChange;
import com.rbkmoney.damsel.payment_processing.PartyEventData;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.partyshop.serializer.SinkEventDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.thrift.TException;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext
@ContextConfiguration(initializers = AbstractKafkaIntegrationTest.Initializer.class)
public abstract class AbstractKafkaIntegrationTest extends PostgresAbstractTest {

    private static final String KAFKA_DOCKER_VERSION = "5.0.1";

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(KAFKA_DOCKER_VERSION).withEmbeddedZookeeper();

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public static final String MG_EVENTS_PARTY = "mg-events-party";

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of("kafka.bootstrap-servers=" + kafka.getBootstrapServers(),
                            "kafka.topics.party-shop.id=" + MG_EVENTS_PARTY)
                    .applyTo(configurableApplicationContext.getEnvironment());
            initTopic(MG_EVENTS_PARTY);
        }

        private <T> void initTopic(String topicName) {
            Consumer<String, T> consumer = createConsumer(SinkEventDeserializer.class);
            try {
                consumer.subscribe(Collections.singletonList(topicName));
                consumer.poll(Duration.ofMillis(100L));
            } catch (Exception e) {
                log.error("KafkaAbstractTest initialize e: ", e);
            }
            consumer.close();
        }
    }

    public static <T> Consumer<String, T> createConsumer(Class clazz) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, clazz);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(props);
    }

    public static <T> Producer<String, T> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "CLIENT");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class);
        return new KafkaProducer<>(props);
    }

    protected static MachineEvent createMachineEvent(PartyChange partyChange, String sourceId, Long sequenceId) throws TException {
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