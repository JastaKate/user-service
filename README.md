# User-service

Сервис пользователей проекта ConnectHome на Java. Отвечает за регистрацию, аутентификацию и работу с JWT. Используется сервисом home-service для получения идентификатора пользователя

## Возможности

- Регистрация пользователей с хешированием паролей
- Аутентификация с выдачей access- и refresh-токенов
- Обновление access-токена
- Хранение и отзыв access-токенов
- Удаление пользователя с отправкой сообщения в Kafka
- Хранение данных в PostgreSQL и миграции Liquibase

## Технологии

Java 17, Spring Boot, Spring Security, JWT, Spring Data JPA, PostgreSQL, Liquibase, Apache Kafka, Gradle

## REST API

Базовый адрес: `http://localhost:8081`

- `POST /api/register` — регистрация
- `POST /api/authenticate` — аутентификация
- `POST /api/refresh-token` — обновление access-токена. Refresh-токен передаётся в заголовке `Authorization: Bearer <refresh_token>`
- `POST /api/logout` — выход и отзыв access-токена
- `GET /api/token` — получение ID пользователя из JWT, переданного в заголовке `token`
- `DELETE /api/delete/{id}` — удаление пользователя

Пример регистрации:

```json
{
  "name": "Kate",
  "username": "kate",
  "password": "example-password"
}
```

Для аутентификации передаются поля `username` и `password`

## Запуск

1. Установить JDK 17.
2. Запустить PostgreSQL и создать базу `user`
3. Запустить Kafka. Конфигурация Docker Compose находится в каталоге `docker`:

   ```bash
   docker compose -f docker/docker-compose.yml up -d
   ```

4. Настроить `src/main/resources/application.yml`:
   - подключение к PostgreSQL — по умолчанию `localhost:5433/user`;
   - учётные данные БД;
   - ключ подписи JWT в `application.security.jwt.secretKey`;
   - адрес Kafka — по умолчанию `localhost:29092`;

5. Запустить приложение

Миграции Liquibase выполняются при запуске

Для локального запуска пароли и ключи подписи нужно передавать через переменные окружения `SPRING_DATASOURCE_PASSWORD` и `APPLICATION_SECURITY_JWT_SECRETKEY`
