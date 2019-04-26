<template>
  <div class="login-site">
    <div class="title" v-if="siteLoaded">{{site['ecSite'] + trans('login')}}</div>
    <div class="load-error" v-if="this.userLoadErrorMsg">{{this.userLoadErrorMsg}}</div>
    <div v-if="!siteLoaded">{{trans('loginInitializing')}}</div>

    <div class="user-login" v-if="siteLoaded && !userLoadErrorMsg">
      <div class="row">
        <div class="label">Email(User Id):</div>
        <input v-model="email"/>
      </div>
      <div class="row">
        <div class="label">Password:</div>
        <input v-model="password" type="password"/>
      </div>

      <div class="row image-tip" v-if="codeType">{{this.codeMessageMap[this.codeType]}}</div>
      <div class="row" v-if="codeType">
        <div class="label">code</div>
        <input v-model="code"/>
      </div>
      <div class="row" v-if="codeType === 'CAPTCHA'">
        <img :src="this.captcha">
      </div>

      <div class="login-error" v-if="loginError">{{loginError}}</div>

      <div class="row buttons">
        <button class="app-button"
                :disabled="isInvalid() || isDoingLogin"
                @click="login()">{{trans('login')}}
        </button>
      </div>
    </div>
  </div>
</template>


<script lang="ts">
import {Component, Vue} from 'vue-property-decorator';
import ApiService from '../services/ApiService';
import EcSite from '../models/EcSite';

@Component({
  components: {},
})
export default class ECSiteLogin extends Vue {
  public userId = null;
  public siteId = null;

  public siteLoaded = false;
  public site = {};
  public userLoadErrorMsg = null;

  public email = '';
  public password = '';
  public code = '';
  public uuid = '';

  public isDoingLogin = false;

  public codeType = null;
  public captcha = null;

  public step = '';
  public loginError = null;

  public codeMessageMap = {
    MFA: Vue.prototype.trans('mfaPlease'),
    Verification: Vue.prototype.trans('verifyPlease'),
    CAPTCHA: Vue.prototype.trans('capchaPlease'),
  };

  /**
   * invoked by when mounted
   */
  public mounted() {
    this.userId = this.$route.params.id;
    this.siteId = this.$route.params.siteId;
    this.uuid = Math.random() + '-' + Date.now();

    ApiService.getECSite(this.userId, this.siteId).then((rsp) => {
      this.site = rsp.data;
      this.loginInit();
    }).catch((err) => {
      this.siteLoaded = true;
      this.userLoadErrorMsg = ApiService.getErrorString(err);
    });
  }

  /**
   * login init
   */
  public loginInit() {
    ApiService.loginInit(this.userId, this.siteId, this.uuid).then((rsp) => {
      console.log(rsp.data);
      this.step = rsp.data.authStep;
      this.email = rsp.data.emailId;
      this.codeType = rsp.data.codeType;
      this.captcha = rsp.data.image;
      if (rsp.data.reason) {
        this.loginError = rsp.data.reason;
      }
    }).catch((err) => {
      this.userLoadErrorMsg = ApiService.getErrorString(err);
    }).finally(() => {
      this.siteLoaded = true;
    });
  }

  /**
   * check email address
   * @param email
   */
  public validateEmail(email) {
    // tslint:disable-next-line
    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
  }

  /**
   * is input invalid
   */
  public isInvalid() {
    // ignore email and password in first step
    if (this.step !== 'FIRST' && this.password.trim().length <= 0) {
      return true;
    }
    if (this.step !== 'FIRST' && this.email.trim().length <= 0) {
      return true;
    }
    if (this.step !== 'FIRST'
        && this.site[String('ecSite')] !== EcSite.kojima
        && !this.validateEmail(this.email)) {
      return true;
    }
    return this.codeType && this.code.trim().length <= 0;
  }

  /**
   * when login clicked
   */
  public login() {
    const loginBody = {
      email: this.email,
      password: this.password,
      code: this.code.trim().length <= 0 ? null : this.code.trim(),
      uuid: this.uuid,
      siteId: this.siteId,
    };

    this.isDoingLogin = true;
    this.loginError = null;
    ApiService.login(this.userId, loginBody).then((rsp) => {
      this.step = rsp.data.authStep;
      this.email = rsp.data.emailId;
      this.codeType = rsp.data.codeType;
      this.captcha = 'data:image/png;base64, ' + rsp.data.image;
      if (rsp.data.reason) {
        this.loginError = rsp.data.reason;
      }

      if (this.step === 'DONE') { // login success
        this.$router.push({name: 'EC Site Settings', params: {id: this.userId}});
      }
    }).catch((err) => {
      this.loginError = ApiService.getErrorString(err);
    }).finally(() => {
      this.isDoingLogin = false;
    });
  }

}
</script>


<style lang="scss">
  .login-site {
    .title {
      font-weight: bold;
      font-size: 24px;
      margin-bottom: 16px;
    }
    .load-error {
      margin-top: 16px;
      font-size: 24px;
      font-weight: bold;
      color: #c93114;
    }
    .login-error {
      color: #c93114;
    }

    .user-login {
      margin-top: 24px;
      width: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      .row {
        margin-bottom: 8px;
        display: flex;
        flex-direction: row;

        &.image-tip {
          margin-top: 16px;
          font-size: 12px;
        }
        &.buttons {
          margin-top: 16px;
        }
        .label {
          font-weight: bold;
          display: flex;
          flex-direction: row;
          align-items: flex-start;
          font-size: 14px;
          width: 120px;
        }
        img {
          margin-top: 8px;
        }
      }
    }
  }
</style>
