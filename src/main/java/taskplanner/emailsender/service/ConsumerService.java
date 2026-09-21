package taskplanner.emailsender.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import taskplanner.emailsender.configuration.KafkaTopics;
import taskplanner.emailsender.dto.EmailSendingTask;
import taskplanner.emailsender.dto.UserReport;

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

    @KafkaListener(
            topics = KafkaTopics.SUMMARY_SENDING,
            groupId = "summary-email-service-group",
            properties = {
                    "spring.json.value.default.type=taskplanner.emailsender.dto.UserReport"
            }
    )
    public void consume(UserReport dto) {
        log.info("Summary sending task received");

        emailService.sendMessage(
                new EmailSendingTask(
                        dto.email(),
                        "summary report",
                        dto.summarization()
                )
        );
    }
}
