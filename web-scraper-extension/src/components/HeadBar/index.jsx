import React from 'react';
import ReactDropdown from 'react-dropdown';
import PT from 'prop-types';
import { useGlobalState } from '@dr.pogodin/react-global-state';

import Button from '../Button';
import './styles.scss';

import { EC_SITES, SCRAPING_TYPE } from '../../config/dropdown-list';
import getI18T from '../../i18nSetup';

/**
 * head bar, buttons components
 */
export default function HeadBar({
  loadType,
  onLoad,
  onLog,
  onSave,
  onSetting,
  onTest,
  setSite,
  setType,
  site,
  type,
}) {
  const [dataType] = useGlobalState('data.dataType');
  const t = getI18T();
  return (
    <div className="header-container">
      <Button title={t('header.load')} onClick={onLoad} disabled={!site || !type || loadType === 'loading'} />
      <Button
        disabled={dataType !== type.value}
        onClick={onSave}
        title={t('header.save')}
      />
      <Button
        disabled={dataType !== type.value}
        onClick={onTest}
        title={t('header.test')}
      />

      <div className="seq" />
      <div className="selector-container">
        <span>{t('header.ecSite')}</span>
        <ReactDropdown
          options={EC_SITES}
          onChange={setSite}
          value={site}
          placeholder=""
        />
      </div>
      <div className="seq" />
      <div className="selector-container">
        <span>{t('header.scrapingType')}</span>
        <ReactDropdown
          options={SCRAPING_TYPE}
          onChange={setType}
          value={type}
          placeholder=""
        />
      </div>
      <div className="seq" />
      <Button title={t('header.setting')} onClick={onSetting} />
      <Button title={t('header.log')} onClick={onLog} />
    </div>
  );
}

HeadBar.propTypes = {
  loadType: PT.string,
  onLoad: PT.func,
  onLog: PT.func,
  onSave: PT.func,
  onTest: PT.func,
  onSetting: PT.func,
  setSite: PT.func.isRequired,
  setType: PT.func.isRequired,
  site: PT.string,
  type: PT.string,
};

HeadBar.defaultProps = {
  loadType: undefined,
  onLoad: undefined,
  onLog: undefined,
  onSave: undefined,
  onTest: undefined,
  onSetting: undefined,
  site: undefined,
  type: undefined,
};
