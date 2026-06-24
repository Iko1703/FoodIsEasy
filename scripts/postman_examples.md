# Примеры тел запросов Postman (FoodIsEasy)

Базовый URL: `http://localhost:8080`

## 1. Логин

```http
POST /auth/login
Content-Type: application/json

{
  "email": "alice@foodiseasy.ru",
  "password": "password"
}
```

Скопируйте `token` из ответа → в Postman: Authorization → Bearer Token.

## 2. Профиль

```http
GET /me/profile
Authorization: Bearer {{token}}
```

## 3. Каталог с фильтром

```http
GET /delishies?mealRole=SOUP&cuisineId=1
```

## 4. Избранное

```http
POST /me/favorites
Authorization: Bearer {{token}}
Content-Type: application/json

{ "delishiesId": 1 }
```

## 5. План и генерация

```http
POST /me/meal-plans
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "name": "Демо неделя",
  "startDate": "2026-06-02",
  "endDate": "2026-06-08"
}
```

```http
POST /me/meal-plans/1/generate
Authorization: Bearer {{token}}
```

## 6. Корзина

```http
POST /me/shopping-lists/from-meal-plan/1
Authorization: Bearer {{token}}
```

## 7. Рекомендации и аналитика

```http
GET /me/recommendations?limit=5
Authorization: Bearer {{token}}
```

```http
GET /me/analytics?days=30
Authorization: Bearer {{token}}
```

## 8. Голосование

```http
GET /groups/1/votes
Authorization: Bearer {{token}}
```
