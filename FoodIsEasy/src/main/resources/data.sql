-- USERS
INSERT INTO users (email, password, first_name, last_name, age, gender) VALUES
  ('alice@example.com', 'pass', 'Alice', 'Smith', 28, 'FEMALE'),
  ('bob@example.com', 'pass', 'Bob', 'Brown', 32, 'MALE'),
  ('carol@example.com', 'pass', 'Carol', 'Johnson', 24, 'FEMALE'),
  ('dave@example.com', 'pass', 'Dave', 'Wilson', 40, 'MALE'),
  ('erin@example.com', 'pass', 'Erin', 'Davis', 35, 'FEMALE');

-- PRODUCTS
INSERT INTO products (name, fat_per_100g, protein_per_100g, carb_per_100g, kcal_per_100g) VALUES
  ('Овсянка', 6.9, 12.0, 65.0, 370),
  ('Молоко 2.5%', 2.5, 3.0, 4.7, 52),
  ('Куриная грудка', 3.6, 31.0, 0.0, 165),
  ('Рис', 0.7, 7.0, 77.0, 350),
  ('Оливковое масло', 99.0, 0.0, 0.0, 884),
  ('Яйцо куриное', 11.0, 12.7, 0.7, 155),
  ('Творог 5%', 5.0, 17.0, 2.0, 145),
  ('Банан', 0.3, 1.1, 23.0, 96),
  ('Помидор', 0.2, 0.9, 3.9, 18),
  ('Огурец', 0.1, 0.7, 3.6, 15),
  ('Сыр твёрдый', 30.0, 25.0, 0.0, 350),
  ('Гречка', 3.3, 13.0, 68.0, 343);

-- DELISHIES (блюда)
INSERT INTO delishies (title, description, author_id, created_at, image_url) VALUES
  ('Овсянка с молоком', 'Классический завтрак с овсяными хлопьями и молоком. Идеально для начала дня.', (SELECT id FROM users WHERE email='alice@example.com'), CURRENT_DATE, 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400'),
  ('Курица с рисом', 'Простое и сытное блюдо из куриной грудки с отварным рисом. Сбалансированный обед.', (SELECT id FROM users WHERE email='bob@example.com'), CURRENT_DATE, 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400'),
  ('Омлет с сыром', 'Быстрый и питательный омлет с твёрдым сыром. Отличный завтрак или ужин.', (SELECT id FROM users WHERE email='carol@example.com'), CURRENT_DATE, 'https://images.unsplash.com/photo-1525351484163-7529414344d8?w=400'),
  ('Салат овощной', 'Лёгкий и свежий салат из помидоров и огурцов. Витамины и минимум калорий.', (SELECT id FROM users WHERE email='dave@example.com'), CURRENT_DATE, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400'),
  ('Гречка с творогом', 'Нетривиальное, но очень вкусное сочетание гречки с творогом. Белки и углеводы.', (SELECT id FROM users WHERE email='erin@example.com'), CURRENT_DATE, 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400');

-- DELISHIES_PRODUCTS (ингредиенты с граммовкой)
INSERT INTO delishies_products (delishies_id, product_id, quantity_grams) VALUES
  ((SELECT id FROM delishies WHERE title='Овсянка с молоком'), (SELECT id FROM products WHERE name='Овсянка'), 60),
  ((SELECT id FROM delishies WHERE title='Овсянка с молоком'), (SELECT id FROM products WHERE name='Молоко 2.5%'), 200),
  ((SELECT id FROM delishies WHERE title='Курица с рисом'), (SELECT id FROM products WHERE name='Куриная грудка'), 200),
  ((SELECT id FROM delishies WHERE title='Курица с рисом'), (SELECT id FROM products WHERE name='Рис'), 150),
  ((SELECT id FROM delishies WHERE title='Курица с рисом'), (SELECT id FROM products WHERE name='Оливковое масло'), 10),
  ((SELECT id FROM delishies WHERE title='Омлет с сыром'), (SELECT id FROM products WHERE name='Яйцо куриное'), 120),
  ((SELECT id FROM delishies WHERE title='Омлет с сыром'), (SELECT id FROM products WHERE name='Сыр твёрдый'), 30),
  ((SELECT id FROM delishies WHERE title='Салат овощной'), (SELECT id FROM products WHERE name='Помидор'), 100),
  ((SELECT id FROM delishies WHERE title='Салат овощной'), (SELECT id FROM products WHERE name='Огурец'), 100),
  ((SELECT id FROM delishies WHERE title='Гречка с творогом'), (SELECT id FROM products WHERE name='Гречка'), 70),
  ((SELECT id FROM delishies WHERE title='Гречка с творогом'), (SELECT id FROM products WHERE name='Творог 5%'), 150);

-- FEEDBACKS
INSERT INTO feedbacks (author_id, delishies_id, message, created_at) VALUES
  ((SELECT id FROM users WHERE email='alice@example.com'), (SELECT id FROM delishies WHERE title='Курица с рисом'), 'Отличное блюдо после тренировки', NOW()),
  ((SELECT id FROM users WHERE email='bob@example.com'), (SELECT id FROM delishies WHERE title='Овсянка с молоком'), 'Неплохо на завтрак', NOW()),
  ((SELECT id FROM users WHERE email='carol@example.com'), (SELECT id FROM delishies WHERE title='Омлет с сыром'), 'Добавил зелени — супер', NOW()),
  ((SELECT id FROM users WHERE email='dave@example.com'), (SELECT id FROM delishies WHERE title='Салат овощной'), 'Лёгкий и свежий', NOW()),
  ((SELECT id FROM users WHERE email='erin@example.com'), (SELECT id FROM delishies WHERE title='Гречка с творогом'), 'Интересное сочетание', NOW());

-- FAVORITE_DELISHIES
INSERT INTO favorite_delishies (user_id, delishies_id) VALUES
  ((SELECT id FROM users WHERE email='alice@example.com'), (SELECT id FROM delishies WHERE title='Курица с рисом')),
  ((SELECT id FROM users WHERE email='bob@example.com'), (SELECT id FROM delishies WHERE title='Овсянка с молоком')),
  ((SELECT id FROM users WHERE email='carol@example.com'), (SELECT id FROM delishies WHERE title='Омлет с сыром')),
  ((SELECT id FROM users WHERE email='dave@example.com'), (SELECT id FROM delishies WHERE title='Салат овощной')),
  ((SELECT id FROM users WHERE email='erin@example.com'), (SELECT id FROM delishies WHERE title='Гречка с творогом'));

-- GROUPS
INSERT INTO groups (name, owner_id) VALUES
  ('ЗОЖ команда', (SELECT id FROM users WHERE email='alice@example.com')),
  ('Фуд-блогеры', (SELECT id FROM users WHERE email='bob@example.com')),
  ('Семья Смит', (SELECT id FROM users WHERE email='alice@example.com'));

-- GROUP_MEMBERS
INSERT INTO group_members (group_id, user_id, role) VALUES
  ((SELECT id FROM groups WHERE name='ЗОЖ команда'), (SELECT id FROM users WHERE email='alice@example.com'), 'ADMIN'),
  ((SELECT id FROM groups WHERE name='ЗОЖ команда'), (SELECT id FROM users WHERE email='bob@example.com'), 'MEMBER'),
  ((SELECT id FROM groups WHERE name='ЗОЖ команда'), (SELECT id FROM users WHERE email='carol@example.com'), 'MEMBER'),
  ((SELECT id FROM groups WHERE name='Фуд-блогеры'), (SELECT id FROM users WHERE email='bob@example.com'), 'ADMIN'),
  ((SELECT id FROM groups WHERE name='Фуд-блогеры'), (SELECT id FROM users WHERE email='dave@example.com'), 'MEMBER'),
  ((SELECT id FROM groups WHERE name='Семья Смит'), (SELECT id FROM users WHERE email='alice@example.com'), 'ADMIN'),
  ((SELECT id FROM groups WHERE name='Семья Смит'), (SELECT id FROM users WHERE email='erin@example.com'), 'MEMBER');


