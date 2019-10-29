<template>
  <div class="pure-g row content">
    <div class="pure-u-1-2"> <!-- left -->
      <div class="pure-u-1">
        <textarea class="pure-u-1" v-model="raw.left" placeholder="等待输入" rows="10" @change="compare" @input="compare"></textarea>
      </div>
      <div class="pure-u-1 value-display">
        <pre v-html="display.left"></pre>
      </div>
    </div> <!-- left end-->
    <div class="pure-u-1-2"> <!-- right -->
      <div class="pure-u-1">
        <textarea class="pure-u-1" v-model="raw.right" placeholder="等待输入" rows="10" @change="compare" @input="compare"></textarea>
      </div>
      <div class="pure-u-1 value-display">
        <pre v-html="display.right"></pre>
      </div>
    </div><!--right end-->
  </div>
</template>

<script>

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

export default {
  name: 'BinDiff',
  data() {
    return {
      raw: {
        left: '',
        right: '',
      },
      display: {
        left: '',
        right: '',
      },
    }
  },
  mounted () {
  },

  methods: {
    compare: function() {
      this.display.left = compareHex(this.raw.left, this.raw.right)
      this.display.right = compareHex(this.raw.right, this.raw.left)
    },
  }
}
</script>

<style lang="scss">


</style>
