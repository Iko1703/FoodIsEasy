#!/usr/bin/env python3
"""Генерирует V5__rich_demo_data.sql — дополнение к V2 для «живой» демо-БД."""
from pathlib import Path

PWD = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"  # password

def pic_url(title: str) -> str:
    seed = abs(hash(title)) % 12 + 1
    return f"/images/dishes/{seed}.jpg"

CUISINES = [
    ("Грузинская", "Хачапури, хинкали, сацебели"),
    ("Японская", "Суши, рамен, мисо"),
    ("Мексиканская", "Тако, гуакамоле, фахитос"),
    ("Французская", "Круассаны, рататуй"),
    ("Индийская", "Карри, дал, наан"),
    ("Турецкая", "Кебаб, пиде, мезе"),
]

PRODUCTS = [
    ("Лосось", "Мясо", 13, 20, 0, 208),
    ("Говядина", "Мясо", 16, 26, 0, 250),
    ("Свинина", "Мясо", 21, 16, 0, 263),
    ("Картофель", "Овощи", 0.1, 2, 17, 77),
    ("Морковь", "Овощи", 0.2, 0.9, 7, 35),
    ("Лук репчатый", "Овощи", 0.1, 1.1, 9, 41),
    ("Чеснок", "Овощи", 0.5, 6.5, 30, 149),
    ("Капуста", "Овощи", 0.1, 1.3, 5, 27),
    ("Свёкла", "Овощи", 0.1, 1.5, 10, 43),
    ("Шпинат", "Овощи", 0.4, 2.9, 3.6, 23),
    ("Авокадо", "Фрукты", 15, 2, 9, 160),
    ("Яблоко", "Фрукты", 0.2, 0.4, 14, 52),
    ("Лимон", "Фрукты", 0.3, 1.1, 6, 29),
    ("Сметана 20%", "Молочные", 20, 2.8, 3.2, 206),
    ("Йогурт натуральный", "Молочные", 3.2, 5, 4, 60),
    ("Сливки 10%", "Молочные", 10, 3, 4, 118),
    ("Фасоль консервированная", "Овощи", 0.5, 6, 14, 99),
    ("Нут", "Крупы", 6, 19, 61, 364),
    ("Макароны", "Крупы", 1.5, 13, 71, 371),
    ("Лапша рисовая", "Крупы", 0.5, 4, 82, 364),
    ("Креветки", "Мясо", 1, 20, 0, 99),
    ("Тунец консервированный", "Мясо", 1, 24, 0, 116),
    ("Тофу", "Овощи", 4.8, 8, 2, 76),
    ("Имбирь", "Овощи", 0.8, 1.8, 16, 80),
    ("Кинза", "Овощи", 0.5, 2, 1, 23),
    ("Перец болгарский", "Овощи", 0.3, 1, 6, 27),
    ("Брокколи", "Овощи", 0.4, 2.8, 7, 34),
    ("Кукуруза", "Овощи", 1.3, 3.3, 19, 96),
    ("Мёд", "Фрукты", 0, 0.3, 82, 304),
    ("Миндаль", "Фрукты", 53, 21, 20, 579),
]

DISH_TEMPLATES = [
    ("Борщ классический", "SOUP", "Русская", "Обед", "Свёкла, капуста, говядина", 55, 280),
    ("Солянка сборная", "SOUP", "Русская", "Обед", "Колбаса, огурцы, лимон", 40, 320),
    ("Уха из лосося", "SOUP", "Русская", "Обед", "Лосось, картофель", 35, 210),
    ("Куриный суп с лапшой", "SOUP", "Русская", "Обед", "Курица, лапша", 45, 240),
    ("Греческий салат", "SALAD", "Здоровое питание", "Салат", "Помидор, огурец, сыр", 15, 180),
    ("Цезарь с курицей", "SALAD", "Итальянская", "Салат", "Курица, салат, сыр", 20, 350),
    ("Винегрет", "SALAD", "Русская", "Салат", "Свёкла, картофель", 25, 120),
    ("Плов узбекский", "MAIN", "Русская", "Обед", "Рис, морковь, говядина", 60, 480),
    ("Котлеты с пюре", "MAIN", "Русская", "Обед", "Говядина, картофель", 50, 520),
    ("Лосось на гриле", "MAIN", "Здоровое питание", "Ужин", "Лосось, лимон", 25, 380),
    ("Стейк из говядины", "MAIN", "Французская", "Ужин", "Говядина, масло", 30, 450),
    ("Ризотто с грибами", "MAIN", "Итальянская", "Обед", "Рис, сливки", 40, 410),
    ("Паста карбонара", "MAIN", "Итальянская", "Обед", "Макароны, яйцо, сыр", 25, 550),
    ("Тако с говядиной", "MAIN", "Мексиканская", "Обед", "Говядина, авокадо", 30, 420),
    ("Карри с нутом", "MAIN", "Индийская", "Обед", "Нут, томаты", 35, 360),
    ("Рамен с курицей", "MAIN", "Японская", "Обед", "Лапша, курица", 35, 440),
    ("Сырники", "BREAKFAST", "Русская", "Завтрак", "Творог, яйцо", 20, 290),
    ("Блины с мёдом", "BREAKFAST", "Русская", "Завтрак", "Молоко, яйцо, мёд", 25, 320),
    ("Гранола с йогуртом", "BREAKFAST", "Здоровое питание", "Завтрак", "Овсянка, йогурт", 5, 280),
    ("Яичница с авокадо", "BREAKFAST", "Здоровое питание", "Завтрак", "Яйцо, авокадо", 10, 310),
    ("Тирамису", "DESSERT", "Итальянская", "Ужин", "Сыр, кофе", 30, 420),
    ("Чизкейк", "DESSERT", "Американская", "Ужин", "Сыр, сливки", 60, 480),
    ("Фруктовый смузи", "SNACK", "Здоровое питание", "Завтрак", "Банан, йогурт", 5, 150),
    ("Хумус с овощами", "SIDE", "Турецкая", "Обед", "Нут, морковь", 10, 200),
    ("Картофельное пюре", "SIDE", "Русская", "Гарнир", "Картофель, сливки", 30, 220),
    ("Хачапури по-аджарски", "MAIN", "Грузинская", "Обед", "Сыр, яйцо", 40, 520),
    ("Хинкали с мясом", "MAIN", "Грузинская", "Обед", "Говядина, специи", 50, 380),
    ("Мисо-суп", "SOUP", "Японская", "Обед", "Тофу, водоросли", 15, 90),
    ("Суши сет Филадельфия", "MAIN", "Японская", "Обед", "Лосось, рис", 45, 420),
    ("Гуакамоле с начос", "SIDE", "Мексиканская", "Обед", "Авокадо, лимон", 15, 280),
    ("Фахитос с курицей", "MAIN", "Мексиканская", "Обед", "Курица, перец", 25, 390),
    ("Рататуй", "MAIN", "Французская", "Ужин", "Кабачки, томаты", 50, 180),
    ("Круассан с джемом", "BREAKFAST", "Французская", "Завтрак", "Мука, масло", 180, 350),
    ("Дал с рисом", "MAIN", "Индийская", "Обед", "Нут, рис", 40, 340),
    ("Кебаб из баранины", "MAIN", "Турецкая", "Ужин", "Баранина, лук", 35, 410),
    ("Пиде с сыром", "MAIN", "Турецкая", "Обед", "Сыр, тесто", 30, 460),
    ("Окрошка на кефире", "SOUP", "Русская", "Обед", "Огурец, яйцо", 20, 190),
    ("Салат с тунцом", "SALAD", "Здоровое питание", "Салат", "Тунец, яйцо", 15, 220),
    ("Креветки в чесночном соусе", "MAIN", "Итальянская", "Ужин", "Креветки, чеснок", 20, 280),
]

EXTRA_USERS = [
    ("frank@foodiseasy.ru", "Фёдор", "Новиков", 29, "MALE"),
    ("grace@foodiseasy.ru", "Галина", "Петрова", 31, "FEMALE"),
    ("henry@foodiseasy.ru", "Геннадий", "Соколов", 45, "MALE"),
    ("iris@foodiseasy.ru", "Ирина", "Морозова", 27, "FEMALE"),
    ("jack@foodiseasy.ru", "Яков", "Фёдоров", 33, "MALE"),
    ("kate@foodiseasy.ru", "Ксения", "Васильева", 22, "FEMALE"),
    ("leo@foodiseasy.ru", "Лев", "Зайцев", 38, "MALE"),
    ("mary@foodiseasy.ru", "Мария", "Павлова", 26, "FEMALE"),
    ("nick@foodiseasy.ru", "Никита", "Кузнецов", 34, "MALE"),
    ("olga@foodiseasy.ru", "Ольга", "Семёнова", 41, "FEMALE"),
]

lines = ["-- Rich demo data (V5). Password for all users: password", ""]

for name, desc in CUISINES:
    lines.append(f"INSERT INTO cuisines (name, description) VALUES ('{name}', '{desc}') ON CONFLICT (name) DO NOTHING;")

lines.append("INSERT INTO cuisines (name, description) VALUES ('Американская', 'Бургеры, десерты') ON CONFLICT (name) DO NOTHING;")
lines.append("")

for email, fn, ln, age, gender in EXTRA_USERS:
    lines.append(
        f"INSERT INTO users (email, password, first_name, last_name, age, gender) VALUES "
        f"('{email}', '{PWD}', '{fn}', '{ln}', {age}, '{gender}') ON CONFLICT (email) DO NOTHING;"
    )
lines.append("")

for pname, pcat, fat, prot, carb, kcal in PRODUCTS:
    lines.append(
        f"INSERT INTO products (name, category_id, fat_per_100g, protein_per_100g, carb_per_100g, kcal_per_100g) "
        f"SELECT '{pname}', id, {fat}, {prot}, {carb}, {kcal} FROM product_categories WHERE name = '{pcat}' "
        f"ON CONFLICT (name) DO NOTHING;"
    )
lines.append("")

for title, role, cuisine, cat, desc, cook, kcal in DISH_TEMPLATES:
    img = pic_url(title)
    lines.append(
        f"INSERT INTO delishies (title, description, recipe, image_url, author_id, cuisine_id, category_id, "
        f"meal_role, cook_time_minutes, kcal_total, protein_total, fat_total, carb_total, avg_rating) "
        f"SELECT '{title}', '{desc}', E'Рецепт:\\n1. {desc}', '{img}', "
        f"(SELECT id FROM users WHERE email = 'alice@foodiseasy.ru'), "
        f"(SELECT id FROM cuisines WHERE name = '{cuisine}'), "
        f"(SELECT id FROM dish_categories WHERE name = '{cat}'), "
        f"'{role}', {cook}, {kcal}, 20, 12, 40, 4.2 "
        f"WHERE NOT EXISTS (SELECT 1 FROM delishies WHERE title = '{title}');"
    )
lines.append("")

# Groups
lines += [
    "INSERT INTO groups (name, owner_id) SELECT 'Офис FitLunch', u.id FROM users u "
    "WHERE u.email = 'bob@foodiseasy.ru' AND NOT EXISTS (SELECT 1 FROM groups WHERE name = 'Офис FitLunch');",
    "INSERT INTO groups (name, owner_id) SELECT 'Друзья-гурманы', u.id FROM users u "
    "WHERE u.email = 'frank@foodiseasy.ru' AND NOT EXISTS (SELECT 1 FROM groups WHERE name = 'Друзья-гурманы');",
]

members = [
    ("Офис FitLunch", "bob@foodiseasy.ru", "ADMIN"),
    ("Офис FitLunch", "carol@foodiseasy.ru", "MEMBER"),
    ("Офис FitLunch", "dave@foodiseasy.ru", "MEMBER"),
    ("Офис FitLunch", "grace@foodiseasy.ru", "MEMBER"),
    ("Друзья-гурманы", "frank@foodiseasy.ru", "ADMIN"),
    ("Друзья-гурманы", "iris@foodiseasy.ru", "MEMBER"),
    ("Друзья-гурманы", "jack@foodiseasy.ru", "MEMBER"),
    ("Друзья-гурманы", "kate@foodiseasy.ru", "MEMBER"),
]
for gname, email, role in members:
    lines.append(
        f"INSERT INTO group_members (group_id, user_id, role) "
        f"SELECT g.id, u.id, '{role}' FROM groups g, users u "
        f"WHERE g.name = '{gname}' AND u.email = '{email}' "
        f"ON CONFLICT (group_id, user_id) DO NOTHING;"
    )
lines.append("")

# Sample meal history (last 20 days spread)
history_dishes = ["Борщ классический", "Плов узбекский", "Цезарь с курицей", "Овсянка с молоком", "Паста карбонара"]
for i, dish in enumerate(history_dishes):
    email = ["alice@foodiseasy.ru", "bob@foodiseasy.ru", "carol@foodiseasy.ru", "dave@foodiseasy.ru", "erin@foodiseasy.ru"][i]
    lines.append(
        f"INSERT INTO meal_history (user_id, delishies_id, eaten_at, meal_type) "
        f"SELECT u.id, d.id, NOW() - INTERVAL '{i + 2} days', 'LUNCH' "
        f"FROM users u, delishies d WHERE u.email = '{email}' AND d.title = '{dish}';"
    )

# More feedbacks
for title, email, rating, msg in [
    ("Борщ классический", "bob@foodiseasy.ru", 5, "Как у бабушки"),
    ("Рамен с курицей", "iris@foodiseasy.ru", 4, "Сытно на обед"),
    ("Греческий салат", "grace@foodiseasy.ru", 5, "Лёгкий и свежий"),
    ("Тирамису", "kate@foodiseasy.ru", 5, "Отличный десерт"),
]:
    lines.append(
        f"INSERT INTO feedbacks (author_id, delishies_id, message, rating, created_at) "
        f"SELECT u.id, d.id, '{msg}', {rating}, NOW() - INTERVAL '3 days' "
        f"FROM users u, delishies d WHERE u.email = '{email}' AND d.title = '{title}';"
    )
lines.append("")

# Preferences for new users
for email, cuisine, weight in [
    ("frank@foodiseasy.ru", "Грузинская", 4),
    ("iris@foodiseasy.ru", "Японская", 5),
    ("jack@foodiseasy.ru", "Мексиканская", 3),
    ("grace@foodiseasy.ru", "Здоровое питание", 5),
]:
    lines.append(
        f"INSERT INTO user_cuisine_preferences (user_id, cuisine_id, weight) "
        f"SELECT u.id, c.id, {weight} FROM users u, cuisines c "
        f"WHERE u.email = '{email}' AND c.name = '{cuisine}' "
        f"ON CONFLICT (user_id, cuisine_id) DO NOTHING;"
    )

lines.append(
    "INSERT INTO user_product_preferences (user_id, product_id, pref_type) "
    "SELECT u.id, p.id, 'ALLERGY' FROM users u, products p "
    "WHERE u.email = 'iris@foodiseasy.ru' AND p.name = 'Сыр твёрдый' "
    "ON CONFLICT (user_id, product_id, pref_type) DO NOTHING;"
)
lines.append("")

# Meal plan with multi-dish lunch
lines += [
    "INSERT INTO meal_plans (name, user_id, start_date, end_date, scope) "
    "SELECT 'Неделя Алисы', u.id, CURRENT_DATE, CURRENT_DATE + 6, 'PERSONAL' "
    "FROM users u WHERE u.email = 'alice@foodiseasy.ru' "
    "AND NOT EXISTS (SELECT 1 FROM meal_plans WHERE name = 'Неделя Алисы');",
]
for dish, role_check in [
    ("Борщ классический", "LUNCH"),
    ("Плов узбекский", "LUNCH"),
    ("Греческий салат", "LUNCH"),
    ("Сырники", "BREAKFAST"),
    ("Лосось на гриле", "DINNER"),
]:
    lines.append(
        f"INSERT INTO meal_plan_entries (meal_plan_id, plan_date, meal_type, delishies_id) "
        f"SELECT mp.id, CURRENT_DATE, '{role_check}', d.id "
        f"FROM meal_plans mp, delishies d "
        f"WHERE mp.name = 'Неделя Алисы' AND d.title = '{dish}' "
        f"AND NOT EXISTS (SELECT 1 FROM meal_plan_entries e "
        f"JOIN meal_plans p ON e.meal_plan_id = p.id "
        f"WHERE p.name = 'Неделя Алисы' AND e.plan_date = CURRENT_DATE "
        f"AND e.meal_type = '{role_check}' AND e.delishies_id = d.id);"
    )
lines.append("")

# Open group vote
lines += [
    "INSERT INTO group_votes (group_id, title, status, created_by) "
    "SELECT g.id, 'Ужин в пятницу', 'OPEN', u.id FROM groups g, users u "
    "WHERE g.name = 'Офис FitLunch' AND u.email = 'bob@foodiseasy.ru' "
    "AND NOT EXISTS (SELECT 1 FROM group_votes WHERE title = 'Ужин в пятницу');",
]
for dish in ["Лосось на гриле", "Кебаб из баранины", "Рататуй"]:
    lines.append(
        f"INSERT INTO group_vote_options (vote_id, delishies_id) "
        f"SELECT v.id, d.id FROM group_votes v, delishies d "
        f"WHERE v.title = 'Ужин в пятницу' AND d.title = '{dish}' "
        f"ON CONFLICT (vote_id, delishies_id) DO NOTHING;"
    )
lines.append(
    "INSERT INTO group_vote_ballots (vote_id, user_id, option_id) "
    "SELECT v.id, u.id, o.id FROM group_votes v, users u, group_vote_options o, delishies d "
    "WHERE v.title = 'Ужин в пятницу' AND u.email = 'carol@foodiseasy.ru' "
    "AND d.title = 'Лосось на гриле' AND o.vote_id = v.id AND o.delishies_id = d.id "
    "ON CONFLICT (vote_id, user_id) DO NOTHING;"
)

# Состав блюд (название продукта из V2/V5, граммы)
DISH_INGREDIENTS: dict[str, list[tuple[str, int]]] = {
    "Борщ классический": [("Свёкла", 150), ("Капуста", 100), ("Говядина", 120), ("Морковь", 50)],
    "Солянка сборная": [("Свинина", 100), ("Огурец", 80), ("Лимон", 20), ("Лук репчатый", 40)],
    "Уха из лосося": [("Лосось", 200), ("Картофель", 150), ("Морковь", 40), ("Лук репчатый", 30)],
    "Куриный суп с лапшой": [("Куриная грудка", 180), ("Макароны", 60), ("Морковь", 40), ("Лук репчатый", 30)],
    "Греческий салат": [("Помидор", 100), ("Огурец", 100), ("Сыр твёрдый", 50), ("Оливковое масло", 15)],
    "Цезарь с курицей": [("Куриная грудка", 150), ("Сыр твёрдый", 40), ("Яйцо куриное", 50)],
    "Винегрет": [("Свёкла", 120), ("Картофель", 100), ("Морковь", 60), ("Оливковое масло", 10)],
    "Плов узбекский": [("Рис", 180), ("Морковь", 100), ("Говядина", 150), ("Лук репчатый", 50)],
    "Котлеты с пюре": [("Говядина", 200), ("Картофель", 250), ("Яйцо куриное", 30), ("Лук репчатый", 40)],
    "Лосось на гриле": [("Лосось", 220), ("Лимон", 30), ("Оливковое масло", 10)],
    "Стейк из говядины": [("Говядина", 250), ("Оливковое масло", 15), ("Чеснок", 5)],
    "Ризотто с грибами": [("Рис", 160), ("Сливки 10%", 80), ("Лук репчатый", 40), ("Сыр твёрдый", 30)],
    "Паста карбонара": [("Макароны", 200), ("Яйцо куриное", 60), ("Сыр твёрдый", 50), ("Свинина", 80)],
    "Тако с говядиной": [("Говядина", 150), ("Авокадо", 80), ("Помидор", 60), ("Лук репчатый", 30)],
    "Карри с нутом": [("Нут", 120), ("Помидор", 100), ("Лук репчатый", 50), ("Имбирь", 10)],
    "Рамен с курицей": [("Лапша рисовая", 150), ("Куриная грудка", 150), ("Яйцо куриное", 50), ("Имбирь", 8)],
    "Сырники": [("Творог 5%", 200), ("Яйцо куриное", 50), ("Мука", 30)],  # муки нет — заменим на Мёд как подсластитель
    "Блины с мёдом": [("Молоко 2.5%", 200), ("Яйцо куриное", 60), ("Мёд", 40)],
    "Гранола с йогуртом": [("Овсянка", 60), ("Йогурт натуральный", 150), ("Мёд", 20), ("Миндаль", 15)],
    "Яичница с авокадо": [("Яйцо куриное", 120), ("Авокадо", 100)],
    "Тирамису": [("Сыр твёрдый", 80), ("Яйцо куриное", 60), ("Сливки 10%", 100)],
    "Чизкейк": [("Сыр твёрдый", 120), ("Сливки 10%", 150), ("Яйцо куриное", 40)],
    "Фруктовый смузи": [("Банан", 120), ("Йогурт натуральный", 150)],
    "Хумус с овощами": [("Нут", 100), ("Морковь", 80), ("Лимон", 15), ("Чеснок", 5)],
    "Картофельное пюре": [("Картофель", 300), ("Сливки 10%", 50), ("Оливковое масло", 10)],
    "Хачапури по-аджарски": [("Сыр твёрдый", 150), ("Яйцо куриное", 60), ("Молоко 2.5%", 80)],
    "Хинкали с мясом": [("Говядина", 200), ("Лук репчатый", 50), ("Перец болгарский", 30)],
    "Мисо-суп": [("Тофу", 100), ("Лапша рисовая", 40), ("Лук репчатый", 20)],
    "Суши сет Филадельфия": [("Лосось", 150), ("Рис", 120), ("Авокадо", 50)],
    "Гуакамоле с начос": [("Авокадо", 150), ("Лимон", 20), ("Помидор", 50)],
    "Фахитос с курицей": [("Куриная грудка", 180), ("Перец болгарский", 80), ("Лук репчатый", 40)],
    "Рататуй": [("Помидор", 150), ("Кабачки", 100), ("Брокколи", 80), ("Оливковое масло", 15)],
    "Круассан с джемом": [("Молоко 2.5%", 100), ("Яйцо куриное", 50), ("Оливковое масло", 40)],
    "Дал с рисом": [("Нут", 100), ("Рис", 150), ("Лук репчатый", 40), ("Имбирь", 8)],
    "Кебаб из баранины": [("Говядина", 200), ("Лук репчатый", 60), ("Перец болгарский", 50)],
    "Пиде с сыром": [("Сыр твёрдый", 120), ("Макароны", 80), ("Яйцо куриное", 40)],
    "Окрошка на кефире": [("Огурец", 100), ("Яйцо куриное", 60), ("Молоко 2.5%", 200), ("Картофель", 80)],
    "Салат с тунцом": [("Тунец консервированный", 120), ("Яйцо куриное", 50), ("Огурец", 80)],
    "Креветки в чесночном соусе": [("Креветки", 200), ("Чеснок", 15), ("Оливковое масло", 20)],
}
# Исправления продуктов, которых нет в БД
DISH_INGREDIENTS["Сырники"] = [("Творог 5%", 200), ("Яйцо куриное", 50), ("Мёд", 20)]
DISH_INGREDIENTS["Рататуй"] = [("Помидор", 150), ("Брокколи", 100), ("Перец болгарский", 80), ("Оливковое масло", 15)]

v6_lines = ["-- Связи блюд с продуктами для корзины и карточек (V6)", ""]
for dish, ingredients in DISH_INGREDIENTS.items():
    for pname, grams in ingredients:
        v6_lines.append(
            f"INSERT INTO delishies_products (delishies_id, product_id, quantity_grams) "
            f"SELECT d.id, p.id, {grams} FROM delishies d, products p "
            f"WHERE d.title = '{dish}' AND p.name = '{pname}' "
            f"ON CONFLICT (delishies_id, product_id) DO NOTHING;"
        )

migration_dir = Path(__file__).resolve().parents[1] / "FoodIsEasy" / "src" / "main" / "resources" / "db" / "migration"
out = migration_dir / "V5__rich_demo_data.sql"
out.write_text("\n".join(lines) + "\n", encoding="utf-8")
print(f"Wrote {out} ({len(lines)} lines)")

v6_out = migration_dir / "V6__dish_product_links.sql"
v6_out.write_text("\n".join(v6_lines) + "\n", encoding="utf-8")
print(f"Wrote {v6_out} ({len(v6_lines)} lines)")
