# 🛍️ Реактивный Сервис Уведомлений (LeanTech Kafka)

**LeanTech Kafka** — реактивное Spring Boot 3 приложение на Java 21 для асинхронной обработки уведомлений с помощью
Apache Kafka и реактивного WebFlux.

## 🚀 Ключевые особенности

- Прием JSON-сообщений из Kafka
- Сохранение в PostgreSQL (через R2DBC)
- Реактивный REST API
- Статус DLT для ошибочных сообщений
- Контейнеризация Docker + Compose

---

## 🛠️ Технологии

- Java 21
- Spring Boot 3 + WebFlux
- R2DBC + PostgreSQL
- Flyway
- Apache Kafka
- Docker, Docker Compose
- JUnit 5, Testcontainers

---

## 📂 Структура

```
LeanTechKafka/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── config/
│   │   │   ├── core/
│   │   │   └── web/
│   │   └── resources/
│   │       ├── db/migration/
│   │       └── application.yml
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── .env
└── pom.xml
```

---

## 📆 Установка

### 1. Клонируйте репозиторий

```bash
git clone https://github.com/5N5RUS/LeanTechKafka.git
cd LeanTechKafka
```

### 2. Создайте `.env`

```
POSTGRES_DB=leantechkafka_db
POSTGRES_USER=leantechkafka_admin
POSTGRES_PASSWORD=12345
```

### 3. Соберите и запустите

```bash
docker-compose up --build -d
```

- Приложение: `http://localhost:8080`
- Kafdrop: `http://localhost:9000`
- PostgreSQL: `localhost:5433`

---

## 🔧 Тестовое сообщение в Kafka

```bash
docker-compose exec kafka kafka-console-producer --broker-list localhost:9092 --topic notifications-topic
```

**Пример JSON:**

```json
{
  "message": "Ваш заказ №12345 был отправлен!",
  "messageType": "EXTERNAL",
  "userUid": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "expirationDate": "2025-12-31T23:59:59"
}
```

**Пример "битого" JSON (попадет в Dead-Letter Topic):**

```json
{
  "mss": "Ошибка",
  "mssTp": "EXRNL",
  "userUid": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "expirationDate": "2025-12-31T23:59:59"
}
```

---

## 💡 API

### Получить все уведомления (с пагинацией)

```http
GET /notifications?page=0&size=10
```

### Получить уведомление по ID

```http
GET /notifications/{id}
```

### Обновить статус уведомления

```http
PUT /notifications/{id}/complete
```

---

## 🔍 Доступ к PostgreSQL

- Хост: `localhost`
- Порт: `5433`
- База: `leantechkafka_db`
- Логин: `leantechkafka_admin`
- Пароль: `12345`

JDBC URL:

```text
jdbc:postgresql://localhost:5433/leantechkafka_db?currentSchema=notifications
```

---

## 🔖 Kafdrop

Для просмотра топиков и сообщений:

- [http://localhost:9000](http://localhost:9000)
- Основной топик: `notifications-topic`
- DLT-топик: `notifications-topic.DLT`

---

## 📈 Статус и логи

```bash
docker-compose logs -f app
```
