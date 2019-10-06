"use strict"

function displayHexHeader() {
  let ret = ""
  for(var i = 0; i <  "0x0000".length; i++) {
    ret += " "
  }
  for(var i = 0; i < 16; i++) {
    ret += "  " + i.toString(16)
  }
  ret+="\n"
  return ret
}

function compareHex(hex, rhs) {
  let ret = displayHexHeader()
  for(var i = 0; i < hex.length/2/16; i++) {
    ret += "0x" + ("000"+i.toString(16)).slice(-4)
    for(var j=0; j<16; j++) {
      if(i*16+j>=hex.length/2) {
        break
      }
      var feed = hex[i*32+j*2] + hex[i*32+j*2+1]
      var fed = false
      if(i*16+j<rhs.length/2) {
        var rfeed = rhs[i*32+j*2] + rhs[i*32+j*2+1]
        if (feed == rfeed) {
          ret += " " + feed
          fed = true
        }
      }

      if(!fed) {
        ret += ' <span class="red">'+feed+'</span>'
      }
    }
    ret += "\n"
  }
  return '<pre>' + ret + '</pre>'
}

var app = new Vue({
  el: '#app',
  data: {
    raw: {
      left: '',
      right: '',
    },
    display: {
      left: '',
      right: '',
    },
  },
  methods: {
    compare: function() {
      this.display.left = compareHex(this.raw.left, this.raw.right)
      this.display.right = compareHex(this.raw.right, this.raw.left)
    },
  },
})
