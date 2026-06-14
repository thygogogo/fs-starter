function formatDateTime(value) {
  if (!value) return ''
  return String(value).replace('T', ' ').substring(0, 19)
}

module.exports = { formatDateTime }
