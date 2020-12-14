import Vue from 'vue';
import Router from 'vue-router';
import Home from '../shared/pages/Home.vue';
import ECSiteList from '../shared/pages/ECSiteList.vue';
import ECSiteLogin from '../shared/pages/ECSiteLogin.vue';
import ProductGroup from '../shared/pages/ProductGroup.vue';
Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/:id/:siteId',
      name: 'Login',
      component: ECSiteLogin,
    },
  ],
});
