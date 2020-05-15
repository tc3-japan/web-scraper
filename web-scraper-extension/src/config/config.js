/**
 * ec sites
 */
export const EC_SITES = [
  {label: 'Amazon', value: 'amazon'},
  {label: 'Rakuten', value: 'rakuten'},
  {label: 'Yahoo', value: 'yahoo'}
]

/**
 * scraping types
 */
export const SCRAPING_TYPE = [
  {label: 'Purchase History', value: 'purchase_history'},
  {label: 'Product', value: 'product'},
]

/**
 * common json dropdown options
 */
export const JSON_DROPDOWN = [
  {label: 'Order Number', value: 'order_number'},
  {label: 'Order Date', value: 'order_date'},
  {label: 'Total Amount', value: 'total_amount'},
  {label: 'Delivery Status', value: 'delivery_status'},
  {label: 'Product Code', value: 'product_code'},
  {label: 'Product Name', value: 'product_name'},
  {label: 'Product Quantity', value: 'product_quantity'},
  {label: 'Unit Price', value: 'unit_price'},
  {label: 'Product Distributor', value: 'product_distributor'},
]

/**
 * default base api
 */
export const DEFAULT_API = 'https://scraper-stub-api.herokuapp.com/scrapers'

/**
 * i18next config
 * follow ../locales/en.json write new translation file and import here
 * https://react.i18next.com/getting-started
 */
export const I18_CONFIG = {
  resources: {
    en: require('../locales/en.json'),
    cn: require('../locales/cn.json'),
    jp: require('../locales/jp.json')
  },
  lng: "en",
  fallbackLng: "en",
  interpolation: {
    escapeValue: false
  }
}