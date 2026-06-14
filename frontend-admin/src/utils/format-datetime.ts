import dayjs from 'dayjs'

export function formatDateTime(value: string | number | Date | null | undefined): string {
  if (value == null || value === '') return '-'
  const d = dayjs(value)
  return d.isValid() ? d.format('YYYY/MM/DD HH:mm') : '-'
}
