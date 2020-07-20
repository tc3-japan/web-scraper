import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import { I18_CONFIG } from './config/config';

/**
 * setup i18n
 */
i18n
  .use(initReactI18next)
  .init(I18_CONFIG, () => {
    console.log('I18_CONFIG loaded');
  });

export default function getI18T() {
  return i18n.t.bind(i18n);
}
