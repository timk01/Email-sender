# Email Sender

Независимый сервис для отправки email-уведомлений.

Сервис получает готовые сообщения из Kafka и отправляет письма указанному получателю через SMTP с помощью Spring Mail.

Email Sender используется в двух сценариях:

- отправка обычных email-задач (`EmailSendingTask`), в том числе приветственного письма после регистрации пользователя;
- отправка ежедневного summary-отчёта (`UserReport`), который формируется Scheduler после получения суммаризации задач пользователя.

## Технологии

- Java 21
- Spring Boot
- Spring Kafka
- Spring Mail
- Apache Kafka
- SMTP
- Gradle
- Docker
- Docker Compose

## Роль в системе

Email Sender — конечный сервис отправки писем.

Сервис участвует в двух основных сценариях:

- после регистрации пользователя Task Planner публикует `EmailSendingTask` в `EMAIL_SENDING_TASKS`, после чего Email Sender отправляет приветственное письмо;
- после формирования ежедневного summary Scheduler публикует `UserReport` в `SUMMARY_SENDING_TASKS`, после чего Email Sender отправляет итоговый отчёт пользователю.

Email Sender не участвует в формировании содержимого summary и не запрашивает данные у других сервисов — он получает готовое сообщение и отвечает за его отправку.

## Форматы сообщений

### Обычное email-уведомление

Сервис получает `EmailSendingTask` из Kafka-топика:

```text
EMAIL_SENDING_TASKS
```

Структура сообщения:

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

Структура сообщения:

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

## SMTP-провайдер

В текущей конфигурации используется **Unisender Go**.

`email-sender` не привязан к конкретному SMTP-провайдеру. При необходимости можно использовать другой сервис, изменив параметры подключения и адрес отправителя.

Для Unisender Go необходимо указать:

```env
MAIL_FROM=...
MAIL_SMTP_USERNAME=...
MAIL_SMTP_PASSWORD=...
```

Домен отправителя должен быть подтверждён у провайдера.

На бесплатном тарифе Unisender Go отправка может быть ограничена подтверждёнными email-адресами или адресами подтверждённых доменов. Это ограничение выбранного SMTP-провайдера, а не самого Email Sender.

## Переменные окружения

Основные переменные окружения:

```env
MAIL_FROM=...
MAIL_SMTP_USERNAME=...
MAIL_SMTP_PASSWORD=...
KAFKA_BOOTSTRAP_SERVERS=...
```

Реальные credentials не должны попадать в Git.

При локальном запуске SMTP-переменные можно передать через конфигурацию запуска IDE.

## Запуск всего проекта

Email Sender входит в общий Docker Compose-стек проекта.

Общий `compose.yml` находится в репозитории Task Planner и позволяет запустить Email Sender вместе с остальными сервисами, PostgreSQL и Kafka из готовых Docker-образов.

Docker-образ Email Sender:

```text
timk1111111/email-sender:latest
```

Инструкция по запуску всего проекта находится в README Task Planner.

## Локальная разработка

Email Sender можно запускать локально отдельно от общего Docker Compose-стека.

Для запуска необходимо передать SMTP credentials через environment variables. Kafka при локальном запуске доступна по адресу:

```text
localhost:9094
```

## CI/CD

При push в `main` GitHub Actions запускает тесты, собирает Docker-образ и публикует его в Docker Hub.