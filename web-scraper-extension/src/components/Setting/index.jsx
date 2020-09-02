import React from 'react';
import PT from 'prop-types';

import './styles.scss';
import Button from '../Button';
import {
  logInfo, processError, storageGet, storageSet,
} from '../../services/utils';
import { DEFAULT_API, DEFAULT_TEST } from '../../config/config';
import getI18T from '../../i18nSetup';

export const BASE_API_KEY = 'BASE_API';
export const BASE_TEST_KEY = 'BASE_TEST';

/**
 * setting panel
 */
export default class Setting extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.state = {
      api: '',
      test: 0,
    };
  }

  componentDidMount() {
    try {
      storageGet(BASE_API_KEY).then((api) => {
        logInfo(`read from api from local ${api}`);
        this.setState({ api: api || DEFAULT_API });
      });
      storageGet(BASE_TEST_KEY).then((test) => {
        logInfo(`read from test from local ${test}`);
        this.setState({ test: test || DEFAULT_TEST });
      });
    } catch(e) {
      processError(e);
    }
  }

  onSave() {
    const { api, test } = this.state;
    const { onBack } = this.props;
    try {
      storageSet(BASE_API_KEY, api).then(() => {
        logInfo('api saved');
       });
      storageSet(BASE_TEST_KEY, test).then(() => {
        logInfo('test saved');
      });
      onBack();
    } catch(e) {
      processError(e);
    }
  }

  render() {
    const { api, test } = this.state;
    const { onBack } = this.props;
    const t = getI18T();
    return (
      <div className="setting-container">
        <div className="top-bar">
          <div className="title">{t('setting.title')}</div>
          <Button title={t('setting.back')} onClick={onBack} />
        </div>

        <div className="input-container">
          <span>{t('setting.baseUrl')}</span>
          <div className="seq" />
          <input value={api} onChange={(e) => this.setState({ api: e.target.value })} size="100"/>
        </div>
        <div className="input-container">
          <span>{t('setting.baseTest')}</span>
          <div className="seq" />
          <input value={test} onChange={(e) => this.setState({ test: e.target.value })} size="20"/>
          <div className="seq" />
          <span>{t('setting.baseTestDesc')}</span>
        </div>

        <Button title={t('setting.update')} onClick={() => this.onSave()} />
      </div>
    );
  }
}

Setting.propTypes = {
  onBack: PT.func,
};

Setting.defaultProps = {
  onBack: undefined,
};
