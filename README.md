# Explore-with-me

## Introduction

Explore-with-me - это платформа, позволяющая пользователям создавать мероприятия, управлять ими и участвовать в них. Приложение перешло на **микросервисную архитектуру** для улучшения масштабируемости, отказоустойчивости и гибкости разработки.

Ключевые компоненты архитектуры:
*   **Config Server**: Централизованное управление конфигурациями для всех микросервисов.
*   **Discovery Server (Eureka)**: Обеспечивает регистрацию и обнаружение сервисов в сети.
*   **Gateway Server**: Единая точка входа для всех клиентских запросов, выполняет маршрутизацию к соответствующим микросервисам.
*   **Event Service**: Отвечает за логику, связанную с мероприятиями (создание, поиск, управление).
*   **User Service**: Управляет данными пользователей и их аутентификацией/авторизацией.
*   **Request Service**: Обрабатывает запросы на участие в мероприятиях.
*   **Stats Service**: Система сбора и анализа статистики, разделенная на:
    *   **Collector**: Принимает и сохраняет информацию о просмотрах и обращениях к эндпоинтам.
    *   **Aggregator**: Агрегирует собранные данные для последующего анализа.
    *   **Analyzer**: Анализирует агрегированные данные для формирования рекомендаций пользователям.

Взаимодействие между сервисами осуществляется с помощью **Feign клиентов**.

## Technologies Used

- **Spring Boot**: Основа для создания микросервисов.
- **Spring Cloud**:
    - **Spring Cloud Config**: Для `config-server`.
    - **Spring Cloud Netflix Eureka** (или **Consul**): Для `discovery-server`.
    - **Spring Cloud Gateway**: Для `gateway-server`.
    - **OpenFeign**: Для декларативного REST-взаимодействия между сервисами.
- **REST**: Для API коммуникации.
- **Docker & Docker Compose**: Для контейнеризации и оркестрации окружения.
- **PostgreSQL**: В качестве основной базы данных для сервисов (где это необходимо).
- **Lombok**: Для уменьшения шаблонного кода.
- **SLF4J**: Для логирования.

## Service Overview

Проект состоит из следующих основных микросервисов:

-   **config-server**: Сервер конфигураций.
-   **discovery-server**: Сервер регистрации и обнаружения сервисов.
-   **gateway-server**: API шлюз, маршрутизирующий запросы.
-   **event-service**: Сервис управления событиями.
-   **user-service**: Сервис управления пользователями.
-   **request-service**: Сервис управления запросами на участие.
-   **collector**: Сервис сбора статистики.
-   **aggregator**: Сервис агрегации статистики.
-   **analyzer**: Сервис анализа статистики и формирования рекомендаций.

## Setup and Installation

### Prerequisites

-   Java 21+
-   Maven
-   Docker
-   Docker Compose

### Installation Steps

1.  **Clone the Repository**

    ```bash
    git clone https://github.com/yiqes/java-plus-graduation.git
    cd java-plus-graduation
    ```

2.  **Build the Project Modules**

    ```bash
    mvn clean install
    ```

3.  **Running with Docker Compose (Recommended)**

    -   Убедитесь, что Docker и Docker Compose установлены.
    -   Перейдите в корневую директорию проекта, где находится `docker-compose.yml`.
    -   Запустите:

        ```bash
        docker-compose up --build
        ```
    Это поднимет все сконфигурированные сервисы, включая `config-server`, `discovery-server` и базы данных.

4.  **Running Without Docker (More Complex)**

    -   Запустите `config-server`.
    -   Запустите `discovery-server`. Убедитесь, что он регистрируется в `config-server` для получения своей конфигурации.
    -   Запустите остальные микросервисы (`event-service`, `user-service`, `request-service`, `stats-collector`, `stats-aggregator`, `stats-analyzer`). Они должны регистрироваться в `discovery-server` и получать конфигурацию из `config-server`.
    -   Запустите `gateway-server`. Он будет использовать `discovery-server` для маршрутизации запросов.
    -   Для каждого сервиса необходимо настроить подключение к базе данных (если используется) в его `bootstrap.properties` (для подключения к config-server) и `application.properties` (или получать из config-server).
    -   Запуск каждого сервиса:
        ```bash
        # Пример для event-service
        # java -jar event-service/target/event-service.jar
        ```

## API Documentation

Все API запросы должны направляться через **Gateway Server**, который обычно доступен по адресу `http://localhost:8080` (или порт, указанный в вашей конфигурации `gateway-server`). Gateway автоматически маршрутизирует запросы к соответствующим микросервисам.

**Эндпоинты остались теми же, что и в монолитной версии.**

### Admin API Endpoints (через Gateway)

-   **POST /admin/categories**
    -   Создает новую категорию.
    -   Request Body: `NewCategoryDto`
    -   *Маршрутизируется на `event-service`*

-   **GET /admin/compilations**
    -   Получает подборки событий.
    -   Query Params: `pinned`, `from`, `size`
    -   *Маршрутизируется на `event-service`*

-   **POST /admin/events** (и другие PATCH, GET эндпоинты для событий)
    -   Создает/обновляет/получает события (администратор).
    -   Request Body: `NewEventDto` / `UpdateEventAdminRequest`
    -   *Маршрутизируется на `event-service`*

-   **GET /admin/users** (и другие эндпоинты для управления пользователями)
    -   Получает пользователей.
    -   Query Params: `ids`, `from`, `size`
    -   *Маршрутизируется на `user-service`*

### Public API Endpoints (через Gateway)

-   **GET /categories**
    -   Получает категории.
    -   Query Params: `from`, `size`
    -   *Маршрутизируется на `event-service`*

-   **GET /compilations**
    -   Получает подборки событий.
    -   Query Params: `pinned`, `from`, `size`
    -   *Маршрутизируется на `event-service`*

-   **GET /events**
    -   Получает события (публичный доступ).
    -   Query Params: `text`, `categories`, `paid`, `rangeStart`, `rangeEnd`, `onlyAvailable`, `sort`, `from`, `size`
    -   *Маршрутизируется на `event-service`*

### Private API Endpoints (через Gateway)

-   **POST /users/{userId}/events** (и другие эндпоинты для событий пользователя)
    -   Создает событие для пользователя.
    -   Request Body: `NewEventDto`
    -   *Маршрутизируется на `event-service` (с проверкой пользователя через `user-service` или передачей `userId`)*

-   **GET /users/{userId}/requests** (и другие эндпоинты для запросов пользователя)
    -   Получает запросы на участие для событий пользователя.
    -   *Маршрутизируется на `request-service` (с проверкой пользователя)*

## Usage Examples

### Getting an event with provided id & user-id from header (через Gateway)

```bash
curl -X GET "http://localhost:8080/123" \
     -H "X-Ewm-User-Id: 456"