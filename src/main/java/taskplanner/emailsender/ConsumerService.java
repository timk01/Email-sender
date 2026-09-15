package taskplanner.emailsender;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import taskplanner.emailsender.dto.EmailSendingTask;

@Service
public class ConsumerService {

    @KafkaListener(topics = "EMAIL_SENDING_TASKS")
    public void consume(EmailSendingTask message) {
        System.out.println("received message " + message);
    }
}
