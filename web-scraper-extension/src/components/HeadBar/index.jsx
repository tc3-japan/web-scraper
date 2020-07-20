import React from 'react';
import ReactDropdown from 'react-dropdown';
import PT from 'prop-types';
import { useGlobalState } from '@dr.pogodin/react-global-state';

import Button from '../Button';
import Modal from '../Modal';

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

  const [showLoadModal, setShowLoadModal] = React.useState(false);
  const [siteToLoad, setSiteToLoad] = React.useState(site);
  const [typeToLoad, setTypeToLoad] = React.useState(type);

  return (
    <div
      className="header-container"
    >
      {
        showLoadModal ? (
          <Modal onCancel={() => setShowLoadModal(false)}>
            <div className="ModalInputRow">
              <span>{t('header.ecSite')}</span>
              <div className="seq" />
              <ReactDropdown
                options={EC_SITES}
                onChange={setSiteToLoad}
                value={siteToLoad}
                placeholder=""
              />
            </div>
            <div className="ModalInputRow">
              <span>{t('header.scrapingType')}</span>
              <div className="seq" />
              <ReactDropdown
                options={SCRAPING_TYPE}
                onChange={setTypeToLoad}
                value={typeToLoad}
                placeholder=""
              />
            </div>
            <div className="ModalInputRow_SpaceAround">
              <Button
                onClick={() => {
                  setShowLoadModal(false);
                  onLoad(siteToLoad, typeToLoad);
                }}
                title={t('dialogBtnOK')}
              />
              <Button
                onClick={() => setShowLoadModal(false)}
                title={t('dialogBtnNo')}
              />
            </div>
          </Modal>
        ) : null
      }
      <div className="fileMenuWrap">
        <Button
          className="button boldButton"
          title={t('header.file')}
        />
        <div className="fileMenu">
          <Button
            className="button"
            disabled={!site || !type || loadType === 'loading'}
            onClick={() => setShowLoadModal(true)}
            title={t('header.load')}
          />
          <Button
            className="button"
            disabled={dataType !== type.value}
            onClick={onSave}
            title={t('header.save')}
          />
          <Button
            className="button"
            disabled={dataType !== type.value}
            onClick={onTest}
            title={t('header.test')}
          />
        </div>
      </div>
      <Button
        className="button boldButton"
        onClick={onLog}
        title={t('header.log')}
      />
      <Button
        className="button boldButton"
        onClick={onSetting}
        title={t('header.setting')}
      />
      <div className="seq big" />
      <div className="selector-container">
        <strong>
          {t('header.ecSite')}
          :
        </strong>
        &nbsp;
        {site.label}
      </div>
      <div className="seq" />
      <div className="selector-container">
        <strong>
          {t('header.scrapingType')}
          :
        </strong>
        &nbsp;
        {type.label}
      </div>
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
