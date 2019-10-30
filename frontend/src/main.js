import Vue from 'vue'
import App from './App.vue'
import BinTools from './components/BinTools'
import BinDiff from "./components/BinDiff"
import iPadConsolePretty from "./components/iPadConsolePretty"
import ProtoBin from "./components/ProtoBin";
import VueSelect from 'vue-select'
import 'vue-select/dist/vue-select.css'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import VueRouter from "vue-router";
import VueClipboard from 'vue-clipboard2';
import VueHighlightJS from 'vue-highlightjs'

Vue.component('v-select', VueSelect);
Vue.use(ElementUI);
Vue.use(VueRouter);
Vue.use(VueClipboard);
Vue.use(VueHighlightJS);

String.prototype.hexEncode = function(){
  var hex, i;

  var result = "";
  for (i=0; i<this.length; i++) {
      hex = this.charCodeAt(i).toString(16);
      result += ("000"+hex).slice(-4);
  }

  return result
};

String.prototype.hexDecode = function(){
  var j;
  var hexes = this.match(/.{1,4}/g) || [];
  var back = "";
  for(j = 0; j<hexes.length; j++) {
      back += String.fromCharCode(parseInt(hexes[j], 16));
  }

  return back;
};

Uint8Array.prototype.toString = function() {
  return `"${this.utf8StringEncode()}" <${this.hexEncode()}>`;
};

Uint8Array.prototype.hexEncode = function () {
  return Array.prototype.map.call(this, x => ('00' + x.toString(16)).slice(-2)).join('');
};

Uint8Array.prototype.utf8StringEncode = function() {
  return new TextDecoder("utf-8").decode(this);
};

const router = new VueRouter({
  routes: [
    { 
      path: '/', 
      component: BinTools 
    },
    { 
      path: '/bin_diff', 
      component: BinDiff 
    },
    { 
      path: '/console_pretty', 
      component: iPadConsolePretty 
    },
    {
      path: '/proto_bin',
      component: ProtoBin
    }
  ]
});

new Vue({
  el: '#app',
  router,
  render: r => r(App)
});