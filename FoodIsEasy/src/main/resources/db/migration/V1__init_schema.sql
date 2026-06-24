-- FoodIsEasy — полная схема PostgreSQL

CREATE TABLE cuisines (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500)
);

CREATE TABLE dish_categories (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE product_categories (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    age         INTEGER,
    gender      VARCHAR(20),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE products (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(255) NOT NULL UNIQUE,
    category_id      BIGINT REFERENCES product_categories (id),
    fat_per_100g     DOUBLE PRECISION,
    protein_per_100g DOUBLE PRECISION,
    carb_per_100g    DOUBLE PRECISION,
    kcal_per_100g    INTEGER
);

CREATE TABLE delishies (
    id                 BIGSERIAL PRIMARY KEY,
    title              VARCHAR(255) NOT NULL,
    description        VARCHAR(2000),
    recipe             TEXT,
    image_url          VARCHAR(1000),
    author_id          BIGINT NOT NULL REFERENCES users (id),
    cuisine_id         BIGINT REFERENCES cuisines (id),
    category_id        BIGINT REFERENCES dish_categories (id),
    cook_time_minutes  INTEGER,
    kcal_total         INTEGER,
    protein_total      DOUBLE PRECISION,
    fat_total          DOUBLE PRECISION,
    carb_total         DOUBLE PRECISION,
    avg_rating         DOUBLE PRECISION DEFAULT 0,
    created_at         DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE delishies_products (
    id              BIGSERIAL PRIMARY KEY,
    delishies_id    BIGINT NOT NULL REFERENCES delishies (id) ON DELETE CASCADE,
    product_id      BIGINT NOT NULL REFERENCES products (id),
    quantity_grams  INTEGER NOT NULL,
    CONSTRAINT uq_delishies_product UNIQUE (delishies_id, product_id)
);

CREATE TABLE feedbacks (
    id           BIGSERIAL PRIMARY KEY,
    author_id    BIGINT NOT NULL REFERENCES users (id),
    delishies_id BIGINT NOT NULL REFERENCES delishies (id) ON DELETE CASCADE,
    message      VARCHAR(2000) NOT NULL,
    rating       SMALLINT CHECK (rating >= 1 AND rating <= 5),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE favorite_delishies (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    delishies_id BIGINT NOT NULL REFERENCES delishies (id) ON DELETE CASCADE,
    CONSTRAINT uq_fav_user_delishies UNIQUE (user_id, delishies_id)
);

CREATE TABLE groups (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL UNIQUE,
    owner_id BIGINT NOT NULL REFERENCES users (id)
);

CREATE TABLE group_members (
    id       BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL REFERENCES groups (id) ON DELETE CASCADE,
    user_id  BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role     VARCHAR(20) NOT NULL,
    CONSTRAINT uq_group_user UNIQUE (group_id, user_id)
);

CREATE TABLE user_cuisine_preferences (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    cuisine_id BIGINT NOT NULL REFERENCES cuisines (id),
    weight     INTEGER NOT NULL DEFAULT 1,
    CONSTRAINT uq_user_cuisine UNIQUE (user_id, cuisine_id)
);

CREATE TABLE user_product_preferences (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products (id),
    pref_type  VARCHAR(30) NOT NULL,
    CONSTRAINT uq_user_product_pref UNIQUE (user_id, product_id, pref_type)
);

CREATE TABLE meal_history (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    delishies_id BIGINT NOT NULL REFERENCES delishies (id),
    eaten_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    meal_type    VARCHAR(20) NOT NULL
);

CREATE INDEX idx_meal_history_user_date ON meal_history (user_id, eaten_at DESC);

CREATE TABLE meal_plans (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    user_id    BIGINT REFERENCES users (id) ON DELETE CASCADE,
    group_id   BIGINT REFERENCES groups (id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date   DATE NOT NULL,
    scope      VARCHAR(20) NOT NULL DEFAULT 'PERSONAL',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_meal_plan_owner CHECK (
        (user_id IS NOT NULL AND group_id IS NULL)
        OR (user_id IS NULL AND group_id IS NOT NULL)
    )
);

CREATE TABLE meal_plan_entries (
    id           BIGSERIAL PRIMARY KEY,
    meal_plan_id BIGINT NOT NULL REFERENCES meal_plans (id) ON DELETE CASCADE,
    plan_date    DATE NOT NULL,
    meal_type    VARCHAR(20) NOT NULL,
    delishies_id BIGINT NOT NULL REFERENCES delishies (id),
    CONSTRAINT uq_meal_plan_slot UNIQUE (meal_plan_id, plan_date, meal_type)
);

CREATE TABLE group_votes (
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT NOT NULL REFERENCES groups (id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_by  BIGINT NOT NULL REFERENCES users (id),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    ends_at     TIMESTAMP
);

CREATE TABLE group_vote_options (
    id           BIGSERIAL PRIMARY KEY,
    vote_id      BIGINT NOT NULL REFERENCES group_votes (id) ON DELETE CASCADE,
    delishies_id BIGINT NOT NULL REFERENCES delishies (id),
    CONSTRAINT uq_vote_dish UNIQUE (vote_id, delishies_id)
);

CREATE TABLE group_vote_ballots (
    id        BIGSERIAL PRIMARY KEY,
    vote_id   BIGINT NOT NULL REFERENCES group_votes (id) ON DELETE CASCADE,
    user_id   BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    option_id BIGINT NOT NULL REFERENCES group_vote_options (id) ON DELETE CASCADE,
    voted_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_vote_user UNIQUE (vote_id, user_id)
);

CREATE TABLE shopping_lists (
    id           BIGSERIAL PRIMARY KEY,
    meal_plan_id BIGINT REFERENCES meal_plans (id) ON DELETE SET NULL,
    group_id     BIGINT REFERENCES groups (id) ON DELETE CASCADE,
    user_id      BIGINT REFERENCES users (id) ON DELETE CASCADE,
    status       VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    order_note   VARCHAR(1000),
    ordered_at   TIMESTAMP,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE shopping_list_items (
    id              BIGSERIAL PRIMARY KEY,
    shopping_list_id BIGINT NOT NULL REFERENCES shopping_lists (id) ON DELETE CASCADE,
    product_id      BIGINT NOT NULL REFERENCES products (id),
    quantity_grams  INTEGER NOT NULL,
    checked         BOOLEAN NOT NULL DEFAULT FALSE,
    custom_name     VARCHAR(255),
    CONSTRAINT uq_list_product UNIQUE (shopping_list_id, product_id)
);

CREATE INDEX idx_delishies_cuisine ON delishies (cuisine_id);
CREATE INDEX idx_delishies_category ON delishies (category_id);
CREATE INDEX idx_meal_plan_entries_date ON meal_plan_entries (meal_plan_id, plan_date);
