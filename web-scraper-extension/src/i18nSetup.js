import i18n from "i18next";
import {initReactI18next} from "react-i18next";
import {I18_CONFIG} from "./config/config";
import {logInfo} from "./services/utils";

/**
 * setup i18n
 */
i18n
  .use(initReactI18next)
  .init(I18_CONFIG, () => {
    logInfo('I18_CONFIG loaded')
  });


export function getI18T() {
  return i18n.t.bind(i18n)
}