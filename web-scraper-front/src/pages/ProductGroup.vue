<template>
  <div class="root">
    <div class="title">{{ trans('productGroupTitle') }}</div>

    <div class="filter">
      <div class="row keyword"><span class="label">{{ trans('keyword') }}:</span><input v-model="filter.searchKeyword"/></div>
      <div class="row confirm-status"><span class="label">{{ trans('confirmationStatus') }}:</span>
        {{ trans('confirmed') }}<input type="checkbox" v-model="filterConfirmed"/>
        {{ trans('unconfirmed') }}<input type="checkbox" v-model="filterUnconfirmed"/>

        <button @click="fetchData()">{{ trans('searchProduct') }}</button>
      </div>
    </div>
    <div class="paging">
      <span :class="`error ${message.type === 'message' ? 'message':''}`" v-if="message">{{message.content}}</span>
      <button :disabled="pageData.first" @click="onPageChanged(filter.pageNo-1)">{{'<<'}}</button>
      <button v-for="n in pageData.totalPages" @click="onPageChanged(n-1)" :disabled="n === pageData.number+1">
        {{`${n}`}}
      </button>
      <button :disabled="pageData.last" @click="onPageChanged(filter.pageNo+1)">{{'>>'}}</button>
      <span>{{`${pageData.number*pageData.size} - ${pageData.number*pageData.size + pageData.numberOfElements} / ${pageData.totalElements}`}}</span>
    </div>
    <div class="table">
      <div class="thead">
        <div class="row">
          <div class="cell">{{ trans('groupId') }}</div>
          <div class="cell">{{ trans('groupingMethod') }}</div>
          <div class="cell">{{ trans('modelNo') }}</div>
          <div class="cell">{{ trans('janCode') }}</div>
          <div class="cell">{{ trans('groupProductName') }}</div>
          <div class="cell">{{ trans('ecSite') }}</div>
          <div class="cell f3">{{ trans('productName') }}</div>
          <div class="cell">{{ trans('price') }}</div>
          <div class="cell">
            <button @click="onGroupButtonEvt()">{{ trans('groupButton') }}</button>
          </div>
          <div class="cell"></div>
          <div class="cell last">{{ trans('confirmationStatus') }}</div>
        </div>
      </div>
      <div class="tbody">
        <div class="row" v-for="item in tableData">
          <div class="cell">{{getValue(item,'groupId')}}</div>
          <div class="cell">{{getValue(item,'groupingMethod')}}</div>
          <div class="cell">
            <div class="model-no">
              <span>{{getValue(item,'modelNo')}}</span>
              <input type="text" v-if="item.groupId" v-model="(groups[item.groupId]||{}).modelNo"
                     v-on:blur="updateGroup(item,'modelNo')"
                     @keyup.enter="updateGroup(item,'modelNo')"/>
            </div>
          </div>
          <div class="cell">
            <div class="jan-code">
              <span>{{getValue(item,'janCode')}}</span>
              <input type="text" v-if="item.groupId" v-model="(groups[item.groupId]||{}).janCode"
                     v-on:blur="updateGroup(item,'janCode')"
                     @keyup.enter="updateGroup(item,'janCode')"/>
            </div>
          </div>
          <div class="cell">
            <div class="product-name">
              <span>{{getValue(item,'productName')}}</span>
              <input type="text" v-if="item.groupId" v-model="(groups[item.groupId]||{}).productName"
                     v-on:blur="updateGroup(item,'productName')"
                     @keyup.enter="updateGroup(item,'productName')"/>
            </div>
          </div>
          <div class="cell">{{item.ecSite}}</div>
          <div class="cell f3"><a :href="getLink(item)" target="_blank">{{getProductInfo(item)}}</a></div>
          <div class="cell">{{item.unitPrice ? item.unitPrice : ''}}</div>
          <div class="cell"><input type="checkbox" v-if="item.groupId || !item.productGroupId" v-model="item.checked"/>
          </div>
          <div class="cell">
            <button v-if="item.groupId" @click="onUngroup(item.groupId)">{{ trans('ungroupButton') }}</button>
          </div>
          <div class="cell last"><input type="checkbox" v-if="item.groupId"
                                        @change="updateGroup(item,'confirmationStatus')"
                                        v-model="(groups[item.groupId]||{}).confirmation"/></div>
        </div>
      </div>

      <div v-if="tableData.length === 0" class="no-content">No records found</div>
    </div>

    <div class="dialog-root" v-if="showSelectGroup">
      <div class="dialog">
        <div class="title">{{ trans('groupDialogMessage') }}</div>

        <div class="content">
          <div class="key-group">
            <div class="key-title">{{ trans('modelNo') }}:</div>
            <div class="group" v-for="key in Object.keys(groups)">
              <div v-if="groups[key].modelNo" >
                <input type="radio" name="group" :value="groups[key].modelNo" v-model="selectGroupModelNo"/>
                <span @click="selectGroupModelNo=groups[key].modelNo">{{groups[key].modelNo}}</span>
              </div>
            </div>
            <div class="group">
              <input type="radio" name="group" value="__new_group" v-model="selectGroupModelNo"/>
              <span><input @focus="selectGroupModelNo='__new_group'" v-model="createGroupNameModelNo"/></span>
            </div>
          </div>
          <div class="key-group">
            <div class="key-title">{{ trans('janCode') }}:</div>
            <div class="group" v-for="key in Object.keys(groups)">
              <div v-if="groups[key].janCode" >
                <input type="radio" name="group" :value="groups[key].janCode" v-model="selectGroupJanCode"/>
                <span @click="selectGroupJanCode=groups[key].janCode">{{groups[key].janCode}}</span>
              </div>
            </div>
            <div class="group">
              <input type="radio" name="group" value="__new_group" v-model="selectGroupJanCode"/>
              <span><input @focus="selectGroupJanCode='__new_group'" v-model="createGroupNameJanCode"/></span>
            </div>
          </div>
          <div class="key-group">
            <div class="key-title">{{ trans('groupProductName') }}:</div>
            <div class="group" v-for="key in Object.keys(groups)">
              <div v-if="groups[key].productName" >
                <input type="radio" name="group" :value="groups[key].productName" v-model="selectGroupProductName"/>
                <span @click="selectGroupProductName=groups[key].productName">{{groups[key].productName}}</span>
              </div>
            </div>
            <div class="group">
              <input type="radio" name="group" value="__new_group" v-model="selectGroupProductName"/>
              <span><input @focus="selectGroupProductName='__new_group'" v-model="createGroupNameProductName"/></span>
            </div>
          </div>
          <div class="buttons">
            <button @click="showSelectGroup=false">{{ trans('cancelButton') }}</button>
            <button @click="onGroup()"
                    :disabled="
                    (!selectGroupModelNo && !selectGroupJanCode && !selectGroupProductName)
                    || (
                       (selectGroupModelNo     === '__new_group' && createGroupNameModelNo.trim().length === 0)
                    && (selectGroupJanCode     === '__new_group' && createGroupNameJanCode.trim().length === 0)
                    && (selectGroupProductName === '__new_group' && createGroupNameProductName.trim().length === 0)
                    )
            ">
              {{ trans('groupButton') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {Component, Vue} from 'vue-property-decorator';
import ApiService from '../services/ApiService';
import AppConfig from '../config';

@Component({
  components: {},
})
export default class ProductGroup extends Vue {
  public pageData = { // page info from spring
    last: true,
    totalPages: 0,
    totalElements: 0,
    size: 0,
    number: 0,
    first: true,
    numberOfElements: 0,
    content: [],
  };

  public filter = {
    pageSize: 100,
    pageNo: 0,
    searchKeyword: '',
    confirmationStatus: 'unconfirmed',
  };

  public filterUnconfirmed = true;
  public filterConfirmed = false;

  public groups = {};
  public tableData = [];
  public message = null;

  public timer = null;

  public showSelectGroup = false;
  public selectGroupModelNo     = null;
  public selectGroupJanCode     = null;
  public selectGroupProductName = null;
  public createGroupNameModelNo     = '';
  public createGroupNameJanCode     = '';
  public createGroupNameProductName = '';

  /**
   * on page index changed
   */
  public onPageChanged(pageNo) {
    this.fetchProducts(pageNo);
  }

  /**
   * fetch products by page number
   */
  public fetchProducts(pageNo) {
    let confirmationStatus = null;
    if (this.filterUnconfirmed) {
      confirmationStatus = 'unconfirmed';
    }
    if (this.filterConfirmed) {
      confirmationStatus = 'confirmed';
    }

    if (this.filterConfirmed && this.filterUnconfirmed) {
      confirmationStatus = null;
    }
    ApiService.searchProducts(Object.assign({}, this.filter, {pageNo, confirmationStatus})).then((rsp) => {
      this.pageData = rsp.data;
      this.filter.pageNo = this.pageData.number;
      this.processData();
    });
  }

  /**
   * fetch groups then fetch products
   */
  public fetchData() {
    ApiService.getAllProductGroups().then((rsp) => {
      this.groups = {};
      rsp.data.forEach((group) => {
        this.groups[group.id] = group;
        this.groups[group.id].confirmation = group.confirmationStatus === 'confirmed';
      });
      this.fetchProducts(this.filter.pageNo);
    });
  }

  /**
   * get group values
   * @param item the row item
   * @param key the key
   */
  public getValue(item, key) {
    if (item.groupId) {
      return item[key];
    }
    if (item.productGroupId) {
      return '';
    }
    return 'N/A';
  }

  /**
   * show message
   * @param entity the message entity
   */
  public showMessage(entity) {
    this.message = entity;
    clearTimeout(this.timer);
    this.timer = setTimeout(() => {
      this.message = null;
    }, 10000);
  }

  /**
   * get product information
   * @param item the item object
   */
  public getProductInfo(item) {
    if (!item.id) {
      return '';
    }
    return `${item.productName}(${item.modelNo || 'N/A'})`;
  }

  /**
   * get link from product
   * @param item the product
   */
  public getLink(item) {
    if (item.ecSite && item.productCode) {
      return AppConfig.productLinkBase[item.ecSite.toLowerCase()] + item.productCode;
    }
    return '#';
  }

  /**
   * update group
   * @param item the group
   * @param key the value-key
   */
  public updateGroup(item, key) {
    if (key === 'modelNo') {
      if (this.getValue(item, key) === this.groups[item.groupId].modelNo) {
        return;
      }
    }
    if (key === 'janCode') {
      if (this.getValue(item, key) === this.groups[item.groupId].janCode) {
        return;
      }
    }
    if (key === 'productName') {
      if (this.getValue(item, key) === this.groups[item.groupId].productName) {
        return;
      }
    }
    this.groups[item.groupId].confirmationStatus = this.groups[item.groupId].confirmation ? 'confirmed' : 'unconfirmed';
    ApiService.updateGroup(item.groupId, this.groups[item.groupId]).then(() => {
      this.showMessage({type: 'message', content: 'Group update successful'});
      this.fetchData();
    }).catch((e) => {
      this.showMessage({type: 'error', content: ApiService.getErrorString(e)});
      // roll back
      if (key === 'modelNo') {
        this.groups[item.groupId].modelNo = this.getValue(item, key);
      }
      if (key === 'janCode') {
        this.groups[item.groupId].janCode = this.getValue(item, key);
      }
      if (key === 'productName') {
        this.groups[item.groupId].productName = this.getValue(item, key);
      }
    });
  }

  /**
   * combine group data into products
   */
  public processData() {
    const ungroupedProducts = [];
    const groupProducts = {};
    this.pageData.content.forEach((product) => {
      const groupId = product.productGroupId;
      if (!groupId) {
        ungroupedProducts.push(product);
      } else {
        groupProducts[groupId] = groupProducts[groupId] || [];
        groupProducts[groupId].push(product);
      }
    });

    let tableData = [];
    Object.keys(groupProducts).forEach((key) => {
      const group = this.groups[key];
      tableData.push({
        groupId:            parseInt(key, 10),
        modelNo:            group.modelNo,
        janCode:            group.janCode,
        productName:        group.productName,
        groupingMethod:     group.groupingMethod,
        confirmationStatus: group.confirmationStatus,
      });
      tableData = tableData.concat(groupProducts[key]);
    });
    tableData = tableData.concat(ungroupedProducts);
    this.tableData = tableData;
  }

  /**
   * delete group
   * @param id the group id
   */
  public onUngroup(id) {
    ApiService.ungroup(id).then().then(() => {
      this.showMessage({type: 'message', content: 'Group delete successful'});
      this.fetchData();
    }).catch((e) => {
      this.showMessage({type: 'error', content: ApiService.getErrorString(e)});
    });
  }

  /**
   * when click group button in table
   */
  public onGroupButtonEvt() {
    const {groupIds, productIds} = this.getSelectItems();
    if (groupIds.length <= 0 && productIds.length <= 0) {
      this.showMessage({type: 'error', content: 'You need select group or product first!'});
      return;
    }

    this.selectGroupModelNo     = null;
    this.selectGroupJanCode     = null;
    this.selectGroupProductName = null;
    this.createGroupNameModelNo     = '';
    this.createGroupNameJanCode     = '';
    this.createGroupNameProductName = '';
    this.showSelectGroup = true;
  }

  /**
   * when click group button on popup dialog
   */
  public onGroup() {
    const {groupIds, productIds} = this.getSelectItems();
    const modelNo     = this.selectGroupModelNo     === '__new_group' ? this.createGroupNameModelNo.trim()     : this.selectGroupModelNo;
    const janCode     = this.selectGroupJanCode     === '__new_group' ? this.createGroupNameJanCode.trim()     : this.selectGroupJanCode;
    const productName = this.selectGroupProductName === '__new_group' ? this.createGroupNameProductName.trim() : this.selectGroupProductName;

    ApiService.createOrUpdateGroup({groupIds, productIds, modelNo, janCode, productName}).then(() => {
      this.fetchData();
      this.showSelectGroup = false;
      this.showMessage({type: 'message', content: 'group successful'});
    }).catch((e) => {
      this.showMessage({type: 'error', content: ApiService.getErrorString(e)});
    });
  }

  public mounted() {
    this.fetchData();
  }

  /**
   * get checked product or groups
   */
  private getSelectItems() {
    const groupIds = [];
    const productIds = [];

    this.tableData.forEach((item) => {
      if (item.checked) {
        item.groupId ? groupIds.push(item.groupId) : productIds.push(item.id);
      }
    });
    return {groupIds, productIds};
  }
}
</script>


<style lang="scss">
  .dialog-root {
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
    background: rgba(0, 0, 0, 0.3);
    .dialog {
      background: white;
      width: 500px;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translateY(-50%) translateX(-50%);
      .title {
        margin-top: 16px;
        font-size: 16px;
        margin-bottom: 16px;
      }

      .content {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;

        .key-group {
          margin-bottom: 32px;
          .key-title {
            font-size: 16px;
            font-weight: bold;
            color: #59647a;
          }

          .group {
            display: flex;
            flex-direction: row;
            margin-top: 6px;
            > span {
              margin-left: 6px;
              display: flex;
              flex-direction: row;
              align-items: center;
              cursor: pointer;
              width: 180px;
            }
          }
        }

        .buttons {
          button {
            margin-right: 16px;
          }
          margin-top: 24px;
          margin-bottom: 16px;
        }
      }
    }
  }

  .root {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    .paging {
      margin-top: 24px;
      margin-bottom: 6px;
      width: 100%;
      display: flex;
      flex-direction: row;
      justify-content: flex-end;
      align-items: center;
      .error {
        flex: 1;
        color: red;
        display: flex;
        flex-direction: row;
        align-items: flex-start;
        font-weight: bold;
        &.message {
          color: black;
        }
      }
      button {
        outline: none;
        background: transparent;
        border: none;
        padding: 0 5px;
        font-size: 16px;
        cursor: pointer;
        margin-bottom: 2px;
        color: blue;

        &.page-index {

        }
        &:hover {
          opacity: 0.5;
        }
        &:active {
          opacity: 0.3;
        }
        &:disabled {
          color: black;
          &:hover {
            opacity: 1;
          }
          cursor: auto;
        }
      }
      span {
        margin-left: 16px;
      }
    }
    .table {
      width: 100%;
      .row {
        display: flex;
        flex-direction: row;

        .f1 {
          flex: 1;
        }
        .f2 {
          flex: 2;
        }
        .f3 {
          flex: 3;
        }
        .f05 {
          flex: 0.5;
        }
      }
      .cell {
        border: 1px solid #adb2b8;
        border-bottom: none;
        border-right: none;
        flex: 1;
        word-break: break-all;
        &.last {
          border-right: 1px solid #adb2b8;
        }
      }

      .no-content {
        border: 1px solid #adb2b8;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .tbody {
        &:last-child {
          border-bottom: 1px solid #adb2b8;
        }
        .row {
          .cell {
            min-height: 42px;
            display: flex;
            flex-direction: row;
            align-items: center;
            justify-content: center;
            .model-no {
              flex: 1;
              font-size: 14px;
              input[type="text"] {
                width: 88%;
                text-align: center;
              }
            }
            .jan-code {
              flex: 1;
              font-size: 14px;
              input[type="text"] {
                width: 88%;
                text-align: center;
              }
            }
            .product-name {
              flex: 1;
              font-size: 14px;
              input[type="text"] {
                width: 88%;
                text-align: center;
              }
            }

          }
        }
      }

      .thead {
        .row {
          background-color: #cfe2f3;
          height: 50px;
          .cell {
            display: flex;
            align-items: center;
            justify-content: center;
          }
        }
      }
    }
  }

  .title {
    font-weight: bold;
    font-size: 24px;
    margin-bottom: 64px;
  }

  .filter {
    width: 700px;
    .row {
      display: flex;
      flex-direction: row;
      align-items: center;
      margin-top: 8px;
      input[type="checkbox"] {
        margin-left: 6px;
        margin-right: 24px;
      }
      button {
        margin-left: 36px;
      }
    }
    .label {
      margin-right: 12px;
      display: flex;
      flex-direction: row;
      justify-content: flex-end;
      width: 180px;
    }
  }
</style>
