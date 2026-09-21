package taskplanner.emailsender.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfiguration {

    @Bean
    public NewTopic emailSendingTopic() {
        return TopicBuilder
                .name(KafkaTopics.EMAIL_SENDING)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic summarySendingTopic() {
        return TopicBuilder
                .name(KafkaTopics.SUMMARY_SENDING)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
