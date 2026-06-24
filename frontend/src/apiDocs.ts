export type ApiEndpoint = {
  method: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'
  path: string
  description: string
  auth?: boolean
  body?: string
}

export type ApiModule = {
  id: string
  title: string
  description: string
  endpoints: ApiEndpoint[]
}

export const API_MODULES: ApiModule[] = [
  {
    id: 'auth',
    title: 'Аутентификация',
    description: 'Регистрация и вход, JWT в заголовке Authorization: Bearer …',
    endpoints: [
      { method: 'POST', path: '/auth/register', description: 'Регистрация', body: '{ "email", "password", "firstName", "lastName" }' },
      { method: 'POST', path: '/auth/login', description: 'Вход, возвращает token', body: '{ "email", "password" }' },
    ],
  },
  {
    id: 'catalog',
    title: 'Каталог блюд',
    description: 'Публичный каталог с фильтрами и составом из БД',
    endpoints: [
      { method: 'GET', path: '/delishies', description: 'Список блюд (фильтры: q, cuisineId, categoryId, mealRole, maxKcal, favoritesOnly)' },
      { method: 'GET', path: '/delishies/{id}', description: 'Карточка блюда с ингредиентами и отзывами' },
      { method: 'GET', path: '/cuisines', description: 'Справочник кухонь' },
      { method: 'GET', path: '/dish-categories', description: 'Категории блюд' },
      { method: 'GET', path: '/products', description: 'Справочник продуктов' },
    ],
  },
  {
    id: 'favorites',
    title: 'Избранное и отзывы',
    description: 'Персональные закладки и обратная связь по блюдам',
    endpoints: [
      { method: 'GET', path: '/me/favorites', description: 'Избранные блюда', auth: true },
      { method: 'GET', path: '/me/favorites/ids', description: 'ID избранного', auth: true },
      { method: 'POST', path: '/me/favorites', description: 'Добавить в избранное', auth: true, body: '{ "delishiesId": 1 }' },
      { method: 'DELETE', path: '/me/favorites/{delishiesId}', description: 'Убрать из избранного', auth: true },
      { method: 'POST', path: '/me/delishies/{id}/feedbacks', description: 'Оставить отзыв', auth: true, body: '{ "message", "rating": 1-5 }' },
    ],
  },
  {
    id: 'profile',
    title: 'Профиль и предпочтения',
    description: 'Данные пользователя, кухни, аллергии и нежелательные продукты',
    endpoints: [
      { method: 'GET', path: '/me/profile', description: 'Профиль и группы', auth: true },
      { method: 'PUT', path: '/me/profile', description: 'Обновить профиль', auth: true },
      { method: 'GET', path: '/me/preferences', description: 'Предпочтения', auth: true },
      { method: 'PUT', path: '/me/preferences', description: 'Сохранить предпочтения', auth: true },
    ],
  },
  {
    id: 'nutrition',
    title: 'Питание',
    description: 'История приёмов пищи и недельные планы меню',
    endpoints: [
      { method: 'GET', path: '/me/meal-history', description: 'История (?from=&to=)', auth: true },
      { method: 'POST', path: '/me/meal-history', description: 'Записать приём пищи', auth: true, body: '{ "delishiesId", "mealType" }' },
      { method: 'DELETE', path: '/me/meal-history/{id}', description: 'Удалить запись', auth: true },
      { method: 'GET', path: '/me/meal-plans', description: 'Список планов', auth: true },
      { method: 'POST', path: '/me/meal-plans', description: 'Создать план', auth: true },
      { method: 'GET', path: '/me/meal-plans/{id}', description: 'План с позициями', auth: true },
      { method: 'PUT', path: '/me/meal-plans/{id}/entries', description: 'Добавить блюдо в слот', auth: true },
      { method: 'DELETE', path: '/me/meal-plans/{id}/entries/{entryId}', description: 'Убрать позицию', auth: true },
      { method: 'POST', path: '/me/meal-plans/{id}/generate', description: 'Сгенерировать меню', auth: true },
    ],
  },
  {
    id: 'recommendations',
    title: 'Рекомендации',
    description: 'Rule-based подбор с учётом предпочтений, истории и аллергий',
    endpoints: [
      { method: 'GET', path: '/me/recommendations?limit=12', description: 'Персональные рекомендации', auth: true },
      { method: 'GET', path: '/groups/{id}/recommendations?limit=12', description: 'Рекомендации для группы', auth: true },
    ],
  },
  {
    id: 'analytics',
    title: 'Аналитика',
    description: 'Агрегаты по истории питания и дневные ряды для графиков',
    endpoints: [
      { method: 'GET', path: '/me/analytics?days=30', description: 'Сводка, dailyStats, mealTypeStats', auth: true },
    ],
  },
  {
    id: 'groups',
    title: 'Группы и голосования',
    description: 'Совместные решения: создать группу, голосовать за ужин',
    endpoints: [
      { method: 'POST', path: '/me/groups', description: 'Создать группу', auth: true, body: '{ "name" }' },
      { method: 'POST', path: '/me/groups/join', description: 'Вступить', auth: true, body: '{ "groupId" }' },
      { method: 'GET', path: '/me/groups/browse', description: 'Все группы', auth: true },
      { method: 'GET', path: '/me/groups/{id}', description: 'Участники', auth: true },
      { method: 'GET', path: '/groups/{id}/votes', description: 'Голосования', auth: true },
      { method: 'POST', path: '/groups/{id}/votes', description: 'Создать голосование', auth: true },
      { method: 'POST', path: '/groups/{id}/votes/{voteId}/ballots', description: 'Проголосовать', auth: true },
      { method: 'POST', path: '/groups/{id}/votes/{voteId}/close', description: 'Закрыть и определить победителя', auth: true },
    ],
  },
  {
    id: 'cart',
    title: 'Корзина',
    description: 'Агрегация продуктов из плана меню в общий список покупок',
    endpoints: [
      { method: 'GET', path: '/me/shopping-lists/by-meal-plan/{planId}', description: 'Загрузить корзину', auth: true },
      { method: 'POST', path: '/me/shopping-lists/from-meal-plan/{planId}', description: 'Собрать из меню', auth: true },
      { method: 'PATCH', path: '/me/shopping-lists/{id}/items/{itemId}?checked=true', description: 'Отметить продукт', auth: true },
      { method: 'POST', path: '/me/shopping-lists/{id}/order', description: 'Заявка на заказ (демо)', auth: true },
    ],
  },
]
