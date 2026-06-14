Component({
  properties: {
    title: { type: String, value: '' },
    presets: { type: Array, value: [] },
    selected: { type: Array, value: [] },
    multiple: { type: Boolean, value: true }
  },

  observers: {
    'presets, selected': function () {
      this.updateDisplayTags()
    }
  },

  data: {
    customInput: '',
    showCustomInput: false,
    presetTags: [],
    customTags: []
  },

  lifetimes: {
    attached() {
      this.updateDisplayTags()
    }
  },

  methods: {
    updateDisplayTags() {
      const presets = this.properties.presets || []
      const selected = this.properties.selected || []
      const presetTags = presets.map((tag) => ({
        tag,
        active: selected.includes(tag)
      }))
      const customTags = selected
        .filter((tag) => !presets.includes(tag))
        .map((tag) => ({ tag, active: true }))
      this.setData({ presetTags, customTags })
    },

    onTagTap(e) {
      const tag = e.currentTarget.dataset.tag
      const { selected, multiple } = this.properties
      let next = [...(selected || [])]

      if (multiple) {
        const idx = next.indexOf(tag)
        if (idx >= 0) {
          next.splice(idx, 1)
        } else {
          next.push(tag)
        }
      } else {
        next = next.includes(tag) ? [] : [tag]
      }

      this.triggerEvent('change', { value: next })
    },

    toggleCustomInput() {
      this.setData({
        showCustomInput: !this.data.showCustomInput,
        customInput: ''
      })
    },

    onCustomInput(e) {
      this.setData({ customInput: e.detail.value })
    },

    addCustomTag() {
      const tag = (this.data.customInput || '').trim()
      if (!tag) {
        wx.showToast({ title: '请输入标签内容', icon: 'none' })
        return
      }
      const selected = [...(this.properties.selected || [])]
      if (selected.includes(tag)) {
        wx.showToast({ title: '标签已存在', icon: 'none' })
        return
      }
      selected.push(tag)
      this.setData({ customInput: '', showCustomInput: false })
      this.triggerEvent('change', { value: selected })
    }
  }
})
