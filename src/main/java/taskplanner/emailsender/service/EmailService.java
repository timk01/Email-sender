package taskplanner.emailsender.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import taskplanner.emailsender.dto.EmailSendingTask;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${mail.address}")
    private String senderMailAddressS;

    public void sendMessage(EmailSendingTask dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderMailAddressS);
        message.setTo(dto.recipient());
        message.setSubject(dto.subject());
        message.setText(dto.text());

        javaMailSender.send(message);

        log.info("Email sent to {}", dto.recipient());
    }
}
