package ru.yandex.practicum.configuration;


import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class ConsumerProperties {
    private final Environment environment;

    private Properties getCommonProperties(String consumerType) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, environment.getProperty(consumerType));
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("analyzer.kafka.common-properties.bootstrap.servers"));
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                environment.getProperty("analyzer.kafka.common-properties.key.deserializer"));
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "10000");

        return properties;
    }

    @Bean
    public Consumer<String, HubEventAvro> getHubEventProperties() {
        Properties properties = getCommonProperties("analyzer.kafka.consumers[1].type");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,
                environment.getProperty("analyzer.kafka.consumers[1].properties.group.id"));
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                environment.getProperty("analyzer.kafka.consumers[1].properties.value.deserializer"));

        return new KafkaConsumer<>(properties);
    }

    @Bean
    public Consumer<String, SensorsSnapshotAvro> getSnapshotProperties() {
        Properties properties = getCommonProperties("analyzer.kafka.consumers[0].type");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,
                environment.getProperty("analyzer.kafka.consumers[0].properties.group.id"));
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                environment.getProperty("analyzer.kafka.consumers[0].properties.value.deserializer"));

        return new KafkaConsumer<>(properties);
    }
}