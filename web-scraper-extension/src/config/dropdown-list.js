import getI18T from '../i18nSetup';

const t = getI18T();

/**
 * ec sites
 */
export const EC_SITES = [
  { label: 'Amazon', value: 'amazon' },
  { label: 'Rakuten', value: 'rakuten' },
  { label: 'Yahoo', value: 'yahoo' },
];

/**
 * Valid scraping type values.
 */
export const VALID_SCRAPING_TYPES = {
  PURCHASE_HISTORY: 'purchase_history',
  PRODUCT_DETAIL: 'product',
};

/**
 * scraping types
 */
export const SCRAPING_TYPE = [
  {
    label: t('header.purchaseHistory'),
    value: VALID_SCRAPING_TYPES.PURCHASE_HISTORY,
  },
  {
    label: t('header.productDetail'),
    value: VALID_SCRAPING_TYPES.PRODUCT_DETAIL,
  },
];

/**
 * common json dropdown options
 */
export const JSON_DROPDOWN = [
  { label: t('editor.items.orderNumber'), value: 'order_number' },
  { label: t('editor.items.orderDate'), value: 'order_date' },
  { label: t('editor.items.totalAmount'), value: 'total_amount' },
  { label: t('editor.items.deliveryStatus'), value: 'delivery_status' },
  { label: t('editor.items.productCode'), value: 'product_code' },
  { label: t('editor.items.productName'), value: 'product_name' },
  { label: t('editor.items.productQuantity'), value: 'product_quantity' },
  { label: t('editor.items.unitPrice'), value: 'unit_price' },
  { label: t('editor.items.productDistributor'), value: 'product_distributor' },
];

/**
 * Configuration of the dropdown at product details page.
 */
export const PRODUCT_DETAILS_DROPDOWN = [
  { label: t('productDetails.productCode'), value: 'product_code' },
  { label: t('productDetails.productName'), value: 'product_name' },
  { label: t('productDetails.unitPrice'), value: 'unit_price' },
  { label: t('productDetails.modelNo'), value: 'model_no' },
  { label: t('productDetails.modelNoLabel'), value: 'model_no_label' },
  { label: t('productDetails.janCode'), value: 'jan_code' },
  {
    label: t('productDetails.productDistributer'),
    value: 'product_distributer',
  },
];
