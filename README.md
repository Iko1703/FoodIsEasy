# FoodIsEasy

Сервис персонализированного подбора блюд, планирования питания и совместных решений для семей и групп.

## Быстрый старт (Docker)

Требования: Docker Desktop.

```bash
docker compose up --build
```

| Сервис   | URL                    |
|----------|------------------------|
| **Приложение** | http://localhost:8080 (актуальный UI + API) |
| Frontend (nginx) | http://localhost:3000 (альтернатива) |
| PostgreSQL | localhost:5432       |

> Открывайте **http://localhost:8080** — там свежая сборка React. Старый интерфейс на :8080 был из устаревших файлов в `static/`; теперь фронт собирается в Docker вместе с backend.
>
> Дополнительные пункты меню (Рекомендации, Питание, …) появляются **после входа**.

### Демо-аккаунты

Пароль для всех: `password`

| Email                 |
|-----------------------|
| alice@foodiseasy.ru   |
| bob@foodiseasy.ru     |

## Локальная разработка без Docker

### 1. PostgreSQL

```sql
CREATE USER food WITH PASSWORD 'food';
CREATE DATABASE foodiseasy OWNER food;
```

### 2. Backend

```bash
cd FoodIsEasy
mvn spring-boot:run
```

Flyway автоматически применит миграции из `src/main/resources/db/migration/` (V1–V5: схема, базовый seed, мульти-блюда в плане, роли блюд, расширенный demo-набор).

Пересобрать демо-данные: `python scripts/generate_rich_seed.py` → `V5__rich_demo_data.sql`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Откройте http://localhost:5173 — API проксируется на backend.

## API каталога, избранного и отзывов (фаза D)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/delishies?q=&cuisineId=&categoryId=&mealRole=&maxKcal=&favoritesOnly=` | Каталог с фильтрами |
| GET | `/cuisines` | Список кухонь |
| GET | `/dish-categories` | Категории блюд |
| GET | `/me/favorites` | Избранные блюда |
| GET | `/me/favorites/ids` | ID избранного (для UI) |
| POST | `/me/favorites` | Добавить `{ "delishiesId": 1 }` |
| DELETE | `/me/favorites/{delishiesId}` | Убрать из избранного |
| POST | `/me/delishies/{id}/feedbacks` | Отзыв `{ "message", "rating" }` |

## API рекомендаций и аналитики (фазы E)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/me/recommendations` | Персональная лента |
| GET | `/groups/{id}/recommendations` | Рекомендации для группы |
| GET | `/me/analytics?days=30` | Сводка + `dailyStats` (ккал по дням) + `mealTypeStats` |

## API корзины и голосований (фаза F)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/me/shopping-lists/by-meal-plan/{planId}` | Загрузить сохранённую корзину плана |
| POST | `/me/shopping-lists/from-meal-plan/{planId}` | Собрать/обновить корзину из меню |
| PATCH | `/me/shopping-lists/{id}/items/{itemId}?checked=` | Отметить продукт |
| POST | `/me/shopping-lists/{id}/order` | Кнопка «Заказать» (демо) |
| POST | `/me/groups` | Создать группу (создатель — ADMIN) |
| POST | `/me/groups/join` | Вступить по `groupId` |
| GET | `/me/groups/browse` | Список всех групп |
| GET | `/me/groups/{id}` | Участники и детали |
| GET/POST | `/groups/{id}/votes` | Голосования (открытые и закрытые) |
| POST | `/groups/{id}/votes/{voteId}/close` | Закрыть и определить победителя |

## API питания (фаза C)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/me/meal-history` | История (`?from=&to=`) |
| POST | `/me/meal-history` | Записать приём пищи |
| DELETE | `/me/meal-history/{id}` | Удалить запись |
| GET | `/me/meal-plans` | Личные планы |
| POST | `/me/meal-plans` | Создать план (опционально `groupId`) |
| GET | `/me/meal-plans/{id}` | План с слотами |
| PUT | `/me/meal-plans/{id}/entries` | Добавить блюдо в приём пищи (несколько на слот) |
| DELETE | `/me/meal-plans/{id}/entries/{entryId}` | Убрать позицию из плана |
| POST | `/me/meal-plans/{id}/generate` | Автогенерация по рекомендациям (личные / групповые) |
| DELETE | `/me/meal-plans/{id}` | Удалить план |

## API профиля (фаза B)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/me/profile` | Профиль + группы |
| PUT | `/me/profile` | Обновить имя, возраст, пол |
| GET | `/me/preferences` | Кухни и продукты |
| PUT | `/me/preferences` | Сохранить предпочтения |
| GET | `/cuisines` | Список кухонь (публично) |

Типы продуктовых предпочтений: `FAVORITE`, `DISLIKED`, `ALLERGY`, `SOFT_DISLIKE`.

## Тесты

```bash
cd FoodIsEasy
mvn test
```

Используются Testcontainers (нужен запущенный Docker).

## Архитектура

```
frontend (React)  →  nginx / vite proxy  →  Spring Boot REST API  →  PostgreSQL
```

Схема БД версионируется через **Flyway** (`V1__init_schema.sql`, `V2__seed_data.sql`).

### Основные таблицы

- Пользователи, предпочтения (кухни, продукты, аллергии)
- Блюда, ингредиенты, категории, кухни
- История питания, планы меню
- Группы, голосования
- Общие списки покупок (привязка к групповому меню)
