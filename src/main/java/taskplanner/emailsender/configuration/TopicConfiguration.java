package taskplanner.emailsender.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfiguration {
    private static final String EMAIL_SENDING_TOPIC = "EMAIL_SENDING_TASKS";

    @Bean
    public NewTopic emailSendingTopic() {
        return TopicBuilder
                .name(EMAIL_SENDING_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
