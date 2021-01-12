import Vue from 'vue';
import Router from 'vue-router';
import ECSiteList from '../shared/pages/ECSiteList.vue';
import ECSiteLogin from '../shared/pages/ECSiteLogin.vue';
Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/:id',
      name: 'EC Site Settings',
      component: ECSiteList,
    },
    {
      path: '/:id/:siteId',
      name: 'EC Site Login',
      component: ECSiteLogin,
    },
  ],
});
