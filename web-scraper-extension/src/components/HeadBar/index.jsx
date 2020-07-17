import React from 'react';
import ReactDropdown from 'react-dropdown';
import PT from 'prop-types';

import Button from '../Button';
import './styles.scss';

import { EC_SITES, SCRAPING_TYPE } from '../../config/dropdown-list';
import getI18T from '../../i18nSetup';

/**
 * head bar, buttons components
 */
export default function HeadBar({
  onLoad,
  onSave,
  onChange,
  onLog,
  onSetting,
  loadType,
  onTest,
  site,
  type,
  siteObj,
}) {
  const t = getI18T();
  return (
    <div className="header-container">
      <Button title={t('header.load')} onClick={onLoad} disabled={!site || !type || loadType === 'loading'} />
      <Button title={t('header.save')} onClick={onSave} disabled={!siteObj} />
      <Button title={t('header.test')} disabled={!siteObj} onClick={onTest} />

      <div className="seq" />
      <div className="selector-container">
        <span>{t('header.ecSite')}</span>
        <ReactDropdown options={EC_SITES} onChange={(v) => onChange('site', v)} value={site} placeholder="" />
      </div>
      <div className="seq" />
      <div className="selector-container">
        <span>{t('header.scrapingType')}</span>
        <ReactDropdown options={SCRAPING_TYPE} onChange={(v) => onChange('type', v)} value={type} placeholder="" />
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
  onChange: PT.func,
  site: PT.string,
  siteObj: PT.shape({}),
  type: PT.string,
};

HeadBar.defaultProps = {
  loadType: undefined,
  onLoad: undefined,
  onLog: undefined,
  onSave: undefined,
  onTest: undefined,
  onSetting: undefined,
  onChange: undefined,
  site: undefined,
  siteObj: undefined,
  type: undefined,
};
