<template>
  <div class="ec-site-setting-root">
    <div class="title">{{ trans('ecSiteSetting') }}</div>
    <div class="description">{{ trans('ecSiteDesc') }}</div>

    <div class="load-error" v-if="this.userLoadErrorMsg">{{this.userLoadErrorMsg}}</div>
    <div class="site-list" v-if="!this.userLoadErrorMsg && sites">
      <div class="load-error" v-if="sites.length === 0">{{ trans('noEcSitesFound') }}</div>

      <div class="site" v-for="site in sites">
        <div class="name row">
          <div class="left"></div>
          {{site['ecSite']}}
        </div>
        <div class="check-box row">
          <div class="left"></div>
          <input id="get-purchase-history" type="checkbox"
                 v-model="site['ecUseFlag']"
                 @change="ecUseFlagChange($event, site)"
          />
          <label for="get-purchase-history">{{ trans('getPurchaseHistory') }}</label>
        </div>

        <div class="status row">
          <div class="left">Status:</div>
          <div class="text">
            <div class="value">{{site['authStatus'] ? site['authStatus'] : trans('notLoggedIn')}}</div>
            <div class="reason" v-if="site['authFailReason']">{{site['authFailReason']}}</div>
          </div>
        </div>

        <div class="button row" v-if="isFailed(site)">
          <div class="left"></div>
          <button class="app-button"
                  @click="gotoLogin(site)">{{trans('login')}}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {Component, Vue} from 'vue-property-decorator';
import ApiService from '../services/ApiService';

@Component({
  components: {},
})
export default class ECSiteList extends Vue {
  public ecSiteSettings = {
    title: Vue.prototype.trans('title'),
    description: '',
  };
  public userLoadErrorMsg = null;
  public sites = null;

  /**
   * check is failed
   * @param site the site entity
   */
  public isFailed(site: any) {
    return !(site.authStatus && site.authStatus.toLowerCase().indexOf('success') >= 0);
  }

  /**
   * when checkbox changed
   * @param e the event
   * @param site the site id
   */
  public ecUseFlagChange(e: any, site: any) {
    const userId = this.$route.params.id;
    ApiService.updateECSite(userId, site.id, {ecUseFlag: e.target.checked}).then(() => {
      console.log('update successful');
    }).catch((err) => {
      console.error(err);
    });
  }

  /**
   * goto login page
   * @param site the site entity
   */
  public gotoLogin(site: any) {
    const userId = this.$route.params.id;
    this.$router.push({name: 'EC Site Login', params: {id: userId, siteId: site.id}});
  }

  public mounted() {
    ApiService.getAllEcSites(this.$route.params.id).then((rsp) => {
      console.log(rsp);
      this.sites = rsp.data;
    }).catch((err) => {
      this.userLoadErrorMsg = ApiService.getErrorString(err);
    });
  }
}
</script>


<style lang="scss">
  .ec-site-setting-root {
    .title {
      font-weight: bold;
      font-size: 24px;
    }
    .description {
      margin-top: 8px;
    }
    .load-error {
      margin-top: 16px;
      font-size: 24px;
      font-weight: bold;
      color: #c93114;
    }

    .site-list {
      margin-top: 36px;
      width: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      .site {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        .left {
          width: 80px;
        }
        .row {
          display: flex;
          flex-direction: row;
        }
        .name {
          font-weight: bold;
          font-size: 24px;
          margin-bottom: 8px;
        }

        .status {
          .value {
            font-weight: bold;
            font-size: 18px;
          }
          .text {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            font-size: 14px;
          }
          .reason {
            word-break: break-all;
            width: 300px;
          }
          margin-bottom: 18px;
        }
        .check-box {
          color: #1167bd;
          margin-bottom: 8px;
          label {
            margin-left: 6px;
          }
        }
      }
    }
  }
</style>
