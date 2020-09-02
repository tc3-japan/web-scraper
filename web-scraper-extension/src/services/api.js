/* global fetch, Request */

import * as NProgress from 'nprogress';
import { logInfo, storageGet } from './utils';
import { DEFAULT_API, DEFAULT_TEST } from '../config/config';
import { BASE_API_KEY, BASE_TEST_KEY } from '../components/Setting';

/**
 * get base url
 */
async function getBaseUrl() {
  try {
    return await storageGet(BASE_API_KEY) || DEFAULT_API;
  } catch (e) {
    logInfo(JSON.stringify(e));
    return Promise.resolve(DEFAULT_API);
  }
}

async function getTestCount() {
  try {
    return await storageGet(BASE_TEST_KEY) || DEFAULT_TEST;
  } catch (e) {
    logInfo(JSON.stringify(e));
    return Promise.resolve(DEFAULT_TEST);
  }
}

/**
 * common http request
 * @param request the request
 */
async function fetchRequest(request) {
  NProgress.start();
  try {
    const response = await fetch(request);
    NProgress.done();
    if (response.status === 200) {
      const text = await response.text();
      try {
        return JSON.parse(text);
      } catch (e) {
        return text;
      }
    }
    return Promise.reject(Error(`JSON load failed with status code ${response.status}`));
  } catch (error) {
    NProgress.done();
    return Promise.reject(Error(`JSON load exception ${error}`));
  }
}

/**
 * the Api class
 */
class Api {
  /**
   * load json
   * @param site the site name
   * @param type the scraping type
   */
  static async load(site, type) {
    return fetchRequest(new Request(`${await getBaseUrl()}/${site}/${type}`));
  }

  /**
   * update json
   * @param site the site name
   * @param type the scraping type
   * @param body the request body
   */
  static async save(site, type, body) {
    return fetchRequest(new Request(`${await getBaseUrl()}/${site}/${type}`, {
      method: 'PUT',
      body: JSON.stringify(body),
    }));
  }

  /**
   * test json
   * @param site the site name
   * @param type the scraping type
   * @param body  the test body
   */
  static async test(site, type, body) {
    return fetchRequest(new Request(`${await getBaseUrl()}/${site}/${type}/${await getTestCount()}/test`, {
      method: 'POST',
      body: JSON.stringify(body),
    }));
  }
}

export default Api;
