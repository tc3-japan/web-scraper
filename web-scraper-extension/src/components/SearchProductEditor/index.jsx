/**
 * Editor for the Product Details scraper config.
 */
/* global window */

import React from 'react';
import Swal from 'sweetalert2';
import { v4 as uuid } from 'uuid';
import { useGlobalState } from '@dr.pogodin/react-global-state';

import AttributeField from '../AttributeField';
import Checkbox from '../Checkbox';
import Button from '../Button';
import InputField from '../InputField';
import JsEditor from '../JsEditor';
import RegexField from '../RegexField';
import SectionTitle from '../SectionTitle';
import TargetButton from '../TargetButton';
import { VALID_SCRAPING_TYPES } from '../../config/dropdown-list';
import {
  GET_COMMON_PARENT_MODES,
  getCommonClass,
  getCommonParent,
  joinSelectors,
  sendMessageToPage,
} from '../../services/utils';

import './style.scss';

export default function SearchProductEditor() {
  const { current: heap } = React.useRef({});

  const [dataUuid] = useGlobalState('dataUuid');

  if (!heap.uuid) {
    heap.uuid = {
      excludedSelector: uuid(),
      parentSelector: uuid(),
      selector: uuid(),
    };
    heap.getClassPromises = {};
  }

  const [data, setData] = useGlobalState('data');

  const scriptMode = data && data.isScript;
  const setScriptMode = (yes) => setData({ ...data, isScript: yes });

  const [highlightOwner, setHighlightOwner] = useGlobalState('highlightOwner');
  if (!highlightOwner) heap.selectionsUuid = null;

  const [expanded, setExpanded] = React.useState(true);
  const [i18n] = useGlobalState('i18n');

  React.useEffect(() => {
    setExpanded(true);
  }, [dataUuid]);

  const getClass = (selector) => {
    const promiseId = uuid();
    sendMessageToPage({ action: 'getClass', promiseId, selector });
    return new Promise((resolve) => {
      heap.getClassPromises[promiseId] = resolve;
    });
  };

  // Handles the 'click' message from the page.
  const onClickMessage = async (message) => {
    try {
      // The component is not expecting any 'click' events, thus ignore it.
      if (highlightOwner !== heap.uuid.excludedSelector
      && highlightOwner !== heap.uuid.parentSelector
      && highlightOwner !== heap.uuid.selector) return;

      // Memorizing the message payload.
      if (highlightOwner !== heap.selectionsUuid) {
        heap.selectionsUuid = highlightOwner;
        heap.selections = [message.path];
      } else heap.selections.push(message.path);

      // Ready to calculate and set the selector.
      if (heap.selections.length === 2) {
        // As a side effect, it will also throw if different elements are
        // selected.
        let groupSelector = getCommonParent(
          heap.selections[0],
          heap.selections[1],
          GET_COMMON_PARENT_MODES.FROM_END,
        );

        // Parent (group) selector should be updated.
        if (highlightOwner === heap.uuid.parentSelector
        || !data.groupSelector) {
          const gl = groupSelector.split(' > ').length;

          let p1 = heap.selections[0].split(' > ')[gl - 1];
          p1 = p1.match(/(:.*)/);
          p1 = p1 && p1[1] ? p1[1] : '';
          p1 = groupSelector + p1;
          p1 = await getClass(p1);

          let p2 = heap.selections[1].split(' > ')[gl - 1];
          p2 = p2.match(/(:.*)/);
          p2 = p2 && p2[1] ? p2[1] : '';
          p2 = groupSelector + p2;
          p2 = await getClass(p2);

          groupSelector += getCommonClass([p1, p2]);
        } else ({ groupSelector } = data);

        const gl = groupSelector.split(' > ').length;
        let child = heap.selections[0].split(' > ').slice(gl).join(' > ');
        child += getCommonClass([
          await getClass(heap.selections[0]),
          await getClass(heap.selections[1]),
        ]);

        let { excludedSelector, selector } = data;
        if (highlightOwner === heap.uuid.excludedSelector) {
          excludedSelector = child;
        } else if (highlightOwner === heap.uuid.selector) {
          selector = child;
        }
        setData({
          ...data,
          excludedSelector,
          groupSelector,
          selector,
        });
        sendMessageToPage({ action: 'stopInspector' });
        setHighlightOwner(null);
      }
    } catch (error) {
      sendMessageToPage({ action: 'stopInspector' });
      setHighlightOwner(null);
      Swal.fire({
        html: error.message.replace(/\n/g, '<br />'),
        showConfirmButton: true,
        confirmButtonText: i18n('dialogBtnOK'),
      });
    }
  };

  React.useEffect(() => {
    const onMessage = (message) => {
      switch (message.action) {
        case 'click': return onClickMessage(message);
        default: return undefined;
      }
    };
    window.messageListeners.push(onMessage);
    return () => {
      window.messageListeners = window.messageListeners.filter(
        (item) => item !== onMessage,
      );
    };
  }, [data, highlightOwner]);

  React.useEffect(() => {
    window.onMessage = (message) => {
      if (window.messageListeners) {
        window.messageListeners.forEach((listener) => listener(message));
      }
      switch (message.action) {
        case 'currentUrl':
          return setData({ ...data, url: message.url });
        case 'getClass': {
          const { promiseId } = message;
          const promise = heap.getClassPromises[promiseId];
          if (promise) {
            promise(message.class);
            delete heap.getClassPromises[promiseId];
          }
          break;
        }
        default:
      }
      return undefined;
    };
    return () => {
      delete window.onMessage;
    };
  }, [data]);

  if (!data || data.dataType !== VALID_SCRAPING_TYPES.SEARCH_PRODUCT) {
    return null;
  }

  return (
    <div className="SearchProductEditor">
      <div className="editor-row">
        <SectionTitle
          arrowUp={!expanded}
          onClick={() => setExpanded(!expanded)}
          title={i18n('editor.searchProductPage')}
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
              {i18n('editor.currentUrlWordPlaceholder')}
            </div>
            <div className="mainContentRow">
              <InputField
                className="alignedInputFieldL"
                onChange={
                  (groupSelector) => setData({ ...data, groupSelector })
                }
                title={i18n('editor.parentSelector')}
                value={data.groupSelector}
              />
              <TargetButton
                selector={data.groupSelector}
                uuid={heap.uuid.parentSelector}
              />
            </div>
            <div className="mainContentRow">
              <InputField
                className="alignedInputFieldL"
                disabled={scriptMode}
                onChange={(selector) => setData({ ...data, selector })}
                title={i18n('editor.selector')}
                value={data.selector}
              />
              <TargetButton
                disabled={scriptMode}
                selector={joinSelectors(data.groupSelector, data.selector)}
                uuid={heap.uuid.selector}
              />
              <div className="seq" />
              <AttributeField
                attribute={data.attribute}
                disabled={scriptMode}
                inputClassName="alignedInputFieldR"
                onChange={(attribute) => setData({ ...data, attribute })}
                selector={joinSelectors(data.groupSelector, data.selector)}
                tipClassName="alignedTipR"
              />
            </div>
            <div className="mainContentRow">
              <InputField
                className="alignedInputFieldL"
                disabled={scriptMode}
                onChange={
                  (excludedSelector) => setData({ ...data, excludedSelector })
                }
                title={i18n('editor.excludedSelector')}
                value={data.excludedSelector}
              />
              <TargetButton
                disabled={scriptMode}
                selector={
                  joinSelectors(data.groupSelector, data.excludedSelector)
                }
                uuid={heap.uuid.excludedSelector}
              />
              <div className="seq" />
              <RegexField
                attribute={data.attribute}
                disabled={scriptMode}
                inputClassName="alignedInputFieldR"
                onChange={(regex) => setData({ ...data, regex })}
                regex={data.regex}
                selector={
                  joinSelectors(data.groupSelector, data.selector)
                }
                tipClassName="alignedTipR"
              />
            </div>
            <div className="mainContentRow">
              <Checkbox
                label={i18n('editor.script')}
                onChange={() => setScriptMode(!scriptMode)}
                value={scriptMode}
              />
            </div>
            {
              scriptMode ? (
                <JsEditor
                  onChange={(script) => setData({ ...data, script })}
                  script={data.script}
                />
              ) : null
            }
          </>
        ) : null
      }
    </div>
  );
}
