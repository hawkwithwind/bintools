<template>
  <div class="pure-g content">

    <div class="pure-u-1-2 left">
      <div class="pure-g row">
        <div class="pure-u-1">
          <el-input type="textarea" :rows="2" placeholder="等待输入" v-model="raw"></el-input>
        </div>
      </div>
      <div class="pure-g row">
        <div class="pure-u-1 primary-actions">
          <el-button type="primary" size="mini" v-on:click="saveNumber">
            数
          </el-button>
          <el-button type="primary" size="mini" v-on:click="saveString">
            串
          </el-button>
          <el-button type="primary" size="mini" v-on:click="saveHex">
            Hex
          </el-button>
          <el-button type="primary" size="mini" v-on:click="saveTimestamp">
            秒
          </el-button>
          <!--
          <button class="pure-button pure-button-primary" v-on:click="saveB64">
            B64
          </button>
          -->
          <el-button type="primary" size="mini" v-on:click="saveNet">
            网
          </el-button>
          <el-button type="primary" size="mini" v-on:click="saveHandshake">
            握手
          </el-button>
          <el-button type="primary" size="mini" v-on:click="savePb">
            PB
          </el-button>
          <el-button type="primary" size="mini" v-on:click="saveP">
            P
          </el-button>
          <el-button type="primary" size="mini" v-on:click="parseMMPackHeader">
            PKHeader
          </el-button>
        </div>
      </div>

      <div class="pure-g row">
        <div class="pure-u-1">
          <el-select v-model="encode.from" size="small" placeholder="hex">
            <el-option v-for="item in ['hex', 'base64', 'varint']" :key="item" :label="item" :value="item"></el-option>
          </el-select>

          <el-select v-model="encode.to" size="small" placeholder="base64">
            <el-option v-for="item in ['hex', 'base64', 'varint']" :key="item" :label="item" :value="item"></el-option>
          </el-select>

          <el-button type="primary" size="small" v-on:click="saveEncode">转码</el-button>
        </div>
      </div>

      <el-card class="box-card" shadow="hover" :body-style="{padding: '10px'}" data-123>
        <div slot="header" class="clearfix">
          <span>加解密</span>
        </div>

        <div class="pure-g row">
          <div class="pure-u-1 row">
            <el-select v-model="symdecrypt.codec" size="small" placeholder="hex">
              <el-option v-for="item in ['hex', 'base64']" :key="item" :label="item" :value="item"></el-option>
            </el-select>

            <el-select v-model="symdecrypt.method" size="small" placeholder="aes/cbc">
              <el-option v-for="item in ['aes/cbc']" :key="item" :label="item" :value="item"></el-option>
            </el-select>

            <el-select v-model="symdecrypt.padding" size="small" placeholder="NoPadding">
              <el-option v-for="item in ['NoPadding', 'PKCS5Padding', 'PKCS7Padding']" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </div>
        </div>
        <div class="pure-g row">
          <div class="pure-u-1 row">
            <el-input size="small"
                      placeholder="密钥"
                      :clearable="true"
                      v-model="symdecrypt.key"></el-input>

            <el-input size="small"
                      placeholder="iv"
                      :clearable="true"
                      v-model="symdecrypt.iv"></el-input>
            <el-button type="primary" size="small" v-on:click="saveCryptText">解密</el-button>
          </div>
        </div>
      </el-card>

      <el-card class="box-card address-calc" shadow="hover" :body-style="{padding: '10px'}" data-123>
        <div slot="header" class="clearfix">
          <span>地址计算器</span>
          <el-button style="float: right; padding: 5px 10px;"
                     plain
                     size="small"
                     @click="addressCalcAdd">添加</el-button>
          <el-button style="float: right; padding: 5px 10px; margin-right: 10px;"
                     plain
                     size="small"
                     icon="el-icon-refresh-right"
                     @click="addressCalcRefreshImageList">lldb imagelist</el-button>
        </div>

        <div class="row" v-for="(image, index) in addressCalc.calculations" :key="index">
          <el-select class="image-select"
                     v-model="image.imageName"
                     filterable
                     size="small"
                     placeholder="Image"
                     @change="addressCalcInputDidChange(index, 'image')">
            <el-option v-for="item in addressCalc.imageNames"
                       :key="item"
                       :label="item"
                       :value="item">
            </el-option>
          </el-select>
          <el-input class="slide"
                    size="small"
                    placeholder="slide"
                    :clearable="true"
                    v-model="image.slide"
                    @change="addressCalcInputDidChange(index, 'slide')"></el-input>
          +
          <el-input class="offset"
                    size="small"
                    :clearable="true"
                    placeholder="offset"
                    v-model="image.offset"
                    @change="addressCalcInputDidChange(index, 'offset')"></el-input>
          =
          <div class="result">{{image.result}}</div>
          <div class="space"/>
          <el-input class="mark"
                    size="mini"
                    placeholder="备注"
                    v-model="image.mark"
                    @change="addressCalcInputDidChange(index, 'mark')"></el-input>
        </div>
      </el-card>

      <div class="pure-g row">
        <div class="pure-u-1 value-display" v-if="select.value" style="padding:3px; max-height:200px; border:1px solid black;">
          {{selected.value}}
        </div>
      </div>
      <div class="pure-g row">
        <div class="pure-u-1-2 value-display" style="padding:3px">
          <pre>{{selected.display.left}}</pre>
        </div>
        <div class="pure-u-1-2 value-display" style="padding:3px">
          <pre>{{selected.display.right}}</pre>
        </div>
      </div>
    </div> <!-- pack & unpack -->
    <div class="pure-u-1-2 right"><!-- right -->
      <div class="pure-g row top" ref="logwindow"><!--log-->
        <div class="pure-u-1">
          <pre>{{logtext}}</pre>
        </div>value
      </div><!--log-->
      <div class="pure-g row bottom" ><!--value list-->
        <div class="pure-u-1">
          <div class="value">
            <ul id="value-list">
              <li v-for="item in items" @on:click="select(item)" v-bind:key="item.id">
                <span v-if="item.valuetype == 'number'">
                  <span class="main-value">{{item.valueb10}}</span>
                  <span class="sub-value">{{item.valueb16}}</span>
                </span>
                <span v-if="item.valuetype == 'buffer'">
                  <span class="main-value"><code style="background-color:#dcdcdc;">
                      {{item.valuestr}}</code></span><br/>
                  <span class="sub-value">{{item.valueb16}}</span>
                </span>
                <span v-if="item.valuetype == 'time'">
                  <span class="main-value"><code style="background-color:#dcdcdc;">
                      {{item.valuestr}}</code></span><br/>
                  <span class="sub-value">{{item.valueb16}}</span>
                </span>
                <span v-if="item.valuetype == 'netbuffer'">
                  {{item.valuenettype}} :
                  <span class="main-value"><code style="background-color:#dcdcdc;">
                      {{item.valuestr}}</code></span><br/>
                  <span class="sub-value">{{item.valueb16}}</span>
                </span>
                <span v-if="item.valuetype == 'handshake'">
                  <span class="main-value"><code style="background-color:#dcdcdc;">
                      {{item.value16}}</code></span><br/>
                  <span class="sub-value">{{item.valueb16}}</span>
                </span>
                <span v-if="item.valuetype == 'calc'">
                  <span class="main-value">{{item.valuestr}}<span class="mark" v-if="item.mark">({{item.mark}})</span></span>
                </span>
              </li>
            </ul>
          </div>
        </div>
      </div><!--value list-->
    </div><!-- right -->
  </div>
</template>

<script>
import axios from 'axios';

function ascii_to_hexa(str) {
  var arr1 = [];
  for (var n = 0, l = str.length; n < l; n ++) 
  {
    var hex = Number(str.charCodeAt(n)).toString(16);
    arr1.push(hex);
  }
  return arr1.join('');
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

export default {
  name: 'BinTools',
  data() {
    return {
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

      addressCalc: {
        calculations: [{
          imageName: null,
          slide: null,
          offset: null,
          result: null,
          mark: null,
        }],
        imageNames:[],
        imageSlideDict: {}
      }
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
    addressCalcAdd() {
      this.addressCalc.calculations.push({
        slide: null,
        offset: null,
        result: null,
      })
    },

    _addressCalcUpdate(calculation) {
      if (calculation.slide && calculation.offset) {
        const slideInt = parseInt(calculation.slide, 16);
        const offsetInt = parseInt(calculation.offset, 16);
        calculation.result = '0x' + (slideInt + offsetInt).toString(16);

        this.items.push({
          valuestr: `0x${slideInt.toString(16)} + 0x${offsetInt.toString(16)} = ${calculation.result}`,
          mark: calculation.mark,
          valuetype: 'calc',
        })
      }
    },

    addressCalcInputDidChange(index, type) {
      const calculation = this.addressCalc.calculations[index];

      if (type === 'image') {
        calculation.slide = this.addressCalc.imageSlideDict[calculation.imageName];
      }

      this._addressCalcUpdate(calculation);
    },
    addressCalcRefreshImageList() {
      if (!this.raw) {
        this.$message.warning("请输入 lldb image list 命令输出结果");
        return;
      }

      const lines = this.raw.split(`\n`);

      const imageSlideDict = {};
      const imageNames = [];
      lines.forEach((line) => {
        const reg = /^\[\s*\d+\]\s+?\S+\s*(0x[\w]+)\s+(.+)$/g;
        const m = reg.exec(line);
        if (!m) {
          return;
        }

        const slide = '0x' + parseInt(m[1], 16).toString(16); // remove trivial zeros
        const paths = m[2].split('/');
        const lastPath = paths[paths.length - 1];
        const imageName = lastPath.split(' ')[0];

        imageNames.push(imageName);
        imageSlideDict[imageName] = slide;
      });

      this.addressCalc.imageNames = imageNames;
      this.addressCalc.imageSlideDict = imageSlideDict;

      this.addressCalc.calculations.forEach((calculation) => {
        if (!calculation.imageName) {
          return;
        }

        const newSlide = this.addressCalc.imageSlideDict[calculation.imageName];
        if (newSlide === calculation.slide) {
          return;
        }

        calculation.slide = newSlide;

        this._addressCalcUpdate(calculation);
      });

      this.$message.success('更新成功');
    },

    async parseMMPackHeader() {
      let resp = await axios({
        method: 'POST',
        url: '/headerUnpack',
        data: {
           text: this.raw
        },
      });

      let retdata =resp.data;
      if (retdata && retdata.result && retdata.message === "ok") {
        const item = {
          value: this.raw,
          valuetype: 'buffer',
          valuestr: this.raw,
          valueb16: retdata.result,
          display: {
            left: this.raw,
            right: retdata.result,
          }
        };

        this.items.push(item);
        this.raw = '';
        this.selected = this.items[this.items.length-1];
      } else {
        apiErrorLog(this, retdata);
        return null
      }
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">

.content {
  height: 100%;

  .left {
    height: 100%;
  }

  .right {
    box-sizing: border-box;
    height: 100%;
    padding: 0px 20px;

    .top {
      border: 1px solid #DCDFE6;
      height: 200px;
      overflow-y: scroll;
      padding: 10px;
      border-radius: 4px;
    }

    .bottom {
      box-sizing: content-box;
      border: 1px solid #DCDFE6;
      height: calc(100% - 220px);
      padding: 10px;
      border-radius: 4px;
    }
  }
}

.primary-actions {
  ::v-deep .el-button {
    font-size: 13px;
  }
}

.row {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-bottom: 10px;

  ::v-deep .el-select {
    margin-right: 10px;
    width: 170px;

    &.padding {
      width: 200px;
    }
  }

  .name {
    margin-right: 10px;
  }

  .el-input {
    width: 200px;
    margin-right: 10px;
    flex-shrink: 0;
  }

  .space {
    flex-grow: 1;
  }
}

ul {
  list-style: none; /* Remove HTML bullets */
  padding: 0;
  margin: 0;
}

li { 
  padding-left: 16px; 
}

li::before {
  content: "="; /* Insert content that looks like bullets */
  padding-right: 8px;
  color: blue; /* Or a color you prefer */
}

.value {
  white-space: nowrap;
  overflow: hidden;
  overflow-x: scroll;
}

.main-value {
  color: black;
}

.sub-value {
  color: gray;
  margin-left: 20px;
}

.value-display {
  word-wrap:break-word;
  overflow-x: hidden; /* Hide horizontal scrollbar */
  overflow-y: auto; /* Add vertical scrollbar */
}

pre {
  margin: 0px;
}

::v-deep .el-card__header {
  padding: 5px 10px;
}

.box-card {
  margin-bottom: 10px;
}

.address-calc {
  .slide {
    width: 150px;
  }
  .offset {
    width: 100px;
    margin-left: 10px;
  }

  .mark {
    width: 220px;

    ::v-deep .el-input__inner {
      padding: 0 4px;
    }
  }

  .result {
    margin-left: 10px;
  }

  .image-select {
    width: 100px;
    margin-right: 10px;
  }
}
</style>
