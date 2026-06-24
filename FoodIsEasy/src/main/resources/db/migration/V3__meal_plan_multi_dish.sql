-- Несколько блюд на один приём пищи (дата + тип), без дубля одного и того же блюда в слоте
ALTER TABLE meal_plan_entries DROP CONSTRAINT IF EXISTS uq_meal_plan_slot;

ALTER TABLE meal_plan_entries
    ADD CONSTRAINT uq_meal_plan_slot_dish UNIQUE (meal_plan_id, plan_date, meal_type, delishies_id);
