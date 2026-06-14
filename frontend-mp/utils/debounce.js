function debounce(fn, wait = 300) {
  let timer = null
  return function (...args) {
    const ctx = this
    clearTimeout(timer)
    timer = setTimeout(() => fn.apply(ctx, args), wait)
  }
}

module.exports = { debounce }
