/**
 * Editor for the Product Details scraper config.
 */
/* global window */

import React, { useEffect, useState } from 'react';
import { v4 as uuid } from 'uuid';
import { useGlobalState } from '@dr.pogodin/react-global-state';

import Button from '../Button';
import IconButton, { TYPES as IB_TYPES } from '../IconButton';
import getI18N from '../../i18nSetup';
import InputField from '../InputField';
import RowGroup from './RowGroup';
import SectionTitle from '../SectionTitle';

import './style.scss';
import { VALID_SCRAPING_TYPES } from '../../config/dropdown-list';
import { sendMessageToPage } from '../../services/utils';

export default function ProductDetailsEditor() {
  const [data, setData] = useGlobalState('data');
  const [expanded, setExpanded] = useState(true);
  const i18n = getI18N();

  useEffect(() => {
    window.onMessage = (message) => {
      if (window.messageListeners) {
        window.messageListeners.forEach((listener) => listener(message));
      }
      switch (message.action) {
        case 'currentUrl':
          return setData({ ...data, url: message.url });
        default:
      }
      return undefined;
    };
    return () => {
      delete window.onMessage;
    };
  }, [data]);

  if (!data || data.dataType !== VALID_SCRAPING_TYPES.PRODUCT_DETAIL) {
    return null;
  }

  return (
    <div className="ProductDetailsEditor">
      <div className="editor-row">
        <SectionTitle
          arrowUp={!expanded}
          onClick={() => setExpanded(!expanded)}
          title={i18n('editor.productDetailPage')}
        />
        {
          expanded ? (
            <>
              <InputField
                onChange={(value) => setData({ ...data, url: value })}
                title={i18n('editor.url')}
                value={data.url}
              />
              <Button
                onClick={() => sendMessageToPage({ action: 'currentUrl' })}
                title={i18n('editor.currentUrl')}
              />
            </>
          ) : null
        }
      </div>
      {
        expanded ? (
          <>
            <div className="currentUrlWarning">
              {i18n('editor.currentUrlCodePlaceholder')}
            </div>
            {
              data.productDetails.map((item, index) => (
                <RowGroup
                  group={item}
                  key={item.uuid}
                  updateGroup={(updatedGroup) => {
                    const groups = data.productDetails;
                    let newGroups = groups.slice(0, index);
                    if (updatedGroup) newGroups.push(updatedGroup);
                    newGroups = newGroups.concat(
                      groups.slice(index + 1, groups.length),
                    );
                    setData({ ...data, productDetails: newGroups });
                  }}
                />
              ))
            }
            <IconButton
              onClick={() => {
                const neu = [];
                neu.uuid = uuid();
                setData({
                  ...data,
                  productDetails: [...data.productDetails, neu],
                });
              }}
              topMargin
              title={i18n('editor.addItem')}
              type={IB_TYPES.PLUS}
            />
          </>
        ) : null
      }
    </div>
  );
}
