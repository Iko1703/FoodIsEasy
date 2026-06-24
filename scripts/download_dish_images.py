#!/usr/bin/env python3
"""Скачивает реальные фото еды (TheMealDB) — уникальное на каждое блюдо."""
from __future__ import annotations

import json
import re
import ssl
import time
import urllib.parse
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "frontend" / "public" / "images" / "dishes"
MIGRATION = ROOT / "FoodIsEasy" / "src" / "main" / "resources" / "db" / "migration" / "V10__dish_photo_mapping.sql"

# (русское название, варианты поиска в TheMealDB на английском)
DISHES: list[tuple[str, list[str]]] = [
    ("Овсянка с молоком", ["Porridge", "Oatmeal", "Breakfast"]),
    ("Курица с рисом", ["Chicken Congee", "Chicken Rice", "Jerk chicken with rice"]),
    ("Омлет с сыром", ["Omelette", "Cheese Omelette"]),
    ("Салат овощной", ["Garden Salad", "Salad", "Mediterranean Salad"]),
    ("Гречка с творогом", ["Kasha", "Buckwheat", "Porridge"]),
    ("Паста с курицей", ["Chicken Alfredo", "Chicken Pasta", "Fettuccine"]),
    ("Борщ классический", ["Borscht", "Beetroot Soup"]),
    ("Солянка сборная", ["Solyanka", "Minestrone", "Soup"]),
    ("Уха из лосося", ["Fish Soup", "Salmon", "Fish Stew"]),
    ("Куриный суп с лапшой", ["Chicken Noodle Soup", "Chicken Soup"]),
    ("Греческий салат", ["Greek Salad"]),
    ("Цезарь с курицей", ["Caesar Salad", "Chicken Caesar"]),
    ("Винегрет", ["Beetroot Salad", "Russian Salad", "Salad"]),
    ("Плов узбекский", ["Pilaf", "Plov", "Lamb Pilaf"]),
    ("Котлеты с пюре", ["Meatballs", "Meatloaf", "Mashed Potato"]),
    ("Лосось на гриле", ["Salmon", "Grilled Salmon", "Honey Teriyaki Salmon"]),
    ("Стейк из говядины", ["Beef Steak", "Steak", "Beef Bourguignon"]),
    ("Ризотто с грибами", ["Mushroom Risotto", "Risotto"]),
    ("Паста карбонара", ["Carbonara", "Spaghetti Carbonara"]),
    ("Тако с говядиной", ["Tacos", "Beef Tacos"]),
    ("Карри с нутом", ["Chickpea Curry", "Chana Masala", "Curry"]),
    ("Рамен с курицей", ["Ramen", "Chicken Ramen"]),
    ("Сырники", ["Pancakes", "Cheese Pancakes", "Crepe"]),
    ("Блины с мёдом", ["Pancakes", "Blini", "Honey Pancakes"]),
    ("Гранола с йогуртом", ["Granola", "Yogurt", "Fruit and Yogurt"]),
    ("Яичница с авокадо", ["Avocado", "Eggs Benedict", "Breakfast"]),
    ("Тирамису", ["Tiramisu"]),
    ("Чизкейк", ["Cheesecake"]),
    ("Фруктовый смузи", ["Smoothie", "Fruit Smoothie"]),
    ("Хумус с овощами", ["Hummus", "Falafel"]),
    ("Картофельное пюре", ["Mashed Potato", "Potato"]),
    ("Хачапури по-аджарски", ["Khachapuri", "Cheese Bread"]),
    ("Хинкали с мясом", ["Dumplings", "Khinkali", "Manti"]),
    ("Мисо-суп", ["Miso Soup", "Miso"]),
    ("Суши сет Филадельфия", ["Sushi", "Salmon Sushi"]),
    ("Гуакамоле с начос", ["Guacamole", "Nachos"]),
    ("Фахитос с курицей", ["Fajitas", "Chicken Fajitas"]),
    ("Рататуй", ["Ratatouille"]),
    ("Круассан с джемом", ["Croissant", "Pain au Chocolat"]),
    ("Дал с рисом", ["Dal", "Lentil Curry", "Rice and Peas"]),
    ("Кебаб из баранины", ["Kebab", "Lamb Kebab", "Adana"]),
    ("Пиде с сыром", ["Pide", "Turkish Pizza", "Lahmacun"]),
    ("Окрошка на кефире", ["Gazpacho", "Cold Soup", "Okroshka"]),
    ("Салат с тунцом", ["Tuna Salad", "Tuna"]),
    ("Креветки в чесночном соусе", ["Garlic Prawns", "Shrimp", "Prawns"]),
]

CYR = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
LAT = "abvgdeejzijklmnoprstufhzcss_y_eua"


def slugify(title: str) -> str:
    s = title.lower().replace("ё", "е")
    out = []
    for ch in s:
        if ch in CYR:
            out.append(LAT[CYR.index(ch)])
        elif ch.isalnum():
            out.append(ch)
        else:
            out.append("-")
    return re.sub(r"-+", "-", "".join(out)).strip("-") or "dish"


def api_search(term: str) -> str | None:
    q = urllib.parse.quote(term)
    url = f"https://www.themealdb.com/api/json/v1/1/search.php?s={q}"
    req = urllib.request.Request(url, headers={"User-Agent": "FoodIsEasy/1.0"})
    ctx = ssl.create_default_context()
    with urllib.request.urlopen(req, context=ctx, timeout=30) as resp:
        data = json.loads(resp.read().decode())
    meals = data.get("meals")
    if meals:
        return meals[0].get("strMealThumb")
    return None


def download_image(url: str, dest: Path) -> bool:
    if "themealdb.com/images/media/meals/" in url and not url.endswith("/small"):
        url = f"{url}/small"
    req = urllib.request.Request(url, headers={"User-Agent": "FoodIsEasy/1.0"})
    ctx = ssl.create_default_context()
    with urllib.request.urlopen(req, context=ctx, timeout=30) as resp:
        data = resp.read()
    if len(data) < 800:
        return False
    dest.write_bytes(data)
    return True


def find_image(terms: list[str]) -> tuple[str | None, str | None]:
    for term in terms:
        try:
            thumb = api_search(term)
            if thumb:
                return thumb, term
        except Exception as exc:
            print(f"    API '{term}': {exc}")
        time.sleep(0.15)
    return None, None


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    # убрать старые плейсхолдеры 1.jpg..12.jpg
    for old in OUT.glob("*.jpg"):
        try:
            old.unlink()
        except OSError:
            pass

    sql_lines = [
        "-- Уникальное реальное фото для каждого блюда (TheMealDB -> локальный файл)",
        "",
    ]
    ok = 0
    report: list[str] = []

    for idx, (title, terms) in enumerate(DISHES, start=1):
        fname = f"{idx:02d}-{slugify(title)}.jpg"
        dest = OUT / fname
        print(f"[{idx:02d}] {title}")
        thumb, used = find_image(terms)
        if not thumb:
            print(f"  WARN: фото не найдено")
            report.append(f"{title}: NOT FOUND")
            continue
        try:
            if download_image(thumb, dest):
                ok += 1
                print(f"  OK ({used}) -> {fname}")
                report.append(f"{title}: {used}")
            else:
                print(f"  WARN: пустой файл")
        except Exception as exc:
            print(f"  FAIL download: {exc}")
        time.sleep(0.1)

        path = f"/images/dishes/{fname}"
        safe_title = title.replace("'", "''")
        sql_lines.append(
            f"UPDATE delishies SET image_url = '{path}' WHERE title = '{safe_title}';"
        )

    sql_lines.extend(["", f"-- Скачано: {ok}/{len(DISHES)}", ""])
    MIGRATION.write_text("\n".join(sql_lines), encoding="utf-8")
    (ROOT / "scripts" / "dish_photos_report.txt").write_text(
        "\n".join(report), encoding="utf-8"
    )
    print(f"\nГотово: {ok}/{len(DISHES)} фото")
    print(f"Migration: {MIGRATION}")


if __name__ == "__main__":
    main()
