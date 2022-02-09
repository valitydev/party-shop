package dev.vality.partyshop.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@TestConfiguration
public class TestKafkaConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServer;

    @Value("${kafka.topics.party-shop.id}")
    private String partyShopTopic;

    @Bean
    public KafkaAdmin adminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic createPartyShopTopic() {
        return TopicBuilder.name(partyShopTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

}
