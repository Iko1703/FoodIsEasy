-- placehold.co стабильнее отдаёт картинки в браузере
UPDATE delishies
SET image_url = 'https://placehold.co/400x300/e8f4ea/2d5a3d/png?text=' || id
WHERE image_url LIKE '%picsum.photos%' OR image_url LIKE '%unsplash.com%';
