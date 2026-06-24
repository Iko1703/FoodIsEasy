-- Метка роли блюда в приёме пищи (суп, основное, салат и т.д.)
ALTER TABLE delishies ADD COLUMN meal_role VARCHAR(30);

UPDATE delishies SET meal_role = 'BREAKFAST' WHERE title IN ('Овсянка с молоком', 'Омлет с сыром');
UPDATE delishies SET meal_role = 'MAIN' WHERE title IN ('Курица с рисом', 'Гречка с творогом', 'Паста с курицей');
UPDATE delishies SET meal_role = 'SALAD' WHERE title = 'Салат овощной';

UPDATE delishies SET meal_role = 'MAIN' WHERE meal_role IS NULL;

ALTER TABLE delishies ALTER COLUMN meal_role SET NOT NULL;
ALTER TABLE delishies ALTER COLUMN meal_role SET DEFAULT 'MAIN';

CREATE INDEX idx_delishies_meal_role ON delishies (meal_role);
