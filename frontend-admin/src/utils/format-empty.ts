export function formatEmpty(value: unknown): string {
  if (value == null || value === '') return '-'
  if (typeof value === 'string') return value
  if (typeof value === 'number' || typeof value === 'boolean' || typeof value === 'bigint') {
    return value.toString()
  }
  return '-'
}

export function tableEmptyFormatter(_row: unknown, _column: unknown, cellValue: unknown): string {
  return formatEmpty(cellValue)
}
