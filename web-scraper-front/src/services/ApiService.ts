import axios from 'axios';
import AppConfig from '../config';


export default class ApiService {


  /**
   * get all ec sites
   * @param userId the user id
   */
  public static getAllEcSites(userId: string): Promise<any> {
    return axios.get(`${AppConfig.baseApi}/users/${userId}/ec_sites`);
  }

  /**
   * get error msg from api response
   * @param err the err entity
   */
  public static getErrorString(err: any): string {
    const defaultError = 'Unknown error, please contact admin';
    if (err && err.response) {
      return (err.response.data && err.response.data.message) ? err.response.data.message : defaultError;
    } else {
      return 'Network error';
    }
  }

  /**
   * update ecsite
   * @param userId the user id
   * @param siteId the site id
   * @param entity the ecsite entity
   */
  public static updateECSite(userId: string, siteId: null, entity: any): Promise<any> {
    return axios.put(`${AppConfig.baseApi}/users/${userId}/ec_sites/${siteId}`, entity);
  }

  /**
   * get ecsite
   * @param userId the user id
   * @param siteId the site id
   */
  public static getECSite(userId: string, siteId: null): Promise<any> {
    return axios.get(`${AppConfig.baseApi}/users/${userId}/ec_sites/${siteId}`);
  }

  /**
   * login init
   * @param userId the user id
   * @param siteId the site id
   * @param uuid the unique login uuid
   */
  public static loginInit(userId: any, siteId: any, uuid: any): Promise<any> {
    return axios.get(`${AppConfig.baseApi}/users/${userId}/login_init`, {params: {siteId, uuid}});
  }

  /**
   * do login
   * @param userId the user id
   * @param entity the entity
   */
  public static login(userId: string, entity: any) {
    return axios.post(`${AppConfig.baseApi}/users/${userId}/login`, entity);
  }
}
