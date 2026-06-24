-- Надёжные картинки (picsum) и переносы строк в рецептах
UPDATE delishies
SET image_url = 'https://picsum.photos/seed/' || id || '/400/300'
WHERE image_url IS NULL OR image_url LIKE '%unsplash.com%';

UPDATE delishies
SET recipe = REPLACE(recipe, '\n', E'\n')
WHERE recipe LIKE '%\n%' AND recipe NOT LIKE E'%\n%';
