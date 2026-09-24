# Email Sender

Независимый сервис для отправки email-уведомлений.

Сервис получает сообщения из Kafka и отправляет письма указанному получателю через SMTP с помощью Spring Mail.

Email Sender используется в двух сценариях:

- отправка обычных email-задач (`EmailSendingTask`), в том числе приветственного письма после регистрации пользователя;
- отправка ежедневного summary-отчёта (`UserReport`), который формируется Scheduler после получения суммаризации задач пользователя.

## Схема работы

Обычные email-уведомления:

```text
Kafka
→ EMAIL_SENDING_TASKS
→ @KafkaListener
→ EmailSendingTask
→ Spring Mail
→ SMTP-провайдер
→ получатель
```

Ежедневный отчёт:

```text
Scheduler
→ SUMMARY_SENDING_TASKS
→ @KafkaListener
→ UserReport
→ EmailSendingTask
→ Spring Mail
→ SMTP-провайдер
→ получатель
```

Таким образом, Email Sender напрямую связан со Scheduler через Kafka-топик `SUMMARY_SENDING_TASKS`.

## Форматы сообщений

### Обычное email-уведомление

Сервис получает `EmailSendingTask` из Kafka-топика:

```text
EMAIL_SENDING_TASKS
```

Ожидаемая структура сообщения:

```json
{
  "recipient": "user@example.com",
  "subject": "Subject",
  "text": "Message text"
}
```

Где:

- `recipient` — email получателя;
- `subject` — тема письма;
- `text` — текст письма.

### Ежедневный summary-отчёт

Scheduler публикует `UserReport` в Kafka-топик:

```text
SUMMARY_SENDING_TASKS
```

Ожидаемая структура сообщения:

```json
{
  "email": "user@example.com",
  "summarization": "Daily task summary"
}
```

Email Sender преобразует `UserReport` в `EmailSendingTask`:

- `email` используется как адрес получателя;
- тема письма — `summary report`;
- `summarization` используется как текст письма.

## Используемые технологии

- Java 21
- Spring Boot
- Spring Kafka
- Spring Mail
- Apache Kafka
- SMTP

## SMTP-провайдер

В текущей конфигурации используется **Unisender Go**.

При этом `email-sender` не привязан к конкретному провайдеру. Spring Mail работает через стандартный SMTP, поэтому при необходимости можно использовать другой почтовый сервис или SMTP-провайдер.

При смене провайдера потребуется изменить соответствующие настройки подключения:

- SMTP host;
- port;
- username;
- password;
- SSL/TLS настройки;
- при необходимости — адрес отправителя и настройки домена.

Логика самого сервиса при этом, как правило, меняться не должна.

## Переменные окружения

SMTP credentials и адрес отправителя передаются через переменные окружения:

```env
MAIL_FROM=no-reply@example.com
MAIL_SMTP_USERNAME=YOUR_SMTP_USERNAME
MAIL_SMTP_PASSWORD=YOUR_SMTP_PASSWORD
```

Реальные значения не должны попадать в Git.

При локальном запуске приложения переменные можно передать через настройки запуска IDE.

## Текущая конфигурация Unisender Go

Для Unisender Go:

```text
MAIL_SMTP_USERNAME → user_id
MAIL_SMTP_PASSWORD → API key
MAIL_FROM          → адрес отправителя на подтверждённом домене
```

Например:

```env
MAIL_FROM=no-reply@timk.space
MAIL_SMTP_USERNAME=YOUR_UNISENDER_USER_ID
MAIL_SMTP_PASSWORD=YOUR_UNISENDER_API_KEY
```

SMTP-конфигурация находится в `application.yml`:

```yaml
spring:
  mail:
    host: smtp.go2.unisender.ru
    port: 465
    username: ${MAIL_SMTP_USERNAME}
    password: ${MAIL_SMTP_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          ssl:
            enable: true

mail:
  address: ${MAIL_FROM}
```

`spring.mail.*` — стандартные настройки Spring Boot для подключения к SMTP.

`mail.address` — собственное property приложения, которое используется как адрес отправителя.

SMTP host Unisender Go зависит от датацентра аккаунта (`go1` или `go2`).

Перед использованием Unisender Go необходимо добавить и подтвердить домен отправителя.

## Ограничение бесплатного тарифа Unisender Go

На бесплатном тарифе Unisender Go отправка разрешена только на подтверждённые email-адреса или адреса подтверждённых доменов.

Поэтому при тестировании получателя может потребоваться предварительно добавить в список подтверждённых email-адресов в Unisender Go.

Это ограничение выбранного SMTP-провайдера, а не самого `email-sender`.

Поскольку действия выше удобны далеко не всем/всегда, для локальной разработки или демонстрации проекта можно использовать другой SMTP-сервис, если его условия бесплатного тарифа удобнее и не требуют предварительно подтверждать каждого получателя.

## Запуск

Перед запуском сервиса необходимо:

1. Запустить Kafka.
2. Указать необходимые переменные окружения.
3. Настроить выбранный SMTP-провайдер.
4. Запустить `EmailSenderApplication`.

После запуска сервис слушает два Kafka-топика:

```text
EMAIL_SENDING_TASKS
SUMMARY_SENDING_TASKS
```

Первый используется для обычных email-уведомлений, второй — для ежедневных summary-отчётов, поступающих непосредственно от Scheduler.
