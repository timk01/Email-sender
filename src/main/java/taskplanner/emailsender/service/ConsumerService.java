package taskplanner.emailsender.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import taskplanner.emailsender.configuration.KafkaTopics;
import taskplanner.emailsender.dto.EmailSendingTask;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConsumerService {

    private final EmailService emailService;

    @KafkaListener(topics = KafkaTopics.EMAIL_SENDING)
    public void consume(EmailSendingTask dto) {
        log.info("Email sending task received");

        emailService.sendMessage(dto);
    }
}
