<template>
  <div class="home">
    EC Site Home page

    <br/>
    <router-link to="/users/$2a$10$F7KztiRnl.GJL9I8l4.WUeEdQDiPosNmg8vUqV.rYdihzLEVb.4Me/ec-site-settings">Click here to
      1. EC site settings (user_id: 1)
    </router-link>
    <br />
    <router-link to="/users/$2a$10$P6CimhjlJPSmk0B5pERDGODxh1xyPsixVKqplcLl.EFNE6eZ.vGoi/ec-site-settings">Click here to
      2. EC site settings (user_id: 2)
    </router-link>
    <br />
    <router-link to="/users/$2a$10$4OoyCDmEYVSBxTSOZ0DrWeWpUqHaKi4Bb3nlrBDQhixpwi793HAbW/ec-site-settings">Click here to
      3. EC site settings (user_id: 1)
    </router-link>
    <br />
    <router-link to="/users/$2a$10$deEPiskgx7XOJE7e4sf5K.J20m6qWFnoKfo6H73r5aDoVliC5JNtG/ec-site-settings">Click here to
      4. EC site settings (user_id: 2)
    </router-link>
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
