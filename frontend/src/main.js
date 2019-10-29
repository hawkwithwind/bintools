import Vue from 'vue'
import App from './App.vue'
import BinTools from './components/BinTools'
import BinDiff from "./components/BinDiff"
import iPadConsolePretty from "./components/iPadConsolePretty"
import VueSelect from 'vue-select'
import 'vue-select/dist/vue-select.css'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import VueRouter from "vue-router";
import VueClipboard from 'vue-clipboard2'

Vue.component('v-select', VueSelect);
Vue.use(ElementUI);
Vue.use(VueRouter);
Vue.use(VueClipboard)

String.prototype.hexEncode = function(){
  var hex, i;

  var result = "";
  for (i=0; i<this.length; i++) {
      hex = this.charCodeAt(i).toString(16);
      result += ("000"+hex).slice(-4);
  }

  return result
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
  ]
});

new Vue({
  el: '#app',
  router,
  render: r => r(App)
})