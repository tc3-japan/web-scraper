import {getI18T} from "../i18nSetup";

const t = getI18T();

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
  {label: t('header.purchaseHistory'), value: 'purchase_history'},
  {label: t('header.productDetail'), value: 'product'},
]

/**
 * common json dropdown options
 */
export const JSON_DROPDOWN = [
  {label: t('editor.items.orderNumber'), value: 'order_number'},
  {label: t('editor.items.orderDate'), value: 'order_date'},
  {label: t('editor.items.totalAmount'), value: 'total_amount'},
  {label: t('editor.items.deliveryStatus'), value: 'delivery_status'},
  {label: t('editor.items.productCode'), value: 'product_code'},
  {label: t('editor.items.productName'), value: 'product_name'},
  {label: t('editor.items.productQuantity'), value: 'product_quantity'},
  {label: t('editor.items.unitPrice'), value: 'unit_price'},
  {label: t('editor.items.productDistributor'), value: 'product_distributor'},
]
