package com.example.exercise.search.config;

import com.example.exercise.order.application.dto.MarkOrderPaidCommand;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true")
public class SearchKafkaConfig {
    @Value("${kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("search-service")
    private String searchTopic;

    @Value("product-search")
    private String consumerGroupId;
    @Bean
    public ConsumerFactory<String, MarkOrderPaidCommand> searchConsumerFactory() {
        // Kafka 컨슈머가 사용할 역직렬화 및 그룹 설정을 정의한다.
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // 컨슈머가 붙을 브로커 주소
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId); // 메시지를 함께 소비할 그룹 ID
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // 키를 문자열로 역직렬화
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class); // 값을 JSON으로 역직렬화
        JacksonJsonDeserializer<MarkOrderPaidCommand> deserializer =
                new JacksonJsonDeserializer<>(MarkOrderPaidCommand.class, false);
        deserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MarkOrderPaidCommand> searchKafkaContainerFactory() {
        // @KafkaListener가 사용할 컨테이너 팩토리를 구성한다.
        ConcurrentKafkaListenerContainerFactory<String, MarkOrderPaidCommand> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(searchConsumerFactory()); // 컨슈머 구성 연결
//        factory.setConcurrency(3); // 파티션 병렬 소비 수
        factory.getContainerProperties().setObservationEnabled(true); // Micrometer 관찰 활성화
        return factory;
    }

    @Bean
    public NewTopic searchTopic() {
        // 개발 환경에서 자동으로 토픽을 생성해 손쉽게 테스트한다.
        return TopicBuilder.name(searchTopic)
//                .partitions(3)
                .replicas(1)
                .build();
    }

}
