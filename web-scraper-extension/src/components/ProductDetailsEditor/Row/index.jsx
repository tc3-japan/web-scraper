/* global window */

import _ from 'lodash';
import CodeMirror from 'codemirror';
import 'codemirror/mode/javascript/javascript';
import React, { useEffect, useState } from 'react';
import ReactDropdown from 'react-dropdown';
import { SortableElement, SortableHandle } from 'react-sortable-hoc';
import { v4 as uuid } from 'uuid';
import { useGlobalState } from '@dr.pogodin/react-global-state';

import PT from 'prop-types';

import Button from '../../Button';
import Checkbox from '../../Checkbox';
import { PRODUCT_DETAILS_DROPDOWN } from '../../../config/dropdown-list';
import IconButton, { TYPES as IB_TYPES } from '../../IconButton';
import InputField from '../../InputField';
import { sendMessageToPage } from '../../../services/utils';

import './style.scss';

const Handle = SortableHandle(() => (
  <div className="handle">⋮</div>
));

function Row({
  row,
  updateRow,
}) {
  const { current: heap } = React.useRef({});

  const [i18n] = useGlobalState('i18n');
  const [expanded, setExpanded] = useState(false);
  const [highlightOwner, setHighlightOwner] = useGlobalState('highlightOwner');

  const [attrs, setAttrs] = useState();
  const [showAttrsTip, setShowAttrsTip] = useState(false);
  const [showRegexTip, setShowRegexTip] = useState(false);
  const [scriptMode, setScriptMode] = useState(false);

  const attrsTip = React.useMemo(() => {
    let res;
    if (attrs && showAttrsTip) {
      res = [];
      attrs.forEach((item) => {
        _.forOwn(item, (value, key) => {
          res.push(`${key}="${value}"`);
        });
      });
    }
    return res;
  }, [attrs, showAttrsTip]);

  const regexTip = React.useMemo(() => {
    let res;
    if (attrs && showRegexTip && row.regex) {
      res = [];
      const regex = new RegExp(row.regex);
      for (let i = 0; i < attrs.length; ++i) {
        const keys = Object.keys(attrs[i]);
        for (let j = 0; j < keys.length; ++j) {
          const key = keys[j];
          if (!row.attribute || row.attribute === key) {
            const value = attrs[i][key];
            const m = value && value.match(regex);
            if (m) res.push(m[0]);
            if (row.attribute || res.length === 3) { i = attrs.length; break; }
          }
        }
      }
    }
    return res;
  }, [attrs, showRegexTip, row.attribute, row.regex]);

  const attrsTipRef = React.useRef();

  const [scriptExecResult, setScriptExecResult] = useState();

  useEffect(() => {
    const onMessage = (message) => {
      switch (message.action) {
        case 'click': {
          if (highlightOwner === row.uuid) {
            updateRow({ ...row, selector: message.optimalSelector });
            sendMessageToPage({ action: 'stopInspector' });
            setHighlightOwner(null);
          }
          break;
        }
        case 'getAttributesResult': {
          if (message.opid === heap.opid) setAttrs(message.result);
          break;
        }
        case 'execScriptResult': {
          if (message.opid === heap.execScriptOpid) {
            setScriptExecResult(message.result.value);
          }
          break;
        }
        default:
      }
    };
    window.messageListeners.push(onMessage);
    return () => {
      window.messageListeners = window.messageListeners.filter(
        (item) => item !== onMessage,
      );
    };
  }, [highlightOwner, row, updateRow]);

  /**
   * Manages initialisation / teardown of the script editor. To work properly,
   * it has to be a stand-alone function which is persistent across component
   * re-renders by React.
   */
  const scriptEditorHandler = React.useCallback(
    (node) => {
      if (node && !heap.scriptEditor) {
        heap.scriptEditor = CodeMirror.fromTextArea(node, {
          lineNumbers: true,
          mode: 'javascript',
        });
        heap.scriptEditor.on('change', () => {
          const value = heap.scriptEditor.doc.getValue();
          updateRow({ ...row, script: value });
        });
      } else if (!node && heap.scriptEditor) {
        heap.scriptEditor.toTextArea();
        setScriptExecResult(null);
        delete heap.scriptEditor;
      }
    },
    [],
  );

  return (
    <div className="ProductDetailsEditor_Row">
      <Handle />
      <IconButton
        className="removeRowButton"
        onClick={updateRow}
        type={IB_TYPES.MINUS}
      />
      <div className="mainContent">
        <div className="mainContentRow">
          <ReactDropdown
            onChange={(option) => {
              updateRow({ ...row, item: option.value });
            }}
            options={PRODUCT_DETAILS_DROPDOWN}
            placeholder=""
            value={row.item}
          />
          <div className="seq" />
          <InputField
            disabled={scriptMode}
            onChange={(value) => updateRow({ ...row, selector: value })}
            title={i18n('editor.selector')}
            value={row.selector}
          />
          <Button
            disabled={scriptMode}
            type="selector"
            highlight={highlightOwner}
            onClick={() => {
              if (row.uuid === highlightOwner) {
                sendMessageToPage({ action: 'stopInspector' });
                setHighlightOwner(null);
              } else {
                sendMessageToPage({
                  action: 'startInspector',
                  selector: row.selector,
                  path: row.uuid,
                });
                setHighlightOwner(row.uuid);
              }
            }}
            path={row.uuid}
          />
          <div className="seq" />
          <IconButton
            onClick={() => setExpanded(!expanded)}
            title={i18n('editor.advanced')}
            type={expanded ? IB_TYPES.MINUS : IB_TYPES.PLUS}
          />
        </div>
        {
          expanded ? (
            <>
              <div className="mainContentRow">
                <Checkbox
                  label={i18n('editor.script')}
                  onChange={() => setScriptMode(!scriptMode)}
                  value={scriptMode}
                />
                <div className="seq" />
                <div className="auxBlock">
                  <InputField
                    disabled={scriptMode}
                    onChange={(attribute) => updateRow({ ...row, attribute })}
                    onBlur={(e) => {
                      // This hides the attributes tooltip, but only if the focus
                      // was not lost to one of the tooltip elements. In the later
                      // case removing it would interfere with handling the click
                      // there, thus we leave it up to that handler to close the
                      // tip.
                      const tipNode = attrsTipRef.current;
                      if (!tipNode || !tipNode.contains(e.relatedTarget)) {
                        setShowAttrsTip(false);
                      }
                    }}
                    onFocus={() => {
                      heap.opid = uuid();
                      setShowAttrsTip(true);
                      sendMessageToPage({
                        action: 'getAttributes',
                        selector: row.selector,
                        opid: heap.opid,
                      });
                    }}
                    title={i18n('editor.attribute')}
                    value={row.attribute}
                  />
                  {
                    attrsTip ? (
                      <div className="attrsTip" ref={attrsTipRef}>
                        {
                          attrsTip.length ? (
                            attrsTip.map((tip) => {
                              const fire = () => {
                                const value = tip.match(/[^=]*/)[0];
                                updateRow({
                                  ...row,
                                  attribute: value,
                                });
                                setShowAttrsTip(false);
                              };
                              return (
                                <div
                                  className="item"
                                  onClick={fire}
                                  onKeyPress={fire}
                                  key={tip}
                                  role="button"
                                  tabIndex={0}
                                >
                                  {`• ${tip}`}
                                </div>
                              );
                            })
                          ) : (
                            'No attributes'
                          )
                        }
                      </div>
                    ) : null
                  }
                </div>
                <div className="seq" />
                <div className="auxBlock">
                  <InputField
                    disabled={scriptMode}
                    onBlur={() => setShowRegexTip(false)}
                    onChange={(regex) => updateRow({ ...row, regex })}
                    onFocus={() => {
                      if (row.selector) {
                        heap.opid = uuid();
                        setShowRegexTip(true);
                        sendMessageToPage({
                          action: 'getAttributes',
                          selector: row.selector,
                          opid: heap.opid,
                        });
                      }
                    }}
                    title={i18n('editor.regex')}
                    value={row.regex}
                  />
                  {
                    regexTip ? (
                      <div className="regexTip" ref={attrsTipRef}>
                        {
                          regexTip.length ? (
                            regexTip.map((tip) => (
                              <div
                                className="item"
                                key={tip}
                              >
                                {`• ${tip}`}
                              </div>
                            ))
                          ) : (
                            'No Match'
                          )
                        }
                      </div>
                    ) : null
                  }
                </div>
              </div>
              {
                scriptMode ? (
                  <>
                    <textarea
                      className="scriptEditor"
                      onChange={_.noop}
                      ref={scriptEditorHandler}
                      value={row.script}
                    />
                    <div className="mainContentRow">
                      <Button
                        onClick={() => {
                          let { script } = row;
                          script = script.replace(
                            /\{(orderIndex|productIndex)\}/g,
                            1,
                          );
                          heap.execScriptOpid = uuid();
                          setScriptExecResult(null);
                          sendMessageToPage({
                            action: 'execScript',
                            script,
                            opid: heap.execScriptOpid,
                          });
                        }}
                        title={i18n('editor.check')}
                      />
                      {
                        scriptExecResult ? (
                          <div className="scriptExecResult">
                            <span className="label">
                              Script Execution Result:
                            </span>
                            <br />
                            {scriptExecResult}
                          </div>
                        ) : null
                      }
                    </div>
                  </>
                ) : null
              }
            </>
          ) : null
        }
      </div>
    </div>
  );
}

Row.propTypes = {
  row: PT.shape({
    attribute: PT.string,
    item: PT.string,
    regex: PT.string,
    selector: PT.string,
    script: PT.string,
    uuid: PT.string,
  }).isRequired,
  updateRow: PT.func.isRequired,
};

export default SortableElement(Row);
