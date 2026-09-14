package taskplanner.emailsender;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @KafkaListener(topics = "EMAIL_SENDING_TASKS")
    public void consume(String message) {
        System.out.println("received message " + message);
    }
}
