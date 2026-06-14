export function truncateMiddle(
  value: number | string | null | undefined,
  head = 4,
  tail = 4,
): string {
  const str = value == null ? '' : String(value)
  if (str.length <= head + tail + 3) return str
  return `${str.slice(0, head)}...${str.slice(-tail)}`
}

export function shouldTruncateId(
  value: number | string | null | undefined,
  head = 4,
  tail = 4,
): boolean {
  const str = value == null ? '' : String(value)
  return str.length > head + tail + 3
}
