-- Демо-данные. Пароль для всех: password
-- BCrypt hash of "password"

INSERT INTO product_categories (name) VALUES
    ('Крупы'),
    ('Молочные'),
    ('Мясо'),
    ('Овощи'),
    ('Фрукты'),
    ('Масла');

INSERT INTO dish_categories (name) VALUES
    ('Завтрак'),
    ('Обед'),
    ('Ужин'),
    ('Салат'),
    ('Гарнир');

INSERT INTO cuisines (name, description) VALUES
    ('Русская', 'Традиционная русская кухня'),
    ('Итальянская', 'Паста, ризотто, пицца'),
    ('Азиатская', 'Рис, лапша, соевый соус'),
    ('Здоровое питание', 'Сбалансированные низкокалорийные блюда');

INSERT INTO users (email, password, first_name, last_name, age, gender) VALUES
    ('alice@foodiseasy.ru', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Алиса', 'Смирнова', 28, 'FEMALE'),
    ('bob@foodiseasy.ru', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Борис', 'Козлов', 32, 'MALE'),
    ('carol@foodiseasy.ru', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Карина', 'Иванова', 24, 'FEMALE'),
    ('dave@foodiseasy.ru', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Дмитрий', 'Волков', 40, 'MALE'),
    ('erin@foodiseasy.ru', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Елена', 'Давыдова', 35, 'FEMALE');

INSERT INTO products (name, category_id, fat_per_100g, protein_per_100g, carb_per_100g, kcal_per_100g) VALUES
    ('Овсянка', (SELECT id FROM product_categories WHERE name = 'Крупы'), 6.9, 12.0, 65.0, 370),
    ('Молоко 2.5%', (SELECT id FROM product_categories WHERE name = 'Молочные'), 2.5, 3.0, 4.7, 52),
    ('Куриная грудка', (SELECT id FROM product_categories WHERE name = 'Мясо'), 3.6, 31.0, 0.0, 165),
    ('Рис', (SELECT id FROM product_categories WHERE name = 'Крупы'), 0.7, 7.0, 77.0, 350),
    ('Оливковое масло', (SELECT id FROM product_categories WHERE name = 'Масла'), 99.0, 0.0, 0.0, 884),
    ('Яйцо куриное', (SELECT id FROM product_categories WHERE name = 'Молочные'), 11.0, 12.7, 0.7, 155),
    ('Творог 5%', (SELECT id FROM product_categories WHERE name = 'Молочные'), 5.0, 17.0, 2.0, 145),
    ('Банан', (SELECT id FROM product_categories WHERE name = 'Фрукты'), 0.3, 1.1, 23.0, 96),
    ('Помидор', (SELECT id FROM product_categories WHERE name = 'Овощи'), 0.2, 0.9, 3.9, 18),
    ('Огурец', (SELECT id FROM product_categories WHERE name = 'Овощи'), 0.1, 0.7, 3.6, 15),
    ('Сыр твёрдый', (SELECT id FROM product_categories WHERE name = 'Молочные'), 30.0, 25.0, 0.0, 350),
    ('Гречка', (SELECT id FROM product_categories WHERE name = 'Крупы'), 3.3, 13.0, 68.0, 343);

INSERT INTO delishies (title, description, recipe, image_url, author_id, cuisine_id, category_id, cook_time_minutes, kcal_total, protein_total, fat_total, carb_total, avg_rating) VALUES
    ('Овсянка с молоком', 'Классический завтрак с овсяными хлопьями и молоком.',
     '1. Залейте овсянку молоком.\n2. Варите 5–7 минут на среднем огне.\n3. Подавайте тёплой.',
     'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400',
     (SELECT id FROM users WHERE email = 'alice@foodiseasy.ru'),
     (SELECT id FROM cuisines WHERE name = 'Русская'),
     (SELECT id FROM dish_categories WHERE name = 'Завтрак'),
     10, 320, 12.0, 8.0, 48.0, 4.5),
    ('Курица с рисом', 'Сытное блюдо из куриной грудки с отварным рисом.',
     '1. Обжарьте курицу.\n2. Отварите рис.\n3. Подавайте с маслом.',
     'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400',
     (SELECT id FROM users WHERE email = 'bob@foodiseasy.ru'),
     (SELECT id FROM cuisines WHERE name = 'Русская'),
     (SELECT id FROM dish_categories WHERE name = 'Обед'),
     35, 520, 42.0, 12.0, 55.0, 4.8),
    ('Омлет с сыром', 'Быстрый омлет с твёрдым сыром.',
     '1. Взбейте яйца.\n2. Добавьте сыр.\n3. Жарьте на сковороде 8 минут.',
     'https://images.unsplash.com/photo-1525351484163-7529414344d8?w=400',
     (SELECT id FROM users WHERE email = 'carol@foodiseasy.ru'),
     (SELECT id FROM cuisines WHERE name = 'Русская'),
     (SELECT id FROM dish_categories WHERE name = 'Завтрак'),
     15, 280, 22.0, 18.0, 2.0, 4.2),
    ('Салат овощной', 'Свежий салат из помидоров и огурцов.',
     '1. Нарежьте овощи.\n2. Заправьте по вкусу.\n3. Подавайте сразу.',
     'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400',
     (SELECT id FROM users WHERE email = 'dave@foodiseasy.ru'),
     (SELECT id FROM cuisines WHERE name = 'Здоровое питание'),
     (SELECT id FROM dish_categories WHERE name = 'Салат'),
     10, 45, 2.0, 0.5, 8.0, 4.0),
    ('Гречка с творогом', 'Гречка с творогом — белки и углеводы.',
     '1. Отварите гречку.\n2. Добавьте творог.\n3. Перемешайте.',
     'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400',
     (SELECT id FROM users WHERE email = 'erin@foodiseasy.ru'),
     (SELECT id FROM cuisines WHERE name = 'Русская'),
     (SELECT id FROM dish_categories WHERE name = 'Ужин'),
     25, 380, 28.0, 8.0, 52.0, 4.3),
    ('Паста с курицей', 'Итальянское блюдо с курицей и рисом.',
     '1. Отварите рис al dente.\n2. Обжарьте курицу.\n3. Смешайте и подавайте.',
     'https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=400',
     (SELECT id FROM users WHERE email = 'bob@foodiseasy.ru'),
     (SELECT id FROM cuisines WHERE name = 'Итальянская'),
     (SELECT id FROM dish_categories WHERE name = 'Обед'),
     30, 490, 38.0, 14.0, 50.0, 4.6);

INSERT INTO delishies_products (delishies_id, product_id, quantity_grams) VALUES
    ((SELECT id FROM delishies WHERE title = 'Овсянка с молоком'), (SELECT id FROM products WHERE name = 'Овсянка'), 60),
    ((SELECT id FROM delishies WHERE title = 'Овсянка с молоком'), (SELECT id FROM products WHERE name = 'Молоко 2.5%'), 200),
    ((SELECT id FROM delishies WHERE title = 'Курица с рисом'), (SELECT id FROM products WHERE name = 'Куриная грудка'), 200),
    ((SELECT id FROM delishies WHERE title = 'Курица с рисом'), (SELECT id FROM products WHERE name = 'Рис'), 150),
    ((SELECT id FROM delishies WHERE title = 'Курица с рисом'), (SELECT id FROM products WHERE name = 'Оливковое масло'), 10),
    ((SELECT id FROM delishies WHERE title = 'Омлет с сыром'), (SELECT id FROM products WHERE name = 'Яйцо куриное'), 120),
    ((SELECT id FROM delishies WHERE title = 'Омлет с сыром'), (SELECT id FROM products WHERE name = 'Сыр твёрдый'), 30),
    ((SELECT id FROM delishies WHERE title = 'Салат овощной'), (SELECT id FROM products WHERE name = 'Помидор'), 100),
    ((SELECT id FROM delishies WHERE title = 'Салат овощной'), (SELECT id FROM products WHERE name = 'Огурец'), 100),
    ((SELECT id FROM delishies WHERE title = 'Гречка с творогом'), (SELECT id FROM products WHERE name = 'Гречка'), 70),
    ((SELECT id FROM delishies WHERE title = 'Гречка с творогом'), (SELECT id FROM products WHERE name = 'Творог 5%'), 150),
    ((SELECT id FROM delishies WHERE title = 'Паста с курицей'), (SELECT id FROM products WHERE name = 'Куриная грудка'), 180),
    ((SELECT id FROM delishies WHERE title = 'Паста с курицей'), (SELECT id FROM products WHERE name = 'Рис'), 140);

INSERT INTO feedbacks (author_id, delishies_id, message, rating, created_at) VALUES
    ((SELECT id FROM users WHERE email = 'alice@foodiseasy.ru'), (SELECT id FROM delishies WHERE title = 'Курица с рисом'), 'Отличное блюдо после тренировки', 5, NOW()),
    ((SELECT id FROM users WHERE email = 'bob@foodiseasy.ru'), (SELECT id FROM delishies WHERE title = 'Овсянка с молоком'), 'Неплохо на завтрак', 4, NOW()),
    ((SELECT id FROM users WHERE email = 'carol@foodiseasy.ru'), (SELECT id FROM delishies WHERE title = 'Омлет с сыром'), 'Добавила зелени — супер', 5, NOW());

INSERT INTO favorite_delishies (user_id, delishies_id) VALUES
    ((SELECT id FROM users WHERE email = 'alice@foodiseasy.ru'), (SELECT id FROM delishies WHERE title = 'Курица с рисом')),
    ((SELECT id FROM users WHERE email = 'bob@foodiseasy.ru'), (SELECT id FROM delishies WHERE title = 'Овсянка с молоком'));

INSERT INTO groups (name, owner_id) VALUES
    ('ЗОЖ команда', (SELECT id FROM users WHERE email = 'alice@foodiseasy.ru')),
    ('Семья Смирновых', (SELECT id FROM users WHERE email = 'alice@foodiseasy.ru'));

INSERT INTO group_members (group_id, user_id, role) VALUES
    ((SELECT id FROM groups WHERE name = 'ЗОЖ команда'), (SELECT id FROM users WHERE email = 'alice@foodiseasy.ru'), 'ADMIN'),
    ((SELECT id FROM groups WHERE name = 'ЗОЖ команда'), (SELECT id FROM users WHERE email = 'bob@foodiseasy.ru'), 'MEMBER'),
    ((SELECT id FROM groups WHERE name = 'ЗОЖ команда'), (SELECT id FROM users WHERE email = 'carol@foodiseasy.ru'), 'MEMBER'),
    ((SELECT id FROM groups WHERE name = 'Семья Смирновых'), (SELECT id FROM users WHERE email = 'alice@foodiseasy.ru'), 'ADMIN'),
    ((SELECT id FROM groups WHERE name = 'Семья Смирновых'), (SELECT id FROM users WHERE email = 'erin@foodiseasy.ru'), 'MEMBER');

INSERT INTO user_cuisine_preferences (user_id, cuisine_id, weight) VALUES
    ((SELECT id FROM users WHERE email = 'alice@foodiseasy.ru'), (SELECT id FROM cuisines WHERE name = 'Русская'), 3),
    ((SELECT id FROM users WHERE email = 'alice@foodiseasy.ru'), (SELECT id FROM cuisines WHERE name = 'Здоровое питание'), 2);

INSERT INTO user_product_preferences (user_id, product_id, pref_type) VALUES
    ((SELECT id FROM users WHERE email = 'alice@foodiseasy.ru'), (SELECT id FROM products WHERE name = 'Куриная грудка'), 'FAVORITE'),
    ((SELECT id FROM users WHERE email = 'dave@foodiseasy.ru'), (SELECT id FROM products WHERE name = 'Сыр твёрдый'), 'ALLERGY');
