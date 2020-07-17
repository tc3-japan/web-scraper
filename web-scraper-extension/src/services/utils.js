import _ from 'lodash';
import { VALID_SCRAPING_TYPES } from '../config/dropdown-list';
import {getI18T} from "../i18nSetup";

const selector = require('./selector-helper')
let chrome = window.chrome;
let browser = window.browser;

/**
 * Convert purchase history JSON to UI data structure.
 * @param site the site
 * @return {{meta}|*}
 */
function convertPurchaseHistoryToFrontend(site) {
  if (site.meta) {
    return site;
  }
  const processRows = (obj) => {
    const keys = _.keys(_.omit(obj, ['url_element', 'parent', 'purchase_product']))
    obj.rows = []
    _.each(keys, (k) => {
      const v = obj[k]
      v.type = k
      delete obj[k]
      obj.rows.push(v)
    })
  }

  processRows(site['purchase_order'])
  processRows(site['purchase_order']['purchase_product'])
  return Object.assign({}, site, {
    meta: {
      expanded: {
        history: true,
        order: true,
        product: true,
        next: true,
      },
      advancedExpanded: {},
      highlight: '',
    }
  })
}

/**
 * Converts JSON object from API to UI format.
 * @param {object} data
 * @param {string} type Data type (see VALID_SCRAPING_TYPES).
 * @return {object}
 */
export function convertToFrontend(data, type) {
  switch (type) {
    case VALID_SCRAPING_TYPES.PURCHASE_HISTORY:
      return convertPurchaseHistoryToFrontend(data);
  }
}

/**
 * convert to backend struct
 * @param site the site
 * @return {*}
 */
export const convertToBackend = (site) => {
  const request = _.cloneDeep(site)
  delete request.meta

  const processRows = (arr, path) => {
    _.each(arr, row => {
      if (_.isNil(row.type) || _.isEmpty(row.type)) {
        return;
      }
      const type = row.type
      delete row.type
      _.set(request, `${path}.${type}`, row)
    })
  }

  processRows(request['purchase_order'].rows, 'purchase_order')
  processRows(request['purchase_order']['purchase_product'].rows, 'purchase_order.purchase_product')

  delete request['purchase_order'].rows;
  delete request['purchase_order']['purchase_product'].rows;
  return request
}


function getNative() {
  if (!chrome && !browser) {
    return {
      runtime: {lastError: null, onMessage: {addListener: () => null}},
      tabs: {query: () => null},
      storage: {
        local: {
          get: (key, cb) => cb({[key]: window.localStorage.getItem(key)}),
          set: (obj, cb) => {
            const k = _.keys(obj)[0]
            window.localStorage.setItem(k, obj[k]);
            cb();
          }
        }
      }
    }
  }
  return chrome || browser;
}

if (getNative().runtime.onMessage) {
  getNative().runtime.onMessage.addListener(function (request, sender, sendResponse) {
    if (window.onMessage) {
      window.onMessage(request)
    } else {
      logInfo('uncached message from page')
    }
  });
}

/**
 *
 * @param key
 */
export function storageGet(key) {
  return new Promise((resolve, reject) => {
    getNative().storage.local.get([key], result => {
      // runtime.lastError will be defined during an API method callback if there was an error
      const error = getNative().runtime.lastError;
      if (error) {
        reject(error);
      } else {
        resolve(result[key]);
      }
    });
  });
}

/**
 * set key
 * @param key
 * @param value
 */
export function storageSet(key, value) {
  return new Promise((resolve, reject) => {
    getNative().storage.local.set({[key]: value}, () => {
      // runtime.lastError will be defined during an API method callback if there was an error
      const error = getNative().runtime.lastError;
      if (error) {
        reject(error);
      } else {
        resolve();
      }
    });
  });
}

/**
 * process error
 * @param e the error
 */
export function processError(e) {
  if (window.log) {
    window.log(JSON.stringify(e))
  }
  console.error(e)
}

/**
 * log info
 * @param msg
 */
export function logInfo(msg) {
  if (window.log) {
    window.log(JSON.stringify(msg))
  }
  console.log(msg)
}

/**
 * send message to page
 * @param args the args
 */
export function sendMessageToPage(args) {
  args.messageId = Date.now() + Math.random();
  getNative().tabs.query({active: true, currentWindow: true}, (tabs) => {
    logInfo('send message = ' + JSON.stringify(args));
    getNative().tabs.sendMessage(tabs[0].id, args);
  });
}


/**
 * selector methods
 */
export const getCommonParent = (p1, p2) => selector.getCommonParent(p1, p2, getI18T)
export const getPathParent = selector.getPathParent
export const removeParent = selector.removeParent
export const getCommonClass = selector.getCommonClass
export const removeDifferentAndAdditional = selector.removeDifferentAndAdditional