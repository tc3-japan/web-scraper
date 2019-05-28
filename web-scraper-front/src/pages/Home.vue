<template>
  <div class="home">
     <h2>{{ trans('title') }} </h2>

    <br/>
    <router-link to="/users/$2a$10$F7KztiRnl.GJL9I8l4.WUeEdQDiPosNmg8vUqV.rYdihzLEVb.4Me/ec-site-settings">1. (user_id: 1){{ trans('clickHereFor') }} </router-link>
    <br />
    <router-link to="/users/$2a$10$P6CimhjlJPSmk0B5pERDGODxh1xyPsixVKqplcLl.EFNE6eZ.vGoi/ec-site-settings">2. (user_id: 2){{ trans('clickHereFor') }} </router-link>
    <br />
    <router-link to="/users/$2a$10$4OoyCDmEYVSBxTSOZ0DrWeWpUqHaKi4Bb3nlrBDQhixpwi793HAbW/ec-site-settings">3. (user_id: 1){{ trans('clickHereFor') }} </router-link>
    <br />
    <router-link to="/users/$2a$10$deEPiskgx7XOJE7e4sf5K.J20m6qWFnoKfo6H73r5aDoVliC5JNtG/ec-site-settings">4. (user_id: 2){{ trans('clickHereFor') }} </router-link>
    <br />
    <router-link to="/product-groups">{{ trans('5ProductGroups') }}</router-link>
    <br />
  </div>
</template>

<script lang="ts">
import {Component, Vue} from 'vue-property-decorator';

@Component({
  components: {},
})
export default class Home extends Vue {
}
</script>
