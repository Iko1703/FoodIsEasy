-- Локальные картинки с того же сервера (без внешних CDN)
UPDATE delishies
SET image_url = '/images/dishes/' || (((id - 1) % 12) + 1) || '.jpg';
