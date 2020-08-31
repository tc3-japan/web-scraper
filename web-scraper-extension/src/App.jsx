/* global window */

import React, {
  useEffect,
  useState,
} from 'react';
import { v4 as uuid } from 'uuid';
import {
  getGlobalState,
  useGlobalState,
} from '@dr.pogodin/react-global-state';

import './App.scss';
import _ from 'lodash';
import Swal from 'sweetalert2';
import HeadBar from './components/HeadBar';

import ProductDetailsEditor from './components/ProductDetailsEditor';
import PurchaseHistoryEditor from './components/PurchaseHistoryEditor';
import SearchProductEditor from './components/SearchProductEditor';

import {
  convertToBackend,
  convertToFrontend,
  logInfo,
  processError,
  storageGet,
  sendMessageToPage,
} from './services/utils';
import {
  EC_SITES,
  SCRAPING_TYPE,
  VALID_SCRAPING_TYPES,
} from './config/dropdown-list';
import Setting, { BASE_API_KEY } from './components/Setting';
import 'sweetalert2/src/sweetalert2.scss';
import 'nprogress/nprogress.css';
import Api from './services/api';
import getI18T from './i18nSetup';
import Button from './components/Button';
import { DEFAULT_API } from './config/config';

export default function App() {
  const [siteObj, setSiteObj] = useGlobalState('data', null);

  // This UUID alters each time the data are reloaded. It is intended to reset
  // to zero state various components managed by local states.
  const [, setDataUuid] = useGlobalState('dataUuid');

  const [site, setSite] = useState(EC_SITES[0]);
  const [type, setType] = useState(SCRAPING_TYPE[0]);
  const [logTxt, setLogTxt] = useState([]);
  const [log, setLog] = useState(false);
  const [setting, setSetting] = useState(false);
  const [loadType, setLoadType] = useState('pending');

  const [i18n] = useGlobalState('i18n', getI18T);

  // The base code allows only a single component to listen the messages from
  // the webpage. This is a quick workaround for the new editor.
  useEffect(() => {
    window.messageListeners = [];
  }, []);

  window.log = (text, dontTimestamp) => {
    let msg = text;
    if (!dontTimestamp) msg = `[${new Date().toISOString()}]: ${msg}`;
    if (_.isFunction(msg)) msg.uuid = uuid();
    setLogTxt([msg].concat(logTxt));
  };

  useEffect(() => {
    window.log('extension load succeed');
    return () => {
      window.log = _.noop;
    };
  }, []);

  const globalState = getGlobalState();

  /**
   * update json value
   * @param path the path
   * @param value the value
   */
  const onUpdate = (path, value) => {
    const newData = _.cloneDeep(globalState.state.data);

    if (!value) {
      const m = path.match(/(.*\.rows)\.(\d*)$/);
      if (m) {
        // The request is to remove i-th row from the array at the path `p`
        // of data object. A special treatment is needed for this.
        const p = m[1];
        const i = Number(m[2]);
        _.get(newData, p).splice(i, 1);
        setSiteObj(newData);
        return;
      }
    }

    _.set(newData, path, value);
    setSiteObj(newData);
  };

  /**
   * load site
   */
  const loadSite = async (siteToLoad, typeToLoad) => {
    if (siteObj && siteObj.dataType) {
      const result = await Swal.fire({
        title: i18n('loadDialogTitle'),
        text: i18n('loadDialogContent'),
        showCancelButton: true,
        showConfirmButton: true,
        confirmButtonText: i18n('dialogBtnYes'),
        cancelButtonText: i18n('dialogBtnNo'),
      });
      if (result.dismiss) {
        return;
      }
    }
    setLoadType('loading');
    setSite(siteToLoad);
    setType(typeToLoad);
    try {
      let data = await Api.load(siteToLoad.value, typeToLoad.value);
      data = convertToFrontend(data, typeToLoad.value);
      setDataUuid(uuid());
      setSiteObj(data);
      setLoadType('loaded');
      logInfo('json loaded.');
    } catch (e) {
      setSiteObj(null);
      setLoadType('loaded');
      processError(e);
    }
  };

  /**
   * test site
   */
  const testSite = async () => {
    const result = await Swal.fire({
      title: i18n('testDialogTitle'),
      showCancelButton: true,
      showConfirmButton: true,
      confirmButtonText: i18n('dialogBtnYes'),
      cancelButtonText: i18n('dialogBtnNo'),
    });
    if (result.dismiss) {
      return;
    }

    try {
      const rsp = await Api.test(
        site.value,
        type.value,
        convertToBackend(siteObj, type.value),
      );
      const baseUrl = await storageGet(BASE_API_KEY) || DEFAULT_API;
      logInfo('test succeed');
      rsp[1].urls.reverse().forEach((url) => {
        const enocoded = encodeURI(url);
        logInfo(() => (
          <a
            className="urlInLog"
            href={`${baseUrl}/${url}`}
            onClick={(e) => {
              e.stopPropagation();
              e.preventDefault();
              sendMessageToPage({
                action: 'openUrl',
                url: `${baseUrl}/${enocoded}`,
              });
            }}
          >
            {url}
          </a>
        ), true);
      });
      logInfo(rsp[0]);
    } catch (e) {
      processError(e);
    }
  };

  /**
   * save site json
   * @return {Promise<void>}
   */
  const saveSite = async () => {
    const result = await Swal.fire({
      title: i18n('saveDialogTitle'),
      showCancelButton: true,
      showConfirmButton: true,
      confirmButtonText: i18n('dialogBtnYes'),
      cancelButtonText: i18n('dialogBtnNo'),
    });
    if (result.dismiss) {
      return;
    }

    try {
      const body = convertToBackend(siteObj, type.value);
      logInfo(JSON.stringify(body));
      await Api.save(site.value, type.value, body);
      logInfo('json saved');
    } catch (e) {
      processError(e);
    }
  };

  if (setting) {
    return (
      <div className="app">
        <Setting onBack={() => setSetting(false)} />
      </div>
    );
  }

  // This selects the appropriate editor for the loaded type of data.
  let content;
  switch (type.value) {
    case VALID_SCRAPING_TYPES.PURCHASE_HISTORY:
      content = (
        <PurchaseHistoryEditor
          siteObj={siteObj}
          onUpdate={onUpdate}
        />
      );
      break;
    case VALID_SCRAPING_TYPES.PRODUCT_DETAIL:
      content = <ProductDetailsEditor />;
      break;
    case VALID_SCRAPING_TYPES.SEARCH_PRODUCT:
      content = <SearchProductEditor />;
      break;
    default:
  }

  // A little tip on our current state.
  let tip;
  switch (loadType) {
    case 'pending': tip = 'loadJsonTip'; break;
    case 'loading': tip = 'loadingJson'; break;
    case 'loaded': tip = siteObj ? null : 'loadJsonFailed'; break;
    default:
  }
  if (siteObj && siteObj.dataType !== type.value) tip = 'loadJsonTip';
  if (tip) tip = <div className="tip">{i18n(tip)}</div>;

  return (
    <div className="app">
      <HeadBar
        loadType={loadType}
        onLoad={loadSite}
        onLog={() => setLog(!log)}
        onSave={saveSite}
        onSetting={() => setSetting(true)}
        onTest={testSite}
        site={site}
        type={type}
      />
      { tip }
      { content }
      {log && (
      <div className="log-container">
        <div className="log-container">
          {
            logTxt.map((Msg) => {
              if (_.isFunction(Msg)) {
                return <Msg key={Msg.uuid} />;
              }
              return <pre key={Msg.slice(0, 64)}>{Msg}</pre>;
            })
          }
        </div>
        <Button
          className="closeLogButton"
          title="Close Log"
          onClick={() => setLog(!log)}
        />
      </div>
      )}
    </div>
  );
}
