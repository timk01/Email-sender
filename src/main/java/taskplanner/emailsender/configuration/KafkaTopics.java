package taskplanner.emailsender.configuration;

public final class KafkaTopics {

    public static final String EMAIL_SENDING = "EMAIL_SENDING_TASKS";
    public static final String SUMMARY_SENDING = "SUMMARY_SENDING_TASKS";

    private KafkaTopics() {
    }
}