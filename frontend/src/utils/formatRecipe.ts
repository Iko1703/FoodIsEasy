/** Превращает литеральные "\\n" из БД в переносы строк */
export function formatRecipe(text: string): string {
  return text.replace(/\\n/g, '\n').trim()
}
