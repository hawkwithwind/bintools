"use strict"

Vue.component('v-select', VueSelect.VueSelect)

String.prototype.hexEncode = function(){
    var hex, i;

    var result = "";
    for (i=0; i<this.length; i++) {
        hex = this.charCodeAt(i).toString(16);
        result += ("000"+hex).slice(-4);
    }

    return result
}

function ascii_to_hexa(str) {
  var arr1 = [];
  for (var n = 0, l = str.length; n < l; n ++) 
  {
    var hex = Number(str.charCodeAt(n)).toString(16);
    arr1.push(hex);
  }
  return arr1.join('');
}

String.prototype.hexDecode = function(){
    var j;
    var hexes = this.match(/.{1,4}/g) || [];
    var back = "";
    for(j = 0; j<hexes.length; j++) {
        back += String.fromCharCode(parseInt(hexes[j], 16));
    }

    return back;
}

function parse(raw) {
  if(!Number.isNaN(parseInt(raw, 10))) {
    return parseInt(raw, 10)
  } else if (!Number.isNaN(parseInt(raw, 16))) {
    return parseInt(raw, 16)
  }

  return NaN
}

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

function displayHex(hex) {
  let ret = displayHexHeader()
  for(var i = 0; i < hex.length/2/16; i++) {
    ret += "0x" + ("000"+i.toString(16)).slice(-4)
    for(var j=0; j<16; j++) {
      if(i*16+j>=hex.length/2) {
        break
      }
      ret += " " + hex[i*32+j*2] + hex[i*32+j*2+1]
    }
    ret += "\n"
  }
  return ret  
}

function displayHexStr(hex) {
  let ret = "\n"
  for(var i = 0; i < hex.length/2/16; i++) {
    for(var j=0; j<16; j++) {
      if(i*16+j>=hex.length/2) {
        break
      }
      let b16 = hex[i*32+j*2] + hex[i*32+j*2+1]
      let b = parseInt(b16, 16)
      if (b >= 32 && b <= 126) {
        try {
          ret += b16.toString().hexDecode()
        } catch(e) {
          console.log(hex.slice(i*16))
          console.log(i, j)
          console.log(b)
          console.log(b16)
        }
      } else {
        ret += "."
      }
    }
    ret += "\n"
  }
  return ret  
}

function saveNumber(raw) {
  let number = parse(raw)
  if(Number.isNaN(number)) {
    return null
  } else {
    let b16 = number.toString(16)
    if (number > 0) {
      b16 = '0x' + b16
    } else {
      b16 = '-0x' + b16.substr(1)
    }
    
    return {
      value: raw,
      valuetype: 'number',
      valueb10: number,
      valueb16: b16,
      display: {
        left: b16,
        right: b16,
      }
    }
  }
}

function saveString(raw) {
  return {
    value: raw,
    valuetype: 'buffer',
    valuestr: raw,
    valueb16: ascii_to_hexa(raw),
    display: {
      left: raw,
      right: ascii_to_hexa(raw),
    }
  }
}

function saveHex(raw) {
  let str = raw.hexDecode()

  let b1 = "0x" + raw.slice(0,2)
  let n1 = parseInt(b1, 16)
  let b2 = "0x" + raw.slice(0,4)
  let n2 = parseInt(b2, 16)
  let b4 = "0x" + raw.slice(0,8)
  let n4 = parseInt(b4, 16)
  let b8 = "0x" + raw.slice(0,16)
  let n8 = parseInt(b8, 16)
  
  return {
    value: raw,
    valuetype: 'buffer',
    valuestr: `${n1}, ${n2}, ${n4}, ${n8}; ` + str,
    valueb16: raw,
    display: {
      left: displayHex(raw),
      right: displayHexStr(raw),
    }
  }
}

function ethHeader(hex) {
  let destMac = "" 
  for(var i = 0; i < 6; i++) {
    if(i*2+1 >= hex.length) {
      return ""
    }
    let b16 = hex[i*2] + hex[i*2+1]
    if(destMac.length > 0) {
      destMac += ":"
    }
    destMac += b16 
  }
  let srcMac = ""
  for(var i = 6; i < 12; i++) {
    if(i*2+1 >= hex.length) {
      return ""
    }
    let b16 = hex[i*2] + hex[i*2+1]
    if(srcMac.length > 0) {
      srcMac += ":"
    }
    srcMac += b16 
  }
  return {
    destMac: destMac,
    srcMac: srcMac,
    header: hex.slice(0,14*2),
  }
}

function ipHeader(hex) {
  let ipheadersize = (parseInt(hex.slice(0,2),16) & 0x0f) << 2
  
  return {
    header: hex.slice(0,ipheadersize*2),
  }
}

function tcpHeader(hex) {
  let tcpheadersize = (parseInt(hex.slice(12*2,13*2), 16) & 0xf0) >> 2
  return {
    header: hex.slice(0,tcpheadersize*2),
  }
}

function httpHeader(hex) {
  let str = ""
  for(var i=0; i<hex.length/2;i++) {
    let b = parseInt(hex.slice(i*2, i*2+2), 16)
    str += String.fromCharCode(b)
  }
  let lines = str.split("\r\n")

  console.log(str)
  console.log(lines)
  
  if(lines[0].endsWith("HTTP/1.1")) {
    let parts = str.split("\r\n\r\n")
    let headers = parts[0]
    let bodyIndex = hex.indexOf("0d0a0d0a")
    bodyIndex += "0d0a0d0a".length
    return {
      headers: headers.split("\r\n"),
      body: hex.slice(bodyIndex),
    }
  } else {
    return {
      headers: [],
      body: hex,
    }
  }
}

function saveNet(raw) {
  let str = raw.hexDecode()
  let eth = ethHeader(raw)

  let ipbuff = raw.slice(eth.header.length)
  let ip = ipHeader(ipbuff)

  let tcpbuff = ipbuff.slice(ip.header.length)
  let tcp = tcpHeader(tcpbuff)
  
  let payload = tcpbuff.slice(tcp.header.length)
  let http = httpHeader(payload)

  let left = ""
  let right = ""

  left += "Ethernet Header:\n"
  left += displayHex(eth.header)
  right += "Ethernet Header:\n" 
  right += displayHexStr(eth.header)

  left += "\nIP Header:\n"
  left += displayHex(ip.header)
  right += "\nIP Header:\n"
  right += displayHexStr(ip.header)

  left += "\nTCP Header:\n"
  left += displayHex(tcp.header)
  right += "\nTCP Header:\n"
  right += displayHexStr(tcp.header)

  let type = "TCP"

  if(http.headers.length > 0) {
    left += "\nHTTP Header:\n"
    left += http.headers.join("\n")
    left += "\n\nPayload:\n"
    left += displayHex(http.body)
    right += "\n\n"
    for(var i=0;i<http.headers.length;i++) {
      right += "\n"
    }
    right += "\nPayload:\n"
    right += displayHexStr(http.body)
    payload = http.body
    type = "HTTP"
  } else {
    left += "\nPayload:\n"
    left += displayHex(payload)
    right += "\nPayload:\n"
    right += displayHexStr(payload)
  }

  return {
    value: raw,
    valuetype: 'netbuffer',
    valuestr: payload.hexDecode(),
    valueb16: payload,
    valuenettype: type,
    display: {
      left: left,
      right: right,
    }
  }
}

function arrayToHex(buff) {
  let ret = ""
  console.log(buff)
  for(var i=0;i<buff.length;i++){
    ret += ("000" + buff[i].toString(16)).slice(-2)
    console.log(buff[i])
    console.log(ret)
  }
  return ret
}

function saveHandshake(hex) {
  let buffer = []
  for(var i=0; i<hex.length/2;i++) {
    let b = parseInt(hex.slice(i*2,i*2+2), 16)
    buffer.push(b)
  }

  let parts = []
  let raw = buffer
  
  for(var c = 0; c<10;c++) {
    if(raw.length<=5) {
      break
    }
    let header = raw.slice(0, 5)
    if([0x15,0x16,0x17,0x19].includes(header[0])) {
      let size = header[3]*256 + header[4]
      let body = raw.slice(5, size+5)
      raw = raw.slice(size+5)
      parts.push({
        header: header,
        body: body,
      })
    } else {
      break
    }
  }

  let left = ""
  let right = ""
  
  for(var i=0; i<parts.length; i++) {
    left += "\n [" + i + "]\n"
    right += "\n\n"
    let hexstr = arrayToHex(parts[i].header.concat(parts[i].body))
    left += displayHex(hexstr)
    right += displayHexStr(hexstr)
  }

  return {
    value: hex,
    valuetype: 'handshake',
    valuestr: hex,
    valueb16: hex,
    display: {
      left: left,
      right: right,
    }
  }
}

function apiErrorLog(app, retdata) {
  var date = new Date()
  var hour = ("000"+date.getHours()).slice(-2)
  var minute = ("000"+date.getMinutes()).slice(-2)
  var second = ("000"+date.getSeconds()).slice(-2)
  
  let valuestr = hour+":"+minute+":"+second
  
  app.logtext += valuestr + " " +retdata.message + "\n"
  setTimeout(function() {
    app.$refs.logwindow.scrollTop = app.$refs.logwindow.scrollHeight;
  }, 100)
}

async function saveEncode(app, raw) {
  let resp = await axios({
    method: 'POST',
    url: '/encode',
    data: {fromCodec: app.encode.from, toCodec: app.encode.to, text: raw},
  })

  let retdata =resp.data
  if (retdata && retdata.result && retdata.message == "ok") {
    return {
      value: raw,
      valuetype: 'buffer',
      valuestr: raw,
      valueb16: retdata.result,
      display: {
        left: raw,
        right: retdata.result,
      }
    }
  } else {
    apiErrorLog(app, retdata)
    return null
  }
}

async function saveCryptText(app, decryptRequest) {
  let resp = await axios({
    method: 'POST',
    url: '/decrypt',
    data: decryptRequest,
  })
  let retdata = resp.data
  if (retdata && retdata.result && retdata.message == "ok") {
    return {
      value: decryptRequest.cryptText,
      valuetype: 'buffer',
      valuestr: decryptRequest.cryptText,
      valueb16: retdata.result,
      display: {
        left: retdata.result,
        right: retdata.result,
      }
    }
  } else {
    apiErrorLog(app, retdata)
    return null
  }
}

async function savePb(app, raw) {
  let resp = await axios({
    method: 'POST',
    url: '/pbunpack',
    data: {codec:'hex', text:raw},
  })

  let retdata = resp.data
  if (retdata && retdata.result && retdata.message == "ok") {
    return {
      value: raw,
      valuetype: 'buffer',
      valuestr: retdata.result,
      display: {left: displayHex(raw), right: retdata.result}
    }
  } else {
    apiErrorLog(app, retdata)
    return null;
  }
}

async function saveP(app, raw) {
  let resp = await axios({
    method: 'POST',
    url: 'http://127.0.0.1:18868/unpack',
    data: {codec: 'hex', text: raw},
  })

  let retdata = resp.data
  if (retdata && retdata.result && retdata.message == "ok") {
    return {
      value: raw,
      valuetype: 'buffer',
      valuestr: retdata.result,
      display: {left: displayHex(raw), right: retdata.result},
    }
  } else {
    apiErrorLog(app, retdata)
    return null
  }
}

function saveB64(raw) {
  return null
}

function preprocess(raw) {
  let ret = raw
  try {
    ret = eval(raw)
  } catch (e) {
    console.log(e)
  }
  return ret
}

var app = new Vue({
  el: '#app',
  data: {
    logtext: '',
    raw: '',
    items: [],
    log: 'hello, bin tools\n',
    selected: {raw:"", value:"", display:{}},
    encode: {
      from: "hex",
      to: "base64",
    },
    symdecrypt: {
      method: "aes/cbc",
      codec: "hex",
      padding: "NoPadding",
      cryptText: "",
      key: "",
      iv: "",
    },
  },
  mounted () {
    window.onbeforeunload = function (e) {
      // Cancel the event as stated by the standard.
      event.preventDefault()
      // Chrome requires returnValue to be set.
      event.returnValue = '确定要刷新页面吗？'
      return '确定要刷新页面吗？'
    }
  },
  methods: {
    saveNumber: function() {
      let ret = preprocess(this.raw)
      let item = saveNumber(ret)
      if(item != null) {
        this.items.push(item)
        this.raw = ''
        this.selected = this.items[this.items.length-1]
      }
    },
    saveString: function() {
      let ret = preprocess(this.raw)
      this.items.push(saveString(ret))
      this.raw = ''
      this.selected = this.items[this.items.length-1]
    },
    saveHex: function() {
      this.items.push(saveHex(this.raw))
      this.raw = ''
      this.selected = this.items[this.items.length-1]
    },
    saveB64: function() {
    },
    saveNet: function() {
      this.items.push(saveNet(this.raw))
      this.raw = ''
      this.selected = this.items[this.items.length-1]
    },
    saveHandshake: function() {
      this.items.push(saveHandshake(this.raw))
      this.raw = ''
      this.selected = this.items[this.items.length-1]
    },
    saveEncode: async function() {
      let resp = await saveEncode(this, this.raw)
      if (resp) {
        this.items.push(resp)
        this.raw = ''
        this.selected = this.items[this.items.length-1]
      }
    },
    saveP: async function() {
      let resp = await saveP(this, this.raw)
      if (resp) {
        this.items.push(resp)
        this.raw = ''
        this.selected = this.items[this.items.length-1]
      }
    },
    savePb: async function() {
      let resp = await savePb(this, this.raw)
      if(resp) {
        this.items.push(resp)
        this.raw = ''
        this.selected = this.items[this.items.length-1]
      }
    },
    saveCryptText: async function() {
      this.symdecrypt.cryptText = this.raw
      
      let resp = await saveCryptText(this, this.symdecrypt)
      if(resp) {
        this.items.push(resp)
        this.raw = ''
        this.selected = this.items[this.items.length-1]
      }
    },
    saveTimestamp: function() {
      let ret = preprocess(this.raw)

      var date = new Date(ret*1000)
      var day = date.getDate()
      var monthIndex = date.getMonth()
      var year = date.getFullYear()
      var hour = date.getHours()
      var minute = date.getMinutes()
      var second = date.getSeconds()

      let valuestr = year+'-'+(monthIndex+1)+'-'+day + " " + hour+":"+minute+":"+second
      
      this.items.push({
        value: ret,
        valuestr: valuestr,
        valueb16: ret,
        valuetype: 'time',
        display: {
          left: ret,
          right: valuestr,
        }
      })
      this.raw = ''
      this.selected = this.items[this.items.length-1]
    },
    select: function(item) {
      this.selected = item
      console.log(item)
    },
  }
})
