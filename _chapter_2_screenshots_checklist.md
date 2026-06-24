# Чеклист иллюстраций для главы 2 (диплом)

## Перед началом

```powershell
cd C:\Users\Iko\Documents\FoodProject
docker compose up --build
```

Открыть: http://localhost:8080  
Логин: `alice@foodiseasy.ru` / `password`

Postman: base URL `http://localhost:8080`  
DBeaver: localhost:5432, DB `foodiseasy`, user `food`, password `food`

---

## Скриншоты (29 шт. + 2 рисунка + листинги)

| № | Что снять | Где |
|---|-----------|-----|
| 2.1 | Дерево пакетов backend в IDEA | §2.1.1 |
| 2.2 | App.tsx — маршруты и меню | §2.1.2 |
| 2.3 | Папка db/migration V1–V6 | §2.1.3 |
| 2.4 | `docker compose ps` | §2.1.5 |
| 2.5 | Postman: login + 401/200 profile | §2.2.2 |
| 2.6 | DBeaver: список таблиц | §2.3.1 |
| 2.7 | SQL: состав борща | §2.3.2 |
| 2.8 | flyway_schema_history | §2.3.3 |
| 2.9 | Postman POST /auth/login | §2.4.1 |
| 2.10 | Postman GET /me/profile | §2.4.2 |
| 2.11 | Postman GET /delishies?mealRole=SOUP | §2.4.3 |
| 2.12 | Postman POST /me/favorites | §2.4.4 |
| 2.13 | Postman GET meal-plans (multi lunch) | §2.4.5 |
| 2.14 | Postman POST generate | §2.4.5 |
| 2.15 | Postman POST shopping-list | §2.4.6 |
| 2.16 | Postman GET votes + winner | §2.4.7 |
| 2.17 | Postman GET /me/recommendations | §2.4.8 |
| 2.18 | Postman GET /me/analytics | §2.4.8 |
| 2.19 | UI: каталог с фильтрами | §2.5 |
| 2.20 | UI: карточка блюда | §2.5 |
| 2.21 | UI: рекомендации | §2.5 |
| 2.22 | UI: планировщик (multi-dish) | §2.5 |
| 2.23 | UI: корзина | §2.5 |
| 2.24 | UI: группы и голосование | §2.5 |
| 2.25 | UI: аналитика с графиком | §2.5 |
| 2.26 | UI: профиль | §2.5 |
| 2.27 | UI: страница /api | §2.5 |
| 2.28 | Терминал: mvn test SUCCESS | §2.6 |
| 2.29 | IDEA: тесты Passed (опц.) | §2.6 |

**Рисунки:** 2.1 — архитектура; 2.2 — ER-диаграмма.

**Листинги:** JwtAuthenticationFilter, CurrentUserService, SecurityConfig, RecommendationServiceImpl (фрагменты).

---

## Тесты

```powershell
cd C:\Users\Iko\Documents\FoodProject\FoodIsEasy
mvn test
```

Нужен запущенный Docker Desktop (Testcontainers).

Примеры Postman: `scripts/postman_examples.md`
