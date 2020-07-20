import React from 'react';
import PT from 'prop-types';

import './styles.scss';
import Button from '../Button';
import {
  logInfo, processError, storageGet, storageSet,
} from '../../services/utils';
import { DEFAULT_API } from '../../config/config';
import getI18T from '../../i18nSetup';

export const BASE_API_KEY = 'BASE_API';

/**
 * setting panel
 */
export default class Setting extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.state = {
      api: '',
    };
  }

  componentDidMount() {
    storageGet(BASE_API_KEY).then((api) => {
      console.log(api)
      logInfo(`read from api from local ${api}`);
      this.setState({ api: api || DEFAULT_API });
    }).catch((e) => processError(e));
  }

  onSave() {
    const { api } = this.state;
    const { onBack } = this.props;
    storageSet(BASE_API_KEY, api).then(() => {
      logInfo('api saved');
      onBack();
    }).catch((e) => processError(e));
  }

  render() {
    const { api } = this.state;
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
          <input value={api} onChange={(e) => this.setState({ api: e.target.value })} />
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
