package dev.vality.partyshop.listener;

import dev.vality.kafka.common.util.LogUtil;
import dev.vality.machinegun.eventsink.SinkEvent;
import dev.vality.partyshop.service.PartyManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartyManagementListener {

    private final PartyManagementService partyManagementService;

    @KafkaListener(topics = "${kafka.topics.party-shop.id}", containerFactory = "kafkaListenerContainerFactory")
    public void handle(List<ConsumerRecord<String, SinkEvent>> messages, Acknowledgment ack) {
        log.info("Got partyManagement machineEvent batch with size: {}", messages.size());
        partyManagementService.handleEvents(
                messages.stream()
                        .map(m -> m.value().getEvent())
                        .collect(Collectors.toList())
        );
        ack.acknowledge();
        log.info(
                "Batch partyManagement has been committed, size={}, {}",
                messages.size(),
                LogUtil.toSummaryStringWithSinkEventValues(messages)
        );
    }
}
