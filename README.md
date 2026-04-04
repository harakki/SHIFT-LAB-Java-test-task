# Тестовое задание ШИФТ ЛАБ. Разработка упрощенной CRM-системы

## Функциональность сервиса

- CRUD для продавцов (`/api/v1/sellers`)
- Создание и просмотр транзакций продавцов (`/api/v1/transactions`)
- Эндпоинты с аналитикой (`/api/v1/analytics`):
  - самый продуктивный продавец по периодам (день, месяц, квартал, год)
  - список продавцов с суммой продаж ниже порога за период
  - самое продуктивное время конкретного продавца (за день/неделю/месяц)
- Валидация входных данных и единый формат ошибок (`ProblemDetail`)
- Soft delete для продавцов
- Аудит изменений продавцов (через Hibernate Envers)
- Пагинация для возвращаемых списков продавцов и транзакций

## Технологии и зависимости

Основной стек:

- Java 25
- Spring Boot 4 (`actuator`, `data-jpa`, `validation`, `webmvc`)
- PostgreSQL (runtime база данных)
- Hibernate Processor + Hibernate Envers (аудит изменений продавцов для сохранения историчности данных)
- MapStruct (маппинг DTO и сущностей)
- Lombok (сокращение шаблонного кода)
- springdoc-openapi + therapi-runtime-javadoc (генерация спецификации OpenAPI из кода и Javadoc)

Тесты:

- JUnit
- Mockito (мокирование зависимостей в юнит-тестах)
- Testcontainers (`postgresql`, `junit-jupiter`) (интеграционные тесты с инстансом базы данных)

> См. конфигурации:
>
> - `build.gradle`
> - `gradle/libs.versions.toml`

## Структура API

Базовый префикс: `/api/v1`

### Sellers

- `GET /sellers` - краткий список продавцов (с пагинацией)
- `GET /sellers/{sellerId}` - подробная информация о продавце
- `POST /sellers` - создать продавца
- `PATCH /sellers/{sellerId}` - обновить продавца
- `DELETE /sellers/{sellerId}` - удалить продавца (soft delete)

### Transactions

- `GET /transactions` - краткий список транзакций (с пагинацией)
- `GET /transactions/{transactionId}` - подробная информация о транзакции
- `POST /transactions` - создать транзакцию
- `GET /transactions/sellers/{sellerId}` - список транзакций продавца (с пагинацией)

### Analytics

- `GET /analytics/sellers/most-productive` - самый продуктивный продавец за период (выводятся периоды день/месяц/квартал/год)
- `GET /analytics/sellers/sum-lower-than?sum={value}[&startDate=...&endDate=...]`- продавцы с суммой продаж ниже порога за определенный период
- `GET /analytics/sellers/{sellerId}/most-productive-time` - самое продуктивное время конкретного продавца (за день/неделю/месяц)

## Сборка и запуск

### Требования

- JDK 25
- Docker

1. Сборка

```bash
./gradlew build
```

2. Запуск приложения

```bash
./gradlew bootRun
```

Приложение запускается на `http://localhost:8080`.

> В `application.yaml` подключен Compose-файл из `src/main/docker/compose.yaml`, который поднимает PostgreSQL (`postgres:18.3-alpine`) на порте `5432`.

3. Запуск тестов

```bash
./gradlew test
```

> Также есть Swagger UI, доступный по адресу `http://localhost:8080/swagger-ui.html` и тесты для проверки работы сервиса через HTTP-клиент IntelliJ IDEA в директории `http`.

## Примеры использования API

### Создать продавца

```bash
curl -X POST 'http://localhost:8080/api/v1/sellers' \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Продавец 1",
    "contactInfo": "+7 999 999 9999"
  }'
```

### Получить продавцов

```bash
curl 'http://localhost:8080/api/v1/sellers?page=0&size=10'
```

### Создать транзакцию

```bash
curl -X POST 'http://localhost:8080/api/v1/transactions' \
  -H 'Content-Type: application/json' \
  -d '{
    "sellerId": 1,
    "amount": 100.00,
    "paymentType": "CASH"
  }'
```

### Получить транзакции продавца

```bash
curl 'http://localhost:8080/api/v1/transactions/sellers/1?page=0&size=10'
```

### Аналитика: продавцы с суммой меньше порога

```bash
curl 'http://localhost:8080/api/v1/analytics/sellers/sum-lower-than?sum=1000000'
```

### Аналитика: самый продуктивный продавец

```bash
curl 'http://localhost:8080/api/v1/analytics/sellers/most-productive'
```

> Все запросы модно найти по адресу `http://localhost:8080/swagger-ui.html` в Swagger UI после запуска приложения.
