import Vue from 'vue';
import App from './App.vue';
import langs from '../translations.json';
import router from './router';
import './styles.scss';

Vue.prototype.lang = 'jp';
Vue.prototype.trans = function(str) {
  var result = langs[Vue.prototype.lang][str];
  if(result) {return result;} else {return 'Invalid translation for ' + str + ' in language ' + Vue.prototype.lang;};
};
Vue.config.productionTip = false;


new Vue({
  router,
  render: (h) => h(App),
}).$mount('#app');
